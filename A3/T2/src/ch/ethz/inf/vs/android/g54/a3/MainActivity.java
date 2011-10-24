package ch.ethz.inf.vs.android.g54.a3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	final String SERVER = "vswot.inf.ethz.ch";
	
	// CHECK: In JSON, the Key of an object doesn't need to be quoted if it's
	//        not a keyword or invalid identifier since maybe the server
	//        requires this (the example slides have them quoted
	final String CMD_REG = "{cmd:\"register\",user:\"%s\"}";
	// answer: {"index":3,"time_vector":{"3":0,"2":70,"1":71,"0":74},"success":"reg_ok"}
	final String CMD_GET_CLIENTS = "{cmd:\"get_clients\"}";
	// answer: {"clients":{"/129.132.75.130":"QuestionBot","/129.132.252.221":"AnswerBot","/77.58.228.17":"willi"}}
	final String CMD_MSG = "{cmd:\"message\",text:\"%s\",time_vector:{%s}";
	final String CMD_DEREG = "{cmd:\"deregister\"}";
	// answer {"success":"dreg_ok"}
	
	DatagramSocket sockCmd = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        try {
        	InetAddress adrServer = InetAddress.getByName(SERVER);
			sockCmd = new DatagramSocket(4000);
			sockCmd.setSoTimeout(10 * 1000); // read timeout of 10s
			sockCmd.connect(adrServer, 4000); // set default socket for send/recv

			byte[] req = String.format(CMD_REG, "AnonUser42").getBytes();
			DatagramPacket pkt = new DatagramPacket(req, req.length);
			sockCmd.send(pkt);
			
			byte[] ans = new byte[128];
			pkt = new DatagramPacket(ans, ans.length);
			sockCmd.receive(pkt); // blocking read
			String a = String.valueOf(ans);
			assert (a.contains("reg_ok")); // TODO: actually parse the JSON object

        } catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
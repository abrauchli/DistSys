package ch.ethz.inf.vs.android.g54.a3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	final String SERVER = "vswot.inf.ethz.ch";

	DatagramSocket sockCmd = null;

	private String cmdReg(String user) {
		JSONObject o = new JSONObject();
		try {
			o.put("cmd", "register");
			o.put("user", user);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o.toString();
		// answer:
		// {"index":3,"time_vector":{"3":0,"2":70,"1":71,"0":74},"success":"reg_ok"}
	}

	private String cmdDereg() {
		JSONObject o = new JSONObject();
		try {
			o.put("cmd", "deregister");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o.toString();
		// answer {"success":"dreg_ok"}
	}

	private String cmdMsg(String text, Map<String, Integer> clocks) {
		JSONObject o = new JSONObject();
		JSONObject t = new JSONObject(clocks);
		try {
			o.put("cmd", "message");
			o.put("text", text);
			o.put("time_vector", t);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o.toString();
	}

	private String cmdGetClients() {

		JSONObject o = new JSONObject();
		try {
			o.put("cmd", "get_clients");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return o.toString();
		// answer:
		// {"index":3,"time_vector":{"3":0,"2":70,"1":71,"0":74},"success":"reg_ok"}
	}

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

			byte[] req = cmdReg("AnonUser42").getBytes();
			DatagramPacket pkt = new DatagramPacket(req, req.length);
			sockCmd.send(pkt);

			byte[] ans = new byte[128];
			pkt = new DatagramPacket(ans, ans.length);
			sockCmd.receive(pkt); // blocking read
			JSONObject o = new JSONObject(String.valueOf(ans));
			assert (o.get("success").equals("reg_ok"));
			String index = (String) o.get("index");
			HashMap<String, Integer> clocks = new HashMap<String, Integer>();
			JSONObject v = o.getJSONObject("time_vector");
			@SuppressWarnings("unchecked")
			Iterator<String> i = v.keys();
			while (i.hasNext()) {
				String c = i.next();
				clocks.put(c, (Integer) o.get(c));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
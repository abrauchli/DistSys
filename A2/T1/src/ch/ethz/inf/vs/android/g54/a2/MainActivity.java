package ch.ethz.inf.vs.android.g54.a2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	public void onBtnHtmlRawClick (View v) {
		Socket s;
		try {
			s = new Socket("vswot.inf.ethz.ch", 8081);			
			
			DataOutputStream outToServer = new DataOutputStream(s.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			String message = "GET /sunspots/Spot1/sensors/temperature HTTP/1.1\nHost: vswot.inf.ethz.ch\n\n";
			outToServer.writeBytes(message);
			outToServer.flush();
			
			String response = inFromServer.readLine();
			TextView t;
			t = (TextView) findViewById(R.id.txt_type_of_request);
			t.setText(R.string.btn_html_raw);
			t = (TextView) findViewById(R.id.txt_response);
			t.setText(response.toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onBtnHtmlApacheClick (View v) {
		TextView t;
		t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.btn_html_apache);
		t = (TextView) findViewById(R.id.txt_response);
		
		// TODO: Replace
		t.setText(R.string.txt_empty);
	}
	
	public void onBtnJsonRawClick (View v) {
		TextView t;
		t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.btn_json_raw);
		t = (TextView) findViewById(R.id.txt_response);

		// TODO: Replace
		t.setText(R.string.txt_empty);
	}
	
	public void onBtnJsonParsedClick (View v) {
		TextView t;
		t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.btn_json_parsed);
		t = (TextView) findViewById(R.id.txt_response);

		// TODO: Replace
		t.setText(R.string.txt_empty);
	}

}

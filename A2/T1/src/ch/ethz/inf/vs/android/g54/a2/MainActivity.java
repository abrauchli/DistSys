package ch.ethz.inf.vs.android.g54.a2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	public void onBtnHtmlRawClick (View v) {
		try {
			Socket s = new Socket("vswot.inf.ethz.ch", 8081);			
			
			DataOutputStream outToServer = new DataOutputStream(s.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			String message = "GET /sunspots/Spot1/sensors/temperature HTTP/1.1\r\nHost: vswot.inf.ethz.ch\r\n\r\n";
			outToServer.writeBytes(message);
			outToServer.flush();
			
			StringBuilder sb = new StringBuilder();
			
			String line = inFromServer.readLine();
			
			//Comment regarding performance
			while (line != null) {
				sb.append(line + "\n");
				line = inFromServer.readLine();
			}
			outToServer.close();
			inFromServer.close();
			
			TextView t;
			t = (TextView) findViewById(R.id.txt_type_of_request);
			t.setText(R.string.btn_html_raw);
			t = (TextView) findViewById(R.id.txt_response);
			
			t.setText(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onBtnHtmlApacheClick (View v) {		
		HttpClient httpclient = new DefaultHttpClient();
		String responseBody = "";
		try {
			HttpGet httpGet = new HttpGet("http://vswot.inf.ethz.ch:8081/sunspots/Spot1/sensors/temperature");
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpGet, responseHandler);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		
		TextView t;
		t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.btn_html_apache);
		t = (TextView) findViewById(R.id.txt_response);
		t.setText(responseBody);
	}
	
	public void onBtnJsonRawClick (View v) {
		HttpClient httpclient = new DefaultHttpClient();
		String responseBody = "";
		try {
			HttpGet httpget = new HttpGet("http://vswot.inf.ethz.ch:8081/sunspots/Spot1/sensors/temperature");
			BasicHeader header = new BasicHeader("Accept", "application/json");
			httpget.addHeader(header);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpget, responseHandler);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		
		TextView t;
		t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.btn_json_raw);
		t = (TextView) findViewById(R.id.txt_response);
		t.setText(responseBody);
	}
	
	public void onBtnJsonParsedClick (View v) {
		HttpClient httpclient = new DefaultHttpClient();
		String responseBody = "";
		double temperature = 0;
		
		try {
			HttpGet httpget = new HttpGet("http://vswot.inf.ethz.ch:8081/sunspots/Spot1/sensors/temperature");
			BasicHeader header = new BasicHeader("Accept", "application/json");
			httpget.addHeader(header);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpget, responseHandler);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		
		try {
			JSONObject jsonObject = new JSONObject(responseBody);
			temperature = jsonObject.getDouble("value");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TextView t;
		t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.btn_json_parsed);
		t = (TextView) findViewById(R.id.txt_response);
		t.setText("Temperature of Sensor 1: " + temperature + "° C");
	}

}

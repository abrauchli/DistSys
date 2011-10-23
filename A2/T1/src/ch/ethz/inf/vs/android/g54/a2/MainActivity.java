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
			
			// Create the in and out streams from the socket
			DataOutputStream outToServer = new DataOutputStream(s.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			// HTTP Query
			String message = "GET /sunspots/Spot1/sensors/temperature HTTP/1.1\r\n"
					+ "Host: vswot.inf.ethz.ch\r\n"
					+ "\r\n";
			outToServer.writeBytes(message);
			outToServer.flush();
			
			// Use a stringbuilder as it's much quicker than freeing/reallocating long strings
			StringBuilder sb = new StringBuilder();
			String line = inFromServer.readLine();

			while (line != null) {
				sb.append(line + "\n");
				// Not sure why, but reading is crazy slow. Esp. compared to the apache client
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
			// something with the socket went foobar
			e.printStackTrace();
		}
	}
	
	public void onBtnHtmlApacheClick (View v) {		
		HttpClient httpclient = new DefaultHttpClient();
		String responseBody = "";
		try {
			// request temperature from sensor
			HttpGet httpGet = new HttpGet("http://vswot.inf.ethz.ch:8081/sunspots/Spot1/sensors/temperature");
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpGet, responseHandler);
		} catch (Exception e) {
			// something with the request went wrong
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}

		// Set the values in the GUI
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
			// request temperature from sensor
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
			// request temperature from sensor
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
			// Result is probably not JSON or value is not double
			e.printStackTrace();
		}
		
		TextView t;
		t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.btn_json_parsed);
		t = (TextView) findViewById(R.id.txt_response);
		t.setText("Temperature of Sensor 1: " + temperature + "° C");
	}

}

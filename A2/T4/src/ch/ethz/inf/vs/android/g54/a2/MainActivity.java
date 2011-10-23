package ch.ethz.inf.vs.android.g54.a2;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	ArrayList<Double> temperatures;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        temperatures = new ArrayList<Double>();
        
        /* Get temperature values during the next ten seconds */
        long timestamp = System.currentTimeMillis();
        while (System.currentTimeMillis() - timestamp <= 10000) {
			if ((System.currentTimeMillis() - timestamp) % 1000 == 0) {
				temperatures.add(getTemperature());
			}
		}
    }
    
    public void onNativeLibraryClick (View v) {
    	Toast.makeText(this, R.string.txt_not_implemented, Toast.LENGTH_SHORT).show();
    }
    
    public void onGoogleChartClick (View v) {
    	/* Create HTML file containing the code for the chart. */
    	String htmlFile = 
    			"<html>\n" +
    			"<head>\n" +
    			"<script type='text/javascript' src='https://www.google.com/jsapi'></script>\n" +
    			"<script type='text/javascript'>\n" +
    			"google.load('visualization', '1', {packages:['corechart']});\n" +
    			"google.setOnLoadCallback(drawChart);" +
    			"function drawChart() {" +
    	        "var data = new google.visualization.DataTable();" +
    	        "data.addColumn('number', 'Temperature');\n";
    	for (int i = 0; i < temperatures.size(); i++) {
			htmlFile = htmlFile + "data.addRow([" + temperatures.get(i) + "]);\n\n";
		}
    	htmlFile = htmlFile + "var chart = new google.visualization.LineChart(document.getElementById('chart_div'));\n"
    			+ "chart.draw(data, {width: 400, height: 240, title: 'Temperatures from Spot 1'});\n"
    			+ "}\n"
    			+ "</script>\n"
    			+ "</head>\n\n"
    			+ "<body>\n"
    			+ "<div id='chart_div'></div>\n"
    			+ "</body>\n"
    			+ "</html>";
    	
    	/* Switch to the view displaying the chart. */
    	setContentView(R.layout.google_chart);
    	
    	/* Display the chart. */
    	WebView mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadData(htmlFile, "text/html", "utf-8");
    }
    
    /** Returns a temperature value. */
    public double getTemperature() {
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
		return temperature;
    }
}
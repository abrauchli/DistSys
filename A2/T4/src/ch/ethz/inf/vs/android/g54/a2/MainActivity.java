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
import android.webkit.WebSettings.PluginState;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	ArrayList<Double> temperatures;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        temperatures = new ArrayList<Double>();
        
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
    	StringBuilder vals = new StringBuilder();
    	if (temperatures.size() > 0) {
			vals.append(temperatures.get(0));
    	}
    	for (int i = 1; i < temperatures.size(); i++) {
    		vals.append(',');
			vals.append(temperatures.get(i));
		}
    	
    	setContentView(R.layout.google_chart);
    	
    	WebView mWebView = (WebView) findViewById(R.id.webView);
    	mWebView.getSettings().setPluginState(PluginState.ON_DEMAND);
    	mWebView.getSettings().setJavaScriptEnabled(true);	
        mWebView.loadUrl("http://chart.apis.google.com/chart"
        	   + "?chxl=0:|°C"
        	   + "&chs=400x240"
        	   + "&cht=lc"
        	   + "&chco=3072F3"
        	   + "&chd=t:" + vals.toString()
        	   + "&chdlp=b"
        	   + "&chls=2,4,1"
        	   + "&chma=5,5,5,25"
        	   + "&chtt=Temparture");
    }
    
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
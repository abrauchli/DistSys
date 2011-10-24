package ch.ethz.inf.vs.android.g54.a2;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	// Transport object used throughout the class
	HttpTransportSE httpTransport = new HttpTransportSE(
			"http://vswot.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice?wsdl");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// enable debug mode to intercept responseDump
		httpTransport.debug = true;
		setContentView(R.layout.main);
	}

	/**
	 * Get the temperature from the sensor and display the raw XML response
	 * @param v The view (Button) that was clicked
	 */
	public void onSoapRawClick(View v) {

		// create the request to getSpot ..
		SoapObject request = new SoapObject(
				"http://webservices.vslecture.vs.inf.ethz.ch/",
				"getSpot");
		// .. and set the parameter to retrieve Spot3
		request.addProperty("id", "Spot3");

		// embed the request in a soap envelope
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);

		try {
			// execute the call on given namespace
			httpTransport.call(
					"http://webservices.vslecture.vs.inf.ethz.ch/SunSPOTWebservice/getSpot",
					envelope);
		} catch (Exception e) {
			e.printStackTrace();
		}


		TextView t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.txt_soap_raw);

		t = (TextView) findViewById(R.id.txt_response);
		t.setText(httpTransport.responseDump);
	}

	/**
	 * Get the temperature from the sensor
	 * @param v The view (Button) that was clicked
	 */
	public void onSoapParsedClick(View v) {
		TextView t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.txt_soap_parsed);

		// create the request to getSpot ..
		SoapObject request = new SoapObject(
				"http://webservices.vslecture.vs.inf.ethz.ch/",
				"getSpot");
		// .. and set the parameter to retrieve Spot3
		request.addProperty("id", "Spot3");

		// embed the request in a soap envelope
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);

		SoapObject response = null;
		String temp = "";
		try {
			// execute the call on given namespace
			httpTransport.call(
					"http://webservices.vslecture.vs.inf.ethz.ch/SunSPOTWebservice/getSpot",
					envelope);
			response = (SoapObject) envelope.getResponse();
			// get the temperature node
			temp = response.getPropertyAsString("temperature");
		} catch (Exception e) {
			e.printStackTrace();
		}

		t = (TextView) findViewById(R.id.txt_response);
		t.setText("Temperature of Sensor 3: " + temp + "° C");
	}

}

package ch.ethz.inf.vs.android.g54.a2;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	HttpTransportSE httpTransport = new HttpTransportSE(
			"http://vswot.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice?wsdl");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		httpTransport.debug = true;
		setContentView(R.layout.main);
	}

	public void onSoapRawClick(View v) {
		SoapObject request = new SoapObject(
				"http://webservices.vslecture.vs.inf.ethz.ch/",
				"getDiscoveredSpots");

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);

		try {
			httpTransport.call(
				"http://webservices.vslecture.vs.inf.ethz.ch/SunSPOTWebservice/getDiscoveredSpotsRequest",
				envelope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TextView t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.txt_soap_raw);

		t = (TextView) findViewById(R.id.txt_response);
		t.setText(httpTransport.responseDump);
	}

	public void onXmlRawClick(View v) {
		SoapObject request = new SoapObject(
				"http://webservices.vslecture.vs.inf.ethz.ch/",
				"getDiscoveredSpots");

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);

		try {
			httpTransport.call(
					"http://webservices.vslecture.vs.inf.ethz.ch/SunSPOTWebservice/getDiscoveredSpotsRequest",
					envelope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TextView t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.txt_xml_raw);

		t = (TextView) findViewById(R.id.txt_response);
		// TODO replace
		t.setText(R.string.txt_empty_string);
	}

	public void onSoapParsedClick(View v) {
		TextView t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.txt_soap_parsed);

		t = (TextView) findViewById(R.id.txt_response);
		// TODO replace
		t.setText(R.string.txt_empty_string);
	}

	public void onXmlParsedClick(View v) {
		SoapObject request = new SoapObject(
				"http://webservices.vslecture.vs.inf.ethz.ch/",
				"getSpot");
		request.addProperty("id", "Spot3");

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);

		SoapObject response = null;
		double temp = 0.0;
		try {
			httpTransport.call(
					"http://webservices.vslecture.vs.inf.ethz.ch/SunSPOTWebservice/getSpot",
					envelope);
			response = (SoapObject) envelope.getResponse();
			/* temp = */ response.getProperty("temperature")/*.value*/; // What class is that?
		} catch (Exception e) {
			e.printStackTrace();
		}

		TextView t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.txt_xml_parsed);

		t = (TextView) findViewById(R.id.txt_response);
		t.setText(Double.toString(temp));
	}

}

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	public void onSoapRawClick (View v) {
		SoapObject request = new SoapObject("http://webservices.vslecture.vs.inf.ethz.ch/", "getDiscoveredSpots");
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		
		HttpTransportSE httpTransport = new HttpTransportSE("http://vswot.inf.ethz.ch:8080/SunSPOTWebServices/SunSPOTWebservice?wsdl");
		httpTransport.debug = true;
		
		try {
			httpTransport.call("http://webservices.vslecture.vs.inf.ethz.ch/SunSPOTWebservice/getDiscoveredSpotsRequest", envelope);
			SoapObject response = (SoapObject) envelope.getResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TextView t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.txt_soap_raw);
		
		t = (TextView) findViewById(R.id.txt_response);
		//TODO replace
		t.setText(R.string.txt_empty_string);
	}
	
	public void onXmlRawClick (View v) {
		TextView t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.txt_xml_raw);
		
		t = (TextView) findViewById(R.id.txt_response);
		//TODO replace
		t.setText(R.string.txt_empty_string);
	}
	
	public void onSoapParsedClick (View v) {
		TextView t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.txt_soap_parsed);
		
		t = (TextView) findViewById(R.id.txt_response);
		//TODO replace
		t.setText(R.string.txt_empty_string);
	}

	public void onXmlParsedClick (View v) {
		TextView t = (TextView) findViewById(R.id.txt_type_of_request);
		t.setText(R.string.txt_xml_parsed);
		
		t = (TextView) findViewById(R.id.txt_response);
		//TODO replace
		t.setText(R.string.txt_empty_string);
	}
	
}

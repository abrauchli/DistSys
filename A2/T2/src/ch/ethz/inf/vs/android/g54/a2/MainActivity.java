package ch.ethz.inf.vs.android.g54.a2;

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

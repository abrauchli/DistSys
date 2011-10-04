package ch.ethz.inf.vs.android.g54.a1;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SensorsMain extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SensorManager m = (SensorManager) getSystemService(SENSOR_SERVICE);
        final List<Sensor> sensors = m.getSensorList(Sensor.TYPE_ALL);
        String[] sens = new String[sensors.size()];
        for (int i=0; i < sensors.size(); ++i) {
        	sens[i] = sensors.get(i).getName();
        }
        ListView lv = (ListView) findViewById(R.id.lv_sensorlist);
        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.li_sensor, sens));
        final SensorsMain me = this;
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				sensors.get(arg2);
				Intent sd = new Intent(me, SensorsDetail.class);
				sd.putExtra("sensor", ((String) ((TextView)arg1).getText()));
				me.startActivity(sd);
			}
		});
    }
}
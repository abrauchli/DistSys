package ch.ethz.inf.vs.android.g54.a1;

import java.util.List;

import ch.ethz.inf.vs.android.g99.a1.R;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SensorsMain extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SensorManager m = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = m.getSensorList(Sensor.TYPE_ALL);
        String[] sens = new String[sensors.size()];
        for (int i=0; i < sensors.size(); ++i) {
        	sens[i] = sensors.get(i).getName();
        }
        ListView lv = (ListView) findViewById(R.id.lv_sensorlist);
        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.li_sensor, sens));
    }
}
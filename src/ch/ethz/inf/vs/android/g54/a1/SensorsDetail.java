package ch.ethz.inf.vs.android.g54.a1;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

public class SensorsDetail extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String sensor = (String) this.getIntent().getExtras().get("sensor");
		
        SensorManager m = (SensorManager) getSystemService(SENSOR_SERVICE);
        final List<Sensor> sensors = m.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : sensors) {
        	if (s.getName().equals(sensor)) {
        		// do something with the sensor
        		break;
        	}
        }
	}
}
package ch.ethz.inf.vs.android.g54.a1;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SensorsDetail extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor);
		String sensor = (String) this.getIntent().getExtras().get("sensor");
		
        SensorManager m = (SensorManager) getSystemService(SENSOR_SERVICE);
        final List<Sensor> sensors = m.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : sensors) {
        	if (s.getName().equals(sensor)) {
                ListView lv = (ListView) findViewById(R.id.lv_sensor);
                String[] sens = {
                		"Name: " + s.getName(),
                		"Version: " + s.getVersion(),
                		"Type: " + getSensorType(s.getType()),
                		"MaxRange: " + s.getMaximumRange(),
                		"Resolution: " + s.getResolution(),
                		"Power: " + s.getPower() + "mA"
                };
                lv.setAdapter(new ArrayAdapter<String>(this, R.layout.li_sensor, sens));
        		break;
        	}
        }
	}
    
    private String getSensorType(int t) {
    	switch(t) {
    	case Sensor.TYPE_ACCELEROMETER: return "accelerometer";
    	case Sensor.TYPE_GYROSCOPE: return "gyroscope";
    	case Sensor.TYPE_LIGHT: return "light";
    	case Sensor.TYPE_MAGNETIC_FIELD: return "magnetic field";
    	case Sensor.TYPE_PRESSURE: return "pressure";
    	case Sensor.TYPE_PROXIMITY: return "proximity";
    	case Sensor.TYPE_TEMPERATURE: return "temperature";
		default: return "Unknown type " + t;
    	}
    }
}
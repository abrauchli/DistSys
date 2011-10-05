package ch.ethz.inf.vs.android.g54.a1;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SensorsDetail extends Activity implements SensorEventListener {
	private ArrayList<String> sens = new ArrayList<String>(10);
	private ListView listView = null;
	private final int SENS_INDEX = 6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensor);
		String sensor = (String) this.getIntent().getExtras().get("sensor");

		SensorManager m = (SensorManager) getSystemService(SENSOR_SERVICE);
		final List<Sensor> sensors = m.getSensorList(Sensor.TYPE_ALL);
		for (Sensor s : sensors) {
			if (s.getName().equals(sensor)) {
				m.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
				listView = (ListView) findViewById(R.id.lv_sensor);
				sens.add("Name: " + s.getName());
				sens.add("Version: " + s.getVersion());
				sens.add("Type: " + getSensorType(s.getType()));
				sens.add("MaxRange: " + s.getMaximumRange());
				sens.add("Resolution: " + s.getResolution());
				sens.add("Power: " + s.getPower() + "mA");
				listView.setAdapter(new ArrayAdapter<String>(this, R.layout.li_sensor, sens));
				break;
			}
		}
	}

	private String getSensorType(int t) {
		switch (t) {
		case Sensor.TYPE_ACCELEROMETER:
			return "accelerometer";
		case Sensor.TYPE_GYROSCOPE:
			return "gyroscope";
		case Sensor.TYPE_LIGHT:
			return "light";
		case Sensor.TYPE_MAGNETIC_FIELD:
			return "magnetic field";
		case Sensor.TYPE_PRESSURE:
			return "pressure";
		case Sensor.TYPE_PROXIMITY:
			return "proximity";
		case Sensor.TYPE_TEMPERATURE:
			return "temperature";
		default:
			return "Unknown type " + t;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent evt) {
		sens.subList(SENS_INDEX, sens.size()).clear();
		for (int i = 0; i < evt.values.length; ++i) {
			sens.add("Value " + i + ": " + evt.values[i]);
		}
		((ArrayAdapter<?>)listView.getAdapter()).notifyDataSetChanged();
	}
}
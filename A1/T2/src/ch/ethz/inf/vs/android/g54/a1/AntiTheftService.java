package ch.ethz.inf.vs.android.g54.a1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class AntiTheftService extends Service implements SensorEventListener {

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	private NotificationManager mNM;
	Sensor mSensor;

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		AntiTheftService getService() {
			return AntiTheftService.this;
		}
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		initSensor();
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// Tell the user we stopped.
		Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private void initSensor() {
		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sm.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	private void ringAlarm() {
		Notification notification = new Notification();
		notification.tickerText = "THIEF ALARM";
		notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		mNM.notify(0, notification);
	}

	@Override
	public void onAccuracyChanged(Sensor s, int acc) {
	}

	@Override
	public void onSensorChanged(SensorEvent evt) {
		// TODO
	}

}

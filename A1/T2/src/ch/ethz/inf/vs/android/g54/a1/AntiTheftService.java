package ch.ethz.inf.vs.android.g54.a1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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
	private Notification notification;

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
		Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
		mNM.cancel(0);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/** Init the alarm activating sensor */
	private void initSensor() {
		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sm.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	/** Sets an unclearable message */
	private void ringAlarm() {
		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		sm.unregisterListener(this, mSensor);
		
		int icon = R.drawable.icon;

		notification = new Notification(icon, "Anti Theft Service started", System.currentTimeMillis());
		notification.tickerText = "Anti Theft Service started";
		notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		
		Context context = getApplicationContext();
		CharSequence contentTitle = "Anti Theft Service";
		CharSequence contentText = "Service is active.";
		Intent notificationIntent = new Intent(this, AntiTheftService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		mNM.notify(0, notification);
	}

	@Override
	public void onAccuracyChanged(Sensor s, int acc) {
	}

	/** The timestamp of the first event above the threshold */
	private long timestamp;
	/** Whether we're in the grace period before alarm activation */
	private boolean trigger = false;
	/** The current threshold values that will trigger the (pre-)alarm period */
	private float[] threshold = { 2.0f, 2.0f, 11.0f };

	@Override
	public void onSensorChanged(SensorEvent evt) {
		// the time difference between the first event above the threshold and the current one
		long diff = evt.timestamp - timestamp;
		for (int i = 0; i < evt.values.length; ++i) {
			if (Math.abs(evt.values[i]) > threshold[i]) {
				if (trigger) {
					if (diff > 3141592653l /* 3.14s */) {
						ringAlarm();
					}
				} else {
					trigger = true;
					threshold = new float[] { 1.0f, 1.0f, 10.0f };
					timestamp = evt.timestamp;
					diff = 0;
				}
				break;
			}
		}
		if (trigger && diff > 10 * 1000000000l) {
			// restart after 10s of quiet
			trigger = false;
			threshold = new float[] { 2.0f, 2.0f, 11.0f };
		}
	}

}

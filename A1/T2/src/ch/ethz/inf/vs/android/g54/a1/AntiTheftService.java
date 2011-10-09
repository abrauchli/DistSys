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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

/** Service hosting the alarm logic. */
public class AntiTheftService extends Service implements SensorEventListener {

	private NotificationManager notificationManager;
	private Notification notification;
	private Sensor accelerometer;
	private MediaPlayer mediaPlayer;
	private AudioManager audioManager;
	private SensorManager sensorManager;
	
	/* 
	 * The time stamp of the first event above the 
	 * threshold of the accelerometer.
	 */
	private long timeStamp;
	
	/* 
	 * The time stamp of the first detection of 
	 * deliberate movement.
	 */
	private long timeStamp2;
	
	/* Whether we're in the grace period before alarm activation */
	private boolean isMovementDetected = false;
	
	/* The current threshold values that will trigger 
	 * the (pre-)alarm period.
	 */
	private float[] threshold = { 2.0f, 2.0f, 11.0f };
	
	/* 
	 * Delay during which the user still is able to disarm 
	 * the device.
	 */
	private int delay;

	/**
	 * Class for clients to access. Because we know this service always runs 
	 * in  the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		AntiTheftService getService() {
			return AntiTheftService.this;
		}
	}
	
	/** Called when the service is created for the first time. */
	@Override
	public void onCreate() {
		/* Initialize attributes. */
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mediaPlayer = MediaPlayer.create(this, R.raw.bark);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		/* Sets up the media player. */
		mediaPlayer.setVolume(10.0f, 10.0f);
		mediaPlayer.setLooping(true);
	}

	/** Called when the service is started or when the delay is updated */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/* Updates the delay. */
		if (intent.hasExtra("is_running") && intent.hasExtra("delay") &&
				intent.getExtras().getBoolean("is_running")) {
			delay = intent.getExtras().getInt("delay");
			return START_STICKY;
		}
		
		/* Use accelerometer to detect deliberate movement. */
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		
		/* Sets the delay. */
		if (intent.hasExtra("delay")) {
			delay = intent.getExtras().getInt("delay");
		}
		
		/* Notifies the user that the Anti Theft Service was started. */
		notification = new Notification(R.drawable.icon, "Anti Theft Service started", System.currentTimeMillis());
		notification.tickerText = "Anti Theft Service started";
		notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		
		Context context = getApplicationContext();
		CharSequence contentTitle = "Anti Theft Service";
		CharSequence contentText = "Service is active.";
		Intent notificationIntent = new Intent(this, AntiTheftService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		notificationManager.notify(0, notification);
		
		/* 
		 * We want this service to continue running until it is 
		 * explicitly stopped, so return sticky.
		 */
		return START_STICKY;
	}

	/** 
	 * Called to notify the service that it is no longer used and 
	 * is being removed.
	 */
	@Override
	public void onDestroy() {
		/* 
		 * Unregister the accelerometer to inhibit calls to onAccuracyChanged and 
		 * onSensorChanged after the service was turned off.
		 */
		sensorManager.unregisterListener(this, accelerometer);
		
		/* Delete all issued notifications. */
		notificationManager.cancel(0);
		notificationManager.cancel(1);
		
		/* Stop the media player. */
		mediaPlayer.stop();
		
		/* Inform the user. */
		Toast.makeText(this, R.string.msg_service_stopped, Toast.LENGTH_SHORT).show();
	}

	/** Return the communication channel to the service. */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/** Starts the alarm. */
	private void ringAlarm() {
		/* 
		 * Unregister the accelerometer to inhibit calls to onAccuracyChanged and 
		 * onSensorChanged after the alarm was triggered.
		 */
		sensorManager.unregisterListener(this, accelerometer);
		
		/* 
		 * Activates the speaker phone if a Bluetooth head set or
		 * wired headphones are connected. Does not work although 
		 * the API was used according to the documentation.
		 */
		if (audioManager.isBluetoothA2dpOn() || audioManager.isWiredHeadsetOn()) {
			audioManager.setSpeakerphoneOn(true);
		}
		
		/* Play alarm. */
		mediaPlayer.start();
	}

	/** Called when the accuracy of the accelerometer has changed. */
	@Override
	public void onAccuracyChanged(Sensor s, int acc) {
		/* Do nothing. */
	}

	/** Called when accelerometer values have changed. */
	@Override
	public void onSensorChanged(SensorEvent evt) {
		/* 
		 * The time difference between the first event 
		 * above the threshold and the current one 
		 */
		long difference = evt.timestamp - timeStamp;
		
		/* 
		 * Check if one of the axes of the accelerometer
		 * is above the predefined threshold.
		 */
		if (Math.abs(evt.values[0]) > threshold[0] || 
				Math.abs(evt.values[1]) > threshold[1] || 
				Math.abs(evt.values[2]) > threshold[2]) {
			if (isMovementDetected) {
				/* Case movement already detected. */
				
				/* Check if deliberate movement. */
				if (difference > 5 * 1000000000l) {
					/* Case deliberate movement.*/
					
					/* 
					 * Adjust the time stamp to the first point in time the 
					 * deliberate movement was detected.
					 */
					timeStamp2 = evt.timestamp;
					
					/* Notifies the user that a potential theft was detected */
					Notification notification = new Notification(R.drawable.icon, "THEFT ALARM!", System.currentTimeMillis());
					notification.tickerText = "THEFT ALARM!";
					
					Context context = getApplicationContext();
					CharSequence contentTitle = "Anti Theft Service";
					CharSequence contentText = "THEFT ALARM!";
					Intent notificationIntent = new Intent(this, AntiTheftService.class);
					PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
					
					notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
					
					notificationManager.notify(1, notification);
					
					/* Check if the delay to disarm the alarm has elapsed. */
					if (difference > (5 + delay) * 1000000000l) {
						/* Case delay has elapsed. */
						
						/* Play an alarm sound. */
						ringAlarm();
					}
				}
			} else {
				/* Case movement not detected. */
				
				isMovementDetected = true;
				
				/* 
				 * Decrease threshold to increase sensitivity of the
				 * movement detection.
				 */
				threshold = new float[] { 1.0f, 1.0f, 10.0f };
				
				/* 
				 * Adjust the time stamp to the first point in time the 
				 * movement was detected.
				 */
				timeStamp = evt.timestamp;
				
				/* Reset difference to the initial value. */
				difference = 0;
			}
			return;
		}
		
		/* Check if enough time (10sec) has elapsed to reset the alarm. */
		if (isMovementDetected && (evt.timestamp - timeStamp2) > 10 * 1000000000l) {
			/* Case reset alarm. */
			
			isMovementDetected = false;
			
			/* 
			 * Increase threshold to decrease sensitivity of the
			 * movement detection.
			 */
			threshold = new float[] { 2.0f, 2.0f, 11.0f };
			
			/* Cancel the notification reporting a potential theft. */
			notificationManager.cancel(1);
		}
	}

}

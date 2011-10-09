package ch.ethz.inf.vs.android.g54.a1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/** 
 * Activity to start and stop the alarm logic through GUI elements.
 */
public class AntiTheftMain extends Activity {
	
	int delay; //Delay during which the user still is able to disarm the device.
	boolean is_running; //Is the service hosting the alarm logic running?
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		final AntiTheftMain currentClass = this;
		
		/* Sets up the listener to capture the changes made in the SeekBar. */		
		SeekBar seekDelay = (SeekBar) findViewById(R.id.seek_delay);
		seekDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		
			/** Notification that the delay has changed. */
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean user) {
				delay = progress;
				
				/* Update the value in the TextView. */
				TextView current_delay = (TextView) findViewById(R.id.txt_current_delay);
				current_delay.setText("Current delay: " + delay + "sec");
				
				if (is_running) {
					/* Notify the service that the delay was updated. */
					
					Intent i = new Intent(currentClass, AntiTheftService.class);
					
					/* Add delay to intent. */
					i.putExtra("delay", delay);
					
					/* 
					 * Boolean flag to indicate if the service needs to be started or if just
					 * the delay needs to be updated.
					 * */
					i.putExtra("is_started", is_running);
					
					startService(i);
				}
			}
	
				/** Notification that the user has started a touch gesture. */
				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					/* Do nothing. */
				}
				
				/** Notification that the user has finished a touch gesture. */
				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
					/* Do nothing. */
				}
			}
		);
	}
	
	/** Called when the alarm is armed or disarmed. */
	public void onAlarmClick(View v) {
		Button b = (Button) v;
		
		if (!is_running) {
			/* Case Service not running: Start service. */
			Intent i = new Intent(this, AntiTheftService.class);
			
			/* Add delay to intent. */
			i.putExtra("delay", delay);
			
			/* 
			 * Boolean flag to indicate if the service needs to be started or if just
			 * the delay needs to be updated.
			 * */
			i.putExtra("is_running", is_running);
			
			startService(i);
			
			is_running = true;
			
			/* Update the text of the Arm-Disarm-Button. */
			b.setText(R.string.disarm_alarm);
			
			/* Inform the user that the alarm is on. */
			Toast.makeText(this, R.string.alarm_on, Toast.LENGTH_SHORT).show();
		} else {
			/* Case Service running: Stop service. */
			
			/* Update the text of the Arm-Disarm-Button. */
			b.setText(R.string.arm_alarm);
			
			/* Inform the user that the alarm is off.. */
			Toast.makeText(this, R.string.alarm_off, Toast.LENGTH_SHORT).show();
			
			stopService(new Intent(this, AntiTheftService.class));
			is_running = false;
		}
	}
	
	/** Called when a new key event occurs. */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    /* Check if the service is running and if the back button is pressed*/
		if (is_running && keyCode == KeyEvent.KEYCODE_BACK) {
	        /* Case service is running and back button is pressed.*/
			
			/* Move task to back. */
			moveTaskToBack(true);
	        return true;
	    }
		
		/* Perform usual action when back button is pressed. */
	    return super.onKeyDown(keyCode, event);
	}
	
}
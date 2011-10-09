package ch.ethz.inf.vs.android.g54.a1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/** 
 * Activity to start and stop the alarm logic through GUI elements.
 */
public class AntiTheftMain extends Activity {
	
	int delay; //Delay during which the user still is able to disarm the device.
	boolean is_running; //Is the service hosting the alarm logic running?
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		final AntiTheftMain currentClass = this;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		/* Sets up the listener to capture the changes made in the SeekBar. */		
		SeekBar seekDelay = (SeekBar) findViewById(R.id.seek_delay);
		seekDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		
			/** Notification that the delay has changed. */
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean user) {
				delay = progress;
				
				/* Update the value in the TextView. */
				TextView current_delay = (TextView) findViewById(R.id.current_delay);
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
		TextView t = (TextView) findViewById(R.id.service_status);
		
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
			b.setText(R.string.disarm);
			
			/* Update the text of the Service-Status-TextView. */
			t.setText(R.string.on);
		} else {
			/* Case Service running: Stop service. */
			
			/* Update the text of the Arm-Disarm-Button. */
			b.setText(R.string.arm);
			
			/* Update the text of the Service-Status-TextView. */
			t.setText(R.string.off);
			
			stopService(new Intent(this, AntiTheftService.class));
			is_running = false;
		}
	}
	
}
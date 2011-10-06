package ch.ethz.inf.vs.android.g54.a1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.ToggleButton;

public class AntiTheftMain extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void onDelayChanged(View v) {
		SeekBar b = (SeekBar) v;
		int delay = b.getProgress();
	}

	public void onAlarmClick(View v) {
		ToggleButton b = (ToggleButton) v;
		if (b.isChecked()) {
			startService(new Intent(this, AntiTheftService.class));
		} else {
			stopService(new Intent(this, AntiTheftService.class));
		}
	}
}
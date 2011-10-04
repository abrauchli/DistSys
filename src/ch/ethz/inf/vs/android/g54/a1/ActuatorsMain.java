package ch.ethz.inf.vs.android.g54.a1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.SeekBar;

public class ActuatorsMain extends Activity {
	private Vibrator vib = null;
	private int duration = 50;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actuators);

		vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		SeekBar seekDuration = (SeekBar) findViewById(R.id.seek_duration);
		seekDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean user) {
						duration = progress;
					}

					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					public void onStopTrackingTouch(SeekBar seekBar) {
						vib.vibrate(duration * 10);
					}
				});

	}

	public void onClickVibrate(View v) {
		long[] pattern = { 0, 100, 100, 200, 100, 100 };
		vib.vibrate(pattern, -1);
	}
}

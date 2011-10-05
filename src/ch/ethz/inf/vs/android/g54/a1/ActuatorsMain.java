package ch.ethz.inf.vs.android.g54.a1;

import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ActuatorsMain extends Activity {
	
	///
	/// Vibrator
	///
	
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

		// media player
		mp = MediaPlayer.create(this, R.raw.bark);
		mp.setVolume(10.0f, 10.0f);
		mp.setLooping(true);

	}

	public void onClickVibrate(View v) {
		long[] pattern = { 0, 100, 100, 200, 100, 100 };
		vib.vibrate(pattern, -1);
	}
	
	///
	/// Flash
	///

	private Camera cam;

	public void onClickFlash(View v) {
		ToggleButton tb = (ToggleButton) v;
		if (tb.isChecked()) {
			cam = Camera.open();
			Camera.Parameters parameters = cam.getParameters();
			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			cam.setParameters(parameters);
			cam.startPreview(); // for some devices
		} else {
			cam.release();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (cam != null) {
			cam.release();
		}
		Toast.makeText(this, "Camera released", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onResume() {
		super.onResume();
		((ToggleButton) findViewById(R.id.tgl_flash)).setChecked(false);
	}
	
	///
	/// Sound
	///
	
	private MediaPlayer mp = null;

	public void onClickSound(View v) {
		if (!mp.isPlaying()) {
			mp.start();
			if (mp.isLooping()) {
				((Button) v).setText(R.string.btn_sound_stop);
			}
		} else {
			mp.stop();
			try {
				mp.prepareAsync();
			} catch (IllegalStateException e) { }
			((Button) v).setText(R.string.btn_sound);
		}
	}

}

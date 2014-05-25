package vonn.droidtheremin;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.vonn.droidtheremin.R;

public class MainActivity extends Activity {
	Thread t;
	int sr = 44100;
	boolean isRunning = true;
	SeekBar fSlider, vSlider;
	CheckBox activeToggle;
	double fSliderval, vSliderval;
	AudioTrack audioTrack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// point the slider to the GUI widget
		fSlider = (SeekBar) findViewById(R.id.frequency);
		vSlider = (SeekBar) findViewById(R.id.volume);
		activeToggle = (CheckBox) findViewById(R.id.chbx_Active);

		// set the listeners on the sliders
		fSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				fSliderval = progress / (double) seekBar.getMax();
			}
		});

		vSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				vSliderval = progress / (double) seekBar.getMax();
			}
		});

		activeToggle
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (!isChecked)
							audioTrack.pause();
						else
							audioTrack.play();
					}
				});

		// start a new thread to synthesise audio
		t = new Thread() {
			public void run() {
				// set process priority
				setPriority(Thread.MAX_PRIORITY);
				// set the buffer size
				int buffsize = AudioTrack.getMinBufferSize(sr,
						AudioFormat.CHANNEL_OUT_MONO,
						AudioFormat.ENCODING_PCM_16BIT);
				// create an audiotrack object
				audioTrack = new AudioTrack(
						AudioManager.STREAM_MUSIC, sr,
						AudioFormat.CHANNEL_OUT_MONO,
						AudioFormat.ENCODING_PCM_16BIT, buffsize,
						AudioTrack.MODE_STREAM);

				short samples[] = new short[buffsize];
				int amp = 10000;
				double twopi = 8. * Math.atan(1.);
				double fr = 400.f;
				double ph = 0.0;
				double mod = 0.0;
				boolean vib = false;

				// start audio
				audioTrack.play();

				// synthesis loop
				while (isRunning) {
					fr = 400 + 400 * fSliderval;
					amp = (int) (10000 * vSliderval);
					mod = vib ? 3 : -3;
					for (int i = 0; i < buffsize; i++) {
						samples[i] = (short) (amp * Math.sin(ph));
						ph += (twopi * fr + mod) / sr;
					}
					audioTrack.write(samples, 0, buffsize);
					vib = !vib;
				}
				audioTrack.stop();
				audioTrack.release();
			}
		};
		t.start();
		
		vSlider.setProgress(50);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onDestroy() {
		super.onDestroy();
		isRunning = false;
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		t = null;
	}
}
package com.victor.ledblinking;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import java.io.IOException;

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 *
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {

	private Gpio gpio;
	private Handler handler = new Handler();
	private static final String LED = "BCM6";

	private Runnable blinkingRunnable = new Runnable() {
		@Override
		public void run() {
			if (gpio == null) {
				return;
			}

			try {
				gpio.setValue(!gpio.getValue());

				handler.postDelayed(blinkingRunnable, 1000);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PeripheralManagerService service = new PeripheralManagerService();

		try {
			gpio = service.openGpio(LED);
			gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

			handler.post(blinkingRunnable);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		handler.removeCallbacks(blinkingRunnable);

		if (gpio == null) {
			return;
		}

		try {
			gpio.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			gpio = null;
		}
	}
}

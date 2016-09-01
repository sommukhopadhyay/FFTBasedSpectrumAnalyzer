package com.somitsolutions.android.spectrumanalyzer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import ca.uol.aig.fftpack.RecordTask;
import ca.uol.aig.fftpack.view.ScaleImageView;

public class SoundRecordAndAnalysisActivity extends Activity implements OnClickListener {

	Button startStopButton;
	RecordTask recordTask;
	ImageView imageViewDisplaySectrum;
	ScaleImageView imageViewScale;
	Bitmap bitmapDisplaySpectrum;
	Canvas canvasDisplaySpectrum;
	Paint paintSpectrumDisplay;
	Paint paintScaleDisplay;
	LinearLayout main;
	int width;
	int height;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
	}

	public void onClick(View v) {
		if (recordTask.isStarted()) {
			startStopButton.setText("Start");
			recordTask.setCancel();
			canvasDisplaySpectrum.drawColor(Color.BLACK);
		} else {
			startStopButton.setText("Stop");
			recordTask = new RecordTask(canvasDisplaySpectrum, paintSpectrumDisplay, imageViewDisplaySectrum, width);
			recordTask.execute();
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		main = new LinearLayout(this);
		main.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		main.setOrientation(LinearLayout.VERTICAL);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		imageViewDisplaySectrum = new ImageView(this);
		if (width > 512) {
			bitmapDisplaySpectrum = Bitmap.createBitmap(512, 300, Bitmap.Config.ARGB_8888);
		} else {
			bitmapDisplaySpectrum = Bitmap.createBitmap(256, 150, Bitmap.Config.ARGB_8888);
		}
		LinearLayout.LayoutParams layoutParams_imageViewScale = null;
		canvasDisplaySpectrum = new Canvas(bitmapDisplaySpectrum);
		paintSpectrumDisplay = new Paint();
		paintSpectrumDisplay.setColor(Color.GREEN);
		imageViewDisplaySectrum.setImageBitmap(bitmapDisplaySpectrum);
//		if (width > 512) {
//			LayoutParams layoutParams_imageViewDisplaySpectrum = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//			layoutParams_imageViewDisplaySpectrum.setMargins(100, 600, 0, 0);
//			imageViewDisplaySectrum.setLayoutParams(layoutParams_imageViewDisplaySpectrum);
//			layoutParams_imageViewScale = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//			layoutParams_imageViewScale.setMargins(100, 20, 0, 0);
//		} else if ((width > 320) && (width < 512)) {
//			LayoutParams layoutParams_imageViewDisplaySpectrum = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//			layoutParams_imageViewDisplaySpectrum.setMargins(60, 250, 0, 0);
//			imageViewDisplaySectrum.setLayoutParams(layoutParams_imageViewDisplaySpectrum);
//			layoutParams_imageViewScale = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//			layoutParams_imageViewScale.setMargins(60, 20, 0, 100);
//		} else if (width < 320) {
//			imageViewDisplaySectrum.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//			layoutParams_imageViewScale = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//		}
		imageViewDisplaySectrum.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		layoutParams_imageViewScale = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		imageViewDisplaySectrum.setId(View.NO_ID);
		main.addView(imageViewDisplaySectrum);

		//Scale
		imageViewScale = new ScaleImageView(this);
		imageViewScale.setLayoutParams(layoutParams_imageViewScale);
		imageViewScale.setId(View.NO_ID);
		main.addView(imageViewScale);

		//Button
		startStopButton = new Button(this);
		startStopButton.setText("Start");
		startStopButton.setOnClickListener(this);
		startStopButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		main.addView(startStopButton);

		setContentView(main);

		recordTask = new RecordTask(canvasDisplaySpectrum, paintSpectrumDisplay, imageViewDisplaySectrum, width);
	}

	@Override
	public void onBackPressed() {
		try {
			if (recordTask.isStarted()) {
				recordTask.setCancel();
			} else {
				super.onBackPressed();
			}
		} catch (IllegalStateException e) {
			Log.e("Stop failed", e.toString());

		}
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}


	@Override
	public void onStop() {
		super.onStop();
		if (recordTask != null) {
			recordTask.cancel(true);
		}
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (recordTask != null) {
			recordTask.cancel(true);
		}
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

}

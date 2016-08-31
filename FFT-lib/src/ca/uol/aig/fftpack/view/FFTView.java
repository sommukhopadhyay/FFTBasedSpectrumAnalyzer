package ca.uol.aig.fftpack.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class FFTView extends ImageView {
	Paint paintScaleDisplay;
	Bitmap bitmapScale;
	Canvas canvasScale;
	private int viewWidth = 0;
	private int viewHeight;

	public FFTView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		if (viewWidth > 512) {
			bitmapScale = Bitmap.createBitmap(512, 50, Bitmap.Config.ARGB_8888);
		} else {
			bitmapScale = Bitmap.createBitmap(256, 50, Bitmap.Config.ARGB_8888);
		}

		paintScaleDisplay = new Paint();
		paintScaleDisplay.setColor(Color.WHITE);
		paintScaleDisplay.setStyle(Paint.Style.FILL);

		canvasScale = new Canvas(bitmapScale);

		setImageBitmap(bitmapScale);
		invalidate();
	}

	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		viewWidth = xNew;
		viewHeight = yNew;
		Log.d("dimension", viewWidth + " x " + viewHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (viewWidth > 512) {
			canvasScale.drawLine(0, 30, 512, 30, paintScaleDisplay);
			for (int i = 0, j = 0; i < 512; i = i + 128, j++) {
				for (int k = i; k < (i + 128); k = k + 16) {
					canvasScale.drawLine(k, 30, k, 25, paintScaleDisplay);
				}
				canvasScale.drawLine(i, 40, i, 25, paintScaleDisplay);
				String text = Integer.toString(j) + " KHz";
				canvasScale.drawText(text, i, 45, paintScaleDisplay);
			}
			canvas.drawBitmap(bitmapScale, 0, 0, paintScaleDisplay);
		} else if ((viewWidth > 320) && (viewWidth < 512)) {
			canvasScale.drawLine(0, 30, 0 + 256, 30, paintScaleDisplay);
			for (int i = 0, j = 0; i < 256; i = i + 64, j++) {
				for (int k = i; k < (i + 64); k = k + 8) {
					canvasScale.drawLine(k, 30, k, 25, paintScaleDisplay);
				}
				canvasScale.drawLine(i, 40, i, 25, paintScaleDisplay);
				String text = Integer.toString(j) + " KHz";
				canvasScale.drawText(text, i, 45, paintScaleDisplay);
			}
			canvas.drawBitmap(bitmapScale, 0, 0, paintScaleDisplay);
		} else if (viewWidth < 320) {
			canvasScale.drawLine(0, 30, 256, 30, paintScaleDisplay);
			for (int i = 0, j = 0; i < 256; i = i + 64, j++) {
				for (int k = i; k < (i + 64); k = k + 8) {
					canvasScale.drawLine(k, 30, k, 25, paintScaleDisplay);
				}
				canvasScale.drawLine(i, 40, i, 25, paintScaleDisplay);
				String text = Integer.toString(j) + " KHz";
				canvasScale.drawText(text, i, 45, paintScaleDisplay);
			}
			canvas.drawBitmap(bitmapScale, 0, 0, paintScaleDisplay);
		}
	}
}

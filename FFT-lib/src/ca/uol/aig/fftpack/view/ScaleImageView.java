package ca.uol.aig.fftpack.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class ScaleImageView extends ImageView {
	Paint paintScaleDisplay;
	Bitmap bitmapScale;
	Canvas canvasScale;
	private int viewWidth;
	private int viewHeight;

	public ScaleImageView(Context context) {
		super(context);
	}

	public ScaleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void init() {
		if (!isInEditMode()) {
			bitmapScale = Bitmap.createBitmap(viewWidth, 50, Bitmap.Config.ARGB_8888);

			paintScaleDisplay = new Paint();
			paintScaleDisplay.setColor(Color.WHITE);
			paintScaleDisplay.setStyle(Paint.Style.FILL);

			canvasScale = new Canvas(bitmapScale);
			setImageBitmap(bitmapScale);
			invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (canvasScale == null) {
			return;
		}

		int linesBig = viewWidth / 4;
		int linesSmall = viewWidth / 16;
		canvasScale.drawLine(0, 25, viewWidth, 25, paintScaleDisplay);
		for (int i = 0, j = 0; i < viewWidth; i = i + linesBig, j++) {
			for (int k = i; k < (i + linesBig); k = k + linesSmall) {
				canvasScale.drawLine(k, 30, k, 25, paintScaleDisplay);
			}
			canvasScale.drawLine(i, 40, i, 25, paintScaleDisplay);
			String text = Integer.toString(j) + " KHz";
			canvasScale.drawText(text, i, 45, paintScaleDisplay);
		}
//		canvas.drawBitmap(bitmapScale, 0, 0, paintScaleDisplay);
	}

	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		viewWidth = xNew;
		viewHeight = yNew;
		init();
		Log.d("DimensionScale", viewWidth + " x " + viewHeight);
	}
}

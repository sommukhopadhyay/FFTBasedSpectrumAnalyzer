package ca.uol.aig.fftpack;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class RecordTask extends AsyncTask<Void, double[], Boolean> {

	private final int width;
	private final ImageView imageViewDisplaySectrum;
	private final Canvas canvasDisplaySpectrum;
	private final Paint paintSpectrumDisplay;
	private RealDoubleFFT transformer;
	private int blockSize = 256;
	private boolean started = false;
	private boolean CANCELLED_FLAG = false;
	private AudioRecord audioRecord;
	int frequency = 8000;
	int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

	public RecordTask(Canvas canvasDisplaySpectrum, Paint paintSpectrumDisplay, ImageView imageViewDisplaySectrum, int width) {
		this.width = width;
		blockSize = width / 2;
		this.imageViewDisplaySectrum = imageViewDisplaySectrum;
		this.canvasDisplaySpectrum = canvasDisplaySpectrum;
		this.paintSpectrumDisplay = paintSpectrumDisplay;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		transformer = new RealDoubleFFT(blockSize);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d("Recording doBackground", params.toString());

		int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, frequency, channelConfiguration, audioEncoding, bufferSize);
		int bufferReadResult;
		short[] buffer = new short[blockSize];
		double[] toTransform = new double[blockSize];
		try {
			audioRecord.startRecording();
			started = true;
		} catch (IllegalStateException e) {
			Log.e("Recording failed", e.toString());
		}
		while (started) {
			if (isCancelled() || (CANCELLED_FLAG)) {
				started = false;
				// publishProgress(cancelledResult);
				Log.d("doInBackground", "Cancelling the RecordTask");
				break;
			} else {
				bufferReadResult = audioRecord.read(buffer, 0, blockSize);

				for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
					toTransform[i] = buffer[i] / 32768.0; // signed 16 bit
				}

				transformer.ft(toTransform);
				publishProgress(toTransform);
			}
		}
		return true;
	}

	@Override
	protected void onProgressUpdate(double[]... progress) {
//		Log.v("onProgressUpdate:", Integer.toString(progress[0].length));
		canvasDisplaySpectrum.drawColor(Color.GRAY);
		if (width > 512) {
			for (int i = 0; i < progress[0].length; i++) {
				int x = 2 * i;
				int downy = (int) (150 - (progress[0][i] * 10));
				int upy = 150;
				canvasDisplaySpectrum.drawLine(x, downy, x, upy, paintSpectrumDisplay);
			}
			imageViewDisplaySectrum.invalidate();
		} else {
			for (int i = 0; i < progress[0].length; i++) {
				int x = i;
				int downy = (int) (150 - (progress[0][i] * 10));
				int upy = 150;
				canvasDisplaySpectrum.drawLine(x, downy, x, upy, paintSpectrumDisplay);
			}

			imageViewDisplaySectrum.invalidate();
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		try {
			if (audioRecord != null) {
				audioRecord.stop();
			}
		} catch (IllegalStateException e) {
			Log.e("Stop failed", e.toString());
		}

		canvasDisplaySpectrum.drawColor(Color.BLACK);
		imageViewDisplaySectrum.invalidate();
	}

	public boolean isStarted() {
		return started;
	}

	public void setCancel() {
		CANCELLED_FLAG = true;
	}
}

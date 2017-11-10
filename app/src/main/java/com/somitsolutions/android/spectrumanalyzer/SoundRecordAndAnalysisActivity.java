package com.somitsolutions.android.spectrumanalyzer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import ca.uol.aig.fftpack.RealDoubleFFT;


public class SoundRecordAndAnalysisActivity extends Activity implements OnClickListener{

    private static final double[] CANCELLED = {100};
	int frequency = 8000;/*44100;*/
    int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    AudioRecord audioRecord;
    private RealDoubleFFT transformer;
    int blockSize = /*2048;// = */256;
    Button startStopButton;
    boolean started = false;
    boolean CANCELLED_FLAG = false;
    double[][] cancelledResult = {{100}};
    int mPeakPos;
    double mHighestFreq;
    RecordAudio recordTask;
    ImageView imageViewDisplaySectrum;
    MyImageView imageViewScale;
    Bitmap bitmapDisplaySpectrum;
   
    Canvas canvasDisplaySpectrum;

    Paint paintSpectrumDisplay;
    Paint paintScaleDisplay;
    static SoundRecordAndAnalysisActivity mainActivity;
    LinearLayout main;
    int width;
    int height;
    int left_Of_BimapScale;
    int left_Of_DisplaySpectrum;
    private final static int ID_BITMAPDISPLAYSPECTRUM = 1;
    private final static int ID_IMAGEVIEWSCALE = 2;

    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
    	//Point size = new Point();
    	//display.get(size);
    	width = display.getWidth();
    	height = display.getHeight();

        //blockSize = 256;



    }
    
    @Override
	public void onWindowFocusChanged (boolean hasFocus) {
    	//left_Of_BimapScale = main.getC.getLeft();
    	MyImageView  scale = (MyImageView)main.findViewById(R.id.ID_IMAGEVIEWSCALE/*ID_IMAGEVIEWSCALE*/);
    	ImageView bitmap = (ImageView)main.findViewById(R.id.ID_BITMAPDISPLAYSPECTRUM);
    	left_Of_BimapScale = scale.getLeft();
    	left_Of_DisplaySpectrum = bitmap.getLeft();
    }
    private class RecordAudio extends AsyncTask<Void, double[], Boolean> {
    	
        @Override
        protected Boolean doInBackground(Void... params) {

            int bufferSize = AudioRecord.getMinBufferSize(frequency,
                    channelConfiguration, audioEncoding);
            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.DEFAULT, frequency,
                    channelConfiguration, audioEncoding, bufferSize);
            int bufferReadResult;
            short[] buffer = new short[blockSize];
            double[] toTransform = new double[blockSize];
            try {
                audioRecord.startRecording();
            } catch (IllegalStateException e) {
                Log.e("Recording failed", e.toString());

            }
            while (started) {

                if (isCancelled() || (CANCELLED_FLAG == true)) {

                    started = false;
                    publishProgress(cancelledResult);
                    Log.d("doInBackground", "Cancelling the RecordTask");
                    break;
                } else {
                    bufferReadResult = audioRecord.read(buffer, 0, blockSize);

                    for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
                        toTransform[i] = (double) buffer[i] / 32768.0; // signed 16 bit
                    }

                    transformer.ft(toTransform);

                    publishProgress(toTransform);

                }

            }
            return true;
        }
        @Override
        protected void onProgressUpdate(double[]...progress) {
        	Log.e("RecordingProgress", "Displaying in progress");
            double mMaxFFTSample = 150.0;

            Log.d("Test:", Integer.toString(progress[0].length));
            if(progress[0].length == 1 ){

                Log.d("FFTSpectrumAnalyzer", "onProgressUpdate: Blackening the screen");
                canvasDisplaySpectrum.drawColor(Color.BLACK);
                imageViewDisplaySectrum.invalidate();

            }

            else {
                if (width > 512) {
                    for (int i = 0; i < progress[0].length; i++) {
                        int x = 2 * i;
                        int downy = (int) (150 - (progress[0][i] * 10));
                        int upy = 150;
                        if(downy < mMaxFFTSample)
                        {
                            mMaxFFTSample = downy;
                            //mMag = mMaxFFTSample;
                            mPeakPos = i;
                        }

                        canvasDisplaySpectrum.drawLine(x, downy, x, upy, paintSpectrumDisplay);
                    }

                    imageViewDisplaySectrum.invalidate();
                } else {
                    for (int i = 0; i < progress[0].length; i++) {
                        int x = i;
                        int downy = (int) (150 - (progress[0][i] * 10));
                        int upy = 150;
                        if(downy < mMaxFFTSample)
                        {
                            mMaxFFTSample = downy;
                            //mMag = mMaxFFTSample;
                            mPeakPos = i;
                        }
                        canvasDisplaySpectrum.drawLine(x, downy, x, upy, paintSpectrumDisplay);
                    }


                    imageViewDisplaySectrum.invalidate();
                }
            }


        }
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
                try{
                    audioRecord.stop();
                }
                catch(IllegalStateException e){
                    Log.e("Stop failed", e.toString());
                }

                canvasDisplaySpectrum.drawColor(Color.BLACK);
                imageViewDisplaySectrum.invalidate();
               /* mHighestFreq = (((1.0 * frequency) / (1.0 * blockSize)) * mPeakPos)/2;
                String str = "Frequency for Highest amplitude: " + mHighestFreq;
                Toast.makeText(getApplicationContext(), str , Toast.LENGTH_LONG).show();*/

            }
       }

        protected void onCancelled(Boolean result){

            try{
                audioRecord.stop();
            }
            catch(IllegalStateException e){
                Log.e("Stop failed", e.toString());

            }
            //recordTask.cancel(true);

            Log.d("FFTSpectrumAnalyzer","onCancelled: New Screen");
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
   
        public void onClick(View v) {
        if (started == true) {
                //started = false;
                CANCELLED_FLAG = true;
                //recordTask.cancel(true);
                try{
                    audioRecord.stop();
                }
                catch(IllegalStateException e){
                    Log.e("Stop failed", e.toString());

                }
                startStopButton.setText("Start");
                //show the frequency that has the highest amplitude...
                mHighestFreq = (((1.0 * frequency) / (1.0 * blockSize)) * mPeakPos)/2;
                String str = "Frequency for Highest amplitude: " + mHighestFreq;
                Toast.makeText(getApplicationContext(), str , Toast.LENGTH_LONG).show();

                canvasDisplaySpectrum.drawColor(Color.BLACK);

            }
        
        else {
                started = true;
                CANCELLED_FLAG = false;
                startStopButton.setText("Stop");
                recordTask = new RecordAudio();
                recordTask.execute();
        }  
        
     }
        static SoundRecordAndAnalysisActivity getMainActivity(){

            return mainActivity;
        }
        
        public void onStop(){
        	super.onStop();
        	/*started = false;
            startStopButton.setText("Start");*/
            //if(recordTask != null){
            recordTask.cancel(true);
            //}
            Intent intent = new Intent(Intent.ACTION_MAIN);
        	intent.addCategory(Intent.CATEGORY_HOME);
        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	startActivity(intent);
        }
        
        public void onStart(){
        	
        	super.onStart();
        	main = new LinearLayout(this);
        	main.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        	main.setOrientation(LinearLayout.VERTICAL);
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        	transformer = new RealDoubleFFT(blockSize);
            
            imageViewDisplaySectrum = new ImageView(this);
            if(width > 512){
            	bitmapDisplaySpectrum = Bitmap.createBitmap((int)512,(int)300,Bitmap.Config.ARGB_8888);
            }
            else{
            	 bitmapDisplaySpectrum = Bitmap.createBitmap((int)256,(int)150,Bitmap.Config.ARGB_8888);
            }
            LinearLayout.LayoutParams layoutParams_imageViewScale = null;
            //Bitmap scaled = Bitmap.createScaledBitmap(bitmapDisplaySpectrum, 320, 480, true);
            canvasDisplaySpectrum = new Canvas(bitmapDisplaySpectrum);
            //canvasDisplaySpectrum = new Canvas(scaled);
            paintSpectrumDisplay = new Paint();
            paintSpectrumDisplay.setColor(Color.GREEN);
            imageViewDisplaySectrum.setImageBitmap(bitmapDisplaySpectrum);
            if(width >512){
            	//imageViewDisplaySectrum.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            	LinearLayout.LayoutParams layoutParams_imageViewDisplaySpectrum=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                ((MarginLayoutParams) layoutParams_imageViewDisplaySpectrum).setMargins(100, 600, 0, 0);
                imageViewDisplaySectrum.setLayoutParams(layoutParams_imageViewDisplaySpectrum);
                layoutParams_imageViewScale= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //layoutParams_imageViewScale.gravity = Gravity.CENTER_HORIZONTAL;
                ((MarginLayoutParams) layoutParams_imageViewScale).setMargins(100, 20, 0, 0);
                
            }
            
            else if ((width >320) && (width<512)){
            	LinearLayout.LayoutParams layoutParams_imageViewDisplaySpectrum=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                ((MarginLayoutParams) layoutParams_imageViewDisplaySpectrum).setMargins(60, 250, 0, 0);
               //layoutParams_imageViewDisplaySpectrum.gravity = Gravity.CENTER_HORIZONTAL;
                imageViewDisplaySectrum.setLayoutParams(layoutParams_imageViewDisplaySpectrum);
                
            	//imageViewDisplaySectrum.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            	layoutParams_imageViewScale=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            	((MarginLayoutParams) layoutParams_imageViewScale).setMargins(60, 20, 0, 100);
            	//layoutParams_imageViewScale.gravity = Gravity.CENTER_HORIZONTAL;
            }
           
            else if (width < 320){
            	/*LinearLayout.LayoutParams layoutParams_imageViewDisplaySpectrum=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                ((MarginLayoutParams) layoutParams_imageViewDisplaySpectrum).setMargins(30, 100, 0, 100);
                imageViewDisplaySectrum.setLayoutParams(layoutParams_imageViewDisplaySpectrum);*/
            	imageViewDisplaySectrum.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            	layoutParams_imageViewScale=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            	//layoutParams_imageViewScale.gravity = Gravity.CENTER;
            }
            imageViewDisplaySectrum.setId(R.id.ID_BITMAPDISPLAYSPECTRUM);
            main.addView(imageViewDisplaySectrum);
            
            imageViewScale = new MyImageView(this);
            imageViewScale.setLayoutParams(layoutParams_imageViewScale);
            imageViewScale.setId(R.id.ID_IMAGEVIEWSCALE);
            
            //imageViewScale.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            main.addView(imageViewScale);
            
            startStopButton = new Button(this);
            startStopButton.setText("Start");
            startStopButton.setOnClickListener(this);
            startStopButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
           
            main.addView(startStopButton);
          
            setContentView(main);
            
            mainActivity = this;
            
        }
        @Override
        public void onBackPressed() {
        	super.onBackPressed();
        	//if(recordTask != null){
        		recordTask.cancel(true); 
        	//}
        	Intent intent = new Intent(Intent.ACTION_MAIN);
        	intent.addCategory(Intent.CATEGORY_HOME);
        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	startActivity(intent);
        }
        
        @Override
        protected void onDestroy() {
            // TODO Auto-generated method stub
            super.onDestroy();
            recordTask.cancel(true); 
            Intent intent = new Intent(Intent.ACTION_MAIN);
        	intent.addCategory(Intent.CATEGORY_HOME);
        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	startActivity(intent);
        }
        //Custom Imageview Class
        public class MyImageView extends ImageView {
        	Paint paintScaleDisplay;
        	Bitmap bitmapScale;
        	//Canvas canvasScale;
        	public MyImageView(Context context) {
        		super(context);
        		// TODO Auto-generated constructor stub
        		if(width >512){
        			bitmapScale = Bitmap.createBitmap((int)512,(int)50,Bitmap.Config.ARGB_8888);
                }
        		else{
        			bitmapScale =  Bitmap.createBitmap((int)256,(int)50,Bitmap.Config.ARGB_8888);
        		}
        		
        		paintScaleDisplay = new Paint();
        		paintScaleDisplay.setColor(Color.WHITE);
                paintScaleDisplay.setStyle(Paint.Style.FILL);
                
                //canvasScale = new Canvas(bitmapScale);
               
                setImageBitmap(bitmapScale);
                invalidate();
        	}
        	@Override
            protected void onDraw(Canvas canvas)
            {
                // TODO Auto-generated method stub
                super.onDraw(canvas);
               
                if(width > 512){
                	 //canvasScale.drawLine(0, 30,  512, 30, paintScaleDisplay);
                    canvas.drawLine(0, 30,  512, 30, paintScaleDisplay);
                	for(int i = 0,j = 0; i< 512; i=i+128, j++){
                     	for (int k = i; k<(i+128); k=k+16){
                     		//canvasScale.drawLine(k, 30, k, 25, paintScaleDisplay);
                            canvas.drawLine(k, 30, k, 25, paintScaleDisplay);
                     	}
                     	//canvasScale.drawLine(i, 40, i, 25, paintScaleDisplay);
                        canvas.drawLine(i, 40, i, 25, paintScaleDisplay);
                     	String text = Integer.toString(j) + " KHz";
                     	//canvasScale.drawText(text, i, 45, paintScaleDisplay);
                        canvas.drawText(text, i, 45, paintScaleDisplay);
                     }
                	canvas.drawBitmap(bitmapScale, 0, 0, paintScaleDisplay);
                }
                else if ((width >320) && (width<512)){
                	 //canvasScale.drawLine(0, 30, 0 + 256, 30, paintScaleDisplay);
                    canvas.drawLine(0, 30, 0 + 256, 30, paintScaleDisplay);
                	 for(int i = 0,j = 0; i<256; i=i+64, j++){
                     	for (int k = i; k<(i+64); k=k+8){
                     		//canvasScale.drawLine(k, 30, k, 25, paintScaleDisplay);
                            canvas.drawLine(k, 30, k, 25, paintScaleDisplay);
                     	}
                     	//canvasScale.drawLine(i, 40, i, 25, paintScaleDisplay);
                     	canvas.drawLine(i, 40, i, 25, paintScaleDisplay);
                     	String text = Integer.toString(j) + " KHz";
                     	//canvasScale.drawText(text, i, 45, paintScaleDisplay);
                         canvas.drawText(text, i, 45, paintScaleDisplay);
                     }
                	 canvas.drawBitmap(bitmapScale, 0, 0, paintScaleDisplay);
                }
               
                else if (width <320){
               	 //canvasScale.drawLine(0, 30,  256, 30, paintScaleDisplay);
               	 canvas.drawLine(0, 30,  256, 30, paintScaleDisplay);
               	 for(int i = 0,j = 0; i<256; i=i+64, j++){
                    	for (int k = i; k<(i+64); k=k+8){
                    		//canvasScale.drawLine(k, 30, k, 25, paintScaleDisplay);
                            canvas.drawLine(k, 30, k, 25, paintScaleDisplay);
                    	}
                    	//canvasScale.drawLine(i, 40, i, 25, paintScaleDisplay);
                     canvas.drawLine(i, 40, i, 25, paintScaleDisplay);
                    	String text = Integer.toString(j) + " KHz";
                    	//canvasScale.drawText(text, i, 45, paintScaleDisplay);
                        canvas.drawText(text, i, 45, paintScaleDisplay);
                    }
               	 canvas.drawBitmap(bitmapScale, 0, 0, paintScaleDisplay);
               }
            }
        }
}
    

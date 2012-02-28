package com.overfitters;
 
import com.theoverfitters.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
 
public class Alg1 extends Activity {
	private ContentManager cm;
	private TextView brText, conText;
	private SeekBar brScroll, conScroll;
	private CharSequence brS, conS;
	private ImageView iv;
	private int bright;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brightnesscontrast);
        cm = ContentManager.getContentManager();
        iv = (ImageView) findViewById(R.id.brightnessContrastImageView);
        cm.getNewImage(iv);
        bright = Native.GetGrayBrightness(cm.getImuGrayImage());
        brScroll = (SeekBar) findViewById(R.id.brightnessSeekBar);
        brScroll.setProgress(bright);
        brText = (TextView) findViewById(R.id.brightnessText);
        conScroll = (SeekBar) findViewById(R.id.contrastSeekBar);
        conText = (TextView) findViewById(R.id.contrastText);
        brS = brText.getText();
        conS = conText.getText();
        brText.setText(brS + " " + bright);
        conText.setText(conS + " " + 1);
        
        brScroll.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				if(arg2)
					brightness(arg1);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        conScroll.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				contrast(arg1);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        //tv1.setText("Memory is max " + Runtime.getRuntime().maxMemory()/1024/1024 + " total " + Runtime.getRuntime().totalMemory()/1024/1024 + " free " + Runtime.getRuntime().freeMemory()/1024/1024);
    }
 
    protected void contrast(int val) {
		// TODO Auto-generated method stub
		conText.setText(conS + " " + val);
	}

	protected void brightness(int val) {
		Bitmap gray = cm.getNewBigGrayImage();
		Bitmap colored = cm.getNewImage(iv);
		brText.setText(brS + " " + val);
		int i = Native.ModGrayBrightness(gray, val-bright);
		Bitmap sGray = cm.getNewSmallGrayImage();
		Native.AddColor(colored, sGray);
		iv.invalidate();
	}

	public void save(View view) {
        //hi[i] = new int[256*1024];
        //i++;
        //TextView tv1 = (TextView) findViewById(R.id.seekBar1Text);
        //System.gc();
        //tv1.setText("Memory is max " + Runtime.getRuntime().maxMemory()/1024/1024 + " total " + Runtime.getRuntime().totalMemory()/1024/1024 + " free " + Runtime.getRuntime().freeMemory()/1024/1024);
    }
 
    public void saveAs(View view) {
        
    }
 
    public void video(View view) {
        
    }
 
    public void videoServer(View view) {
        
    }
}
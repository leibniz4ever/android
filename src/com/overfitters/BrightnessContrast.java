package com.overfitters;
 
import com.theoverfitters.R;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
 
public class BrightnessContrast extends ImageProcessor {
	private TextView brText1, brText2, conText;
	private SeekBar brScroll, conScroll;
	private CharSequence brT1, brT2, conT;
	private int initBright, currBright;
	private int newVal, currVal;
	private float currCon, newCon;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,R.layout.brightness_contrast,R.id.brConLL);
        /*
        setContentView(R.layout.brightness_contrast);
        iv = (ImageView) findViewById(R.id.brightnessContrastImageView);
        cm.getNewImage(iv);
        int wid = cm.getWidth();
        int hei = cm.getHeight();
        iv.setMinimumHeight(hei);
        iv.setMaxHeight(hei);
        iv.setMinimumWidth(wid);
        iv.setMaxWidth(wid);
        Drawable draw = iv.getDrawable();
        Rect bounds;
        bounds = new Rect(0,0,wid,hei);
		draw.setBounds(bounds);
		*/
        
        brScroll = (SeekBar) findViewById(R.id.brightnessSeekBar);
        brText1 = (TextView) findViewById(R.id.brightnessText1);
        brText2 = (TextView) findViewById(R.id.brightnessText2);
        conScroll = (SeekBar) findViewById(R.id.contrastSeekBar);
        conText = (TextView) findViewById(R.id.contrastText);
        brT1 = brText1.getText();
        brT2 = brText2.getText();
        conT = conText.getText();
        
        brScroll.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				brightness(arg1);
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {}
        });
        conScroll.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				contrast(arg1);
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {}
        });
        
        reset();
}

	protected void brightness(int val) {
		newVal = val - 255;
		doProcess();
	}

	protected void contrast(int val) {
		newCon = val/100.0f;
		doProcess();
	}

	@Override
	public void processImage(Bitmap imu, Bitmap mut) {
		currVal = newVal;
		currCon = newCon;
		Native.ModContrast(imu, mut, currCon);
		Native.ModBrightness(imu, mut, currVal);
		currBright = Native.GetBrightness(mut);
	}

	@Override
	public void reset() {
		initBright = Native.GetBrightness(cm.getImage());
		currCon = newCon = 1.0f;
		currVal = newVal = 0;
	    brText1.setText(brT1 + " " + 0 + "\t");
	    brText2.setText(brT2 + " " + initBright);
	    conText.setText(conT + " " + 1);
	    brScroll.setProgress(255);
	    conScroll.setProgress(100);
	}

	@Override
	protected void firstUpdateUi() {
		brText1.setText(brT1 + " " + newVal + "\t");
		conText.setText(conT + " " + newCon);
	}

	@Override
	protected boolean lastUpdateUi() {
		brText2.setText(brT2 + " " + currBright);
		return currVal != newVal || currCon != newCon;
	}
}
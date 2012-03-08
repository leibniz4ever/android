package com.overfitters;

import com.theoverfitters.R;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
 
public class Invert extends ImageProcessor {
    private CheckBox invert,grayscale;
    private boolean inverted, gray;

	/** Called when the activity is first created. On GUI*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.invert, R.id.invertLL);

    	invert = (CheckBox) findViewById(R.id.invert);
    	grayscale = (CheckBox) findViewById(R.id.grayscale);
    	
    	reset();
    	
    	invert(null);
    }
    
    //called by either checkbox on GUI
    public void invert(View view) {
    	doProcess();
    }

    //not on GUI
	@Override
	public void processImage(Bitmap imu, Bitmap mut) {
		if(inverted = invert.isChecked()) {
    		if(gray = grayscale.isChecked()) {
    			Native.InvertGray(imu, mut);
    		}
    		else {
    			Native.InvertColored(imu, mut);
    		}
    	}
    	else {
    		if(gray = grayscale.isChecked()) {
    			Native.ColorToGray(imu, mut);
    		}
    		else {
    			Native.Copy(imu, mut);
    		}
    	}
	}

	//on GUI
	@Override
	public void reset() {
		invert.setChecked(false);
		grayscale.setChecked(false);
		inverted = false;
		gray = false;
	}

	//on GUI
	@Override
	protected void firstUpdateUi() {
		//nothing
	}

	//on GUI
	@Override
	protected boolean lastUpdateUi() {
		return invert.isChecked() != inverted || grayscale.isChecked() != gray;
	}
}
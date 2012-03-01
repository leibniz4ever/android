package com.overfitters;

import com.theoverfitters.R;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
 
public class Invert extends ImageProcessor {
    private ImageView iv;
    private CheckBox invert,grayscale;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invert);
        iv = (ImageView) findViewById(R.id.invertImageView);
        cm.getNewImage(iv);

    	invert = (CheckBox) findViewById(R.id.invert);
    	grayscale = (CheckBox) findViewById(R.id.grayscale);
    	
    	reset();
    }
    
    //called by either checkbox
    public void invert(View view) {
    	doProcess();
    }

	@Override
	public void processImage(Bitmap bm) {
		if(invert.isChecked()) {
    		if(grayscale.isChecked()) {
    			Native.InvertGray(bm);
    		}
    		else {
    			Native.InvertColored(bm);
    		}
    	}
    	else {
    		if(grayscale.isChecked()) {
    			Native.ColorToGray(bm);
    		}
    		else {
    			//do nothing
    		}
    	}
	}

	@Override
	public void reset() {
		invert.setChecked(false);
		grayscale.setChecked(false);
	}

	@Override
	protected void firstUpdateUi() {
		//nothing
	}

	@Override
	protected void lastUpdateUi() {
		cm.setImage(iv);
		contin = false;
	}
}
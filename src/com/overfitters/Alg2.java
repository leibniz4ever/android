package com.overfitters;

import com.theoverfitters.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
 
public class Alg2 extends Activity {
    private ContentManager cm;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invert);
        cm = ContentManager.getContentManager();
        ImageView iv = (ImageView) findViewById(R.id.invertImageView);
        //iv.setBackgroundColor(Color.WHITE);
        cm.getNewImage(iv);
        iv.invalidate();
    }
    
    public void invert(View view) {
    	CheckBox invert = (CheckBox) findViewById(R.id.invert);
    	CheckBox grayscale = (CheckBox) findViewById(R.id.grayscale);
    	ImageView iv = (ImageView) findViewById(R.id.invertImageView);
    	String s = "";
    	if(invert.isChecked()) {
    		if(grayscale.isChecked()) {
    			s = Native.InvertGray(cm.getNewBigGrayImage());
    			s = Native.CopyGray(cm.getNewImage(iv), cm.getNewSmallGrayImage());
    			iv.invalidate();
    		}
    		else {
    			s = Native.InvertColored(cm.getNewImage(iv));
    			iv.invalidate();
    		}
    	}
    	else {
    		if(grayscale.isChecked()) {
    			cm.getNewBigGrayImage();
    			s = Native.CopyGray(cm.getNewImage(iv), cm.getNewSmallGrayImage());
    			iv.invalidate();
    		}
    		else {
    			cm.getNewImage(iv);
    			iv.invalidate();
    		}
    	}
    	s.toString();
    }
 
    public void save(View view) {
        
    }
 
    public void saveAs(View view) {
        
    }
 
    public void video(View view) {
        
    }
 
    public void videoServer(View view) {
        
    }
}
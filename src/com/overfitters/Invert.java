package com.overfitters;

import com.theoverfitters.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
 
public class Invert extends Activity {
    private ContentManager cm;
    private ImageView iv;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invert);
        cm = ContentManager.getContentManager();
        iv = (ImageView) findViewById(R.id.invertImageView);
        cm.getNewImage(iv);
    }
    
    //called by either checkbox
    public void invert(View view) {
    	CheckBox invert = (CheckBox) findViewById(R.id.invert);
    	CheckBox grayscale = (CheckBox) findViewById(R.id.grayscale);
    	Bitmap color = cm.getNewImage();
    	if(invert.isChecked()) {
    		if(grayscale.isChecked()) {
    			Native.InvertGray(color);
    			cm.setImage(iv);
    		}
    		else {
    			Native.InvertColored(color);
    			cm.setImage(iv);
    		}
    	}
    	else {
    		if(grayscale.isChecked()) {
    			Native.ColorToGray(color);
    			cm.setImage(iv);
    		}
    		else {
    			cm.setImage(iv);
    		}
    	}
    }
 
    public void save(View view) {
        //TODO
    }
 
    public void saveAs(View view) {
    	//TODO
    }
 
    public void video(View view) {
    	//TODO
    }
 
    public void videoServer(View view) {
    	//TODO
    }
}
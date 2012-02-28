package com.overfitters;
 
import com.theoverfitters.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
 
public class BrightnessContrast extends Activity {
	private ContentManager cm;
	private TextView brText1, brText2, conText;
	private SeekBar brScroll, conScroll;
	private CharSequence brT1, brT2, conT;
	private ImageView iv;
	private int initBright, currBright;
	private int newVal, currVal;
	private boolean busy;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brightness_contrast);
        cm = ContentManager.getContentManager();
        iv = (ImageView) findViewById(R.id.brightnessContrastImageView);
        cm.getNewImage(iv);
        busy = false;
        //TODO change the logic of getgraybrightness
        initBright = Native.GetBrightness(cm.getNewImage());
        brScroll = (SeekBar) findViewById(R.id.brightnessSeekBar);
        brText1 = (TextView) findViewById(R.id.brightnessText1);
        brText2 = (TextView) findViewById(R.id.brightnessText2);
        conScroll = (SeekBar) findViewById(R.id.contrastSeekBar);
        conText = (TextView) findViewById(R.id.contrastText);
        brT1 = brText1.getText();
        brT2 = brText2.getText();
        conT = conText.getText();
        brText1.setText(brT1 + " " + 0 + "\t");
        brText2.setText(brT2 + " " + initBright);
        conText.setText(conT + " " + 1);
        
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
        //brightness(255);
}

	protected void brightness(int val) {
		val -= 255;
		brText1.setText(brT1 + " " + val + "\n");
		newVal = val;
		if(!busy) {
			busy = true;
			(new Thread() {
				public void run() {
					threadBrightness();
				}
			}).start();
		}
	}
	
	protected void threadBrightness() {
		currVal = newVal;
		//TODO modify this to change colored images
		Native.ModBrightness(cm.getNewImage(), currVal);
		
		//TODO mod
		currBright = Native.GetBrightness(cm.getImage());
		
		cm.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				finishBrightness();
			}
		});
	}
	
	//this may look like it has race conditions, but this is the GUI thread
	protected void finishBrightness() {
		brText2.setText(brT2 + " " + currBright);
		cm.setImage(iv);
		if(currVal != newVal) {
			(new Thread() {
				public void run() {
					threadBrightness();
				}
			}).start();
		}
		else
			busy = false;
	}

	protected void contrast(int oldVal) {
		//TODO this needs to reflect the change in brightness also (and vice versa)
		float val = oldVal/100.0f;
		conText.setText(conT + " " + val);
	}
	
	public void OnActivityResult(int requestCode, int resultCode, Intent i) {
		if(requestCode == ContentManager.LOCATION && resultCode == Activity.RESULT_OK) {
			String filename = (String)i.getExtras().get("filename");
			cm.save(filename);
		}
	}

	public void save(View view) {
        cm.save();
	}
 
    public void saveAs(View view) {
    	cm.saveAs(this);
    }
 
    public void video(View view) {
    	//TODO
    }
 
    public void videoServer(View view) {
    	//TODO
    }
}
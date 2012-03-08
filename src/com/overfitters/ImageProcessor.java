package com.overfitters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

abstract public class ImageProcessor extends Activity {
	private boolean busy, saved;
	protected ContentManager cm;
	private LinearLayout ll;
	protected Panel panel;
	
	//processes the given image in place, any thread
	abstract public void processImage(Bitmap imu, Bitmap mut);
	
	//resets this activity according to the current imuImage, on GUI
	abstract public void reset();
	
	//do updates to UI that need to be done immediately, on GUI
	abstract protected void firstUpdateUi();
	
	//do updates to UI that occur after image is processed (such as setting the image)
	//must also set contin, on GUI
	abstract protected boolean lastUpdateUi();
	
	//do not call this, use other one
	public void onCreate(Bundle bundle) {
		float x,y;
		x = 0;
		y = 0;
		Math.abs(x/y);
	}
	
	//does some basic creation, on GUI
	public void onCreate(Bundle bundle, int layout, int linearLayout) {
		super.onCreate(bundle);
		setContentView(layout);
		ll = (LinearLayout)findViewById(linearLayout);
		
		cm = ContentManager.getContentManager();
		
		panel = new Panel(this);
		
		panel.setImageBitmap(cm.getNewImage());
		ll.addView(panel, 0, cm.getLayoutParams());
		
		busy = false;
		saved = true;
	}
	
	//to be called by GUI thread when ready to start the processing process
	public void doProcess() {
		firstUpdateUi();
		if(!busy) {
			busy = true;
			(new Thread() {
				public void run() {
					doProcessThread();
				}
			}).start();
		}
	}
	
	//threaded work of image processing, not on GUI
	protected void doProcessThread() {
		processImage(cm.getImuImage(), cm.getImage());
		cm.runOnUiThread(new Runnable() {
			public void run() {
				panel.repaint();
				boolean contin = lastUpdateUi();
				if(contin)
					(new Thread() {
						public void run() {
							doProcessThread();
						}
					}).start();
				else {
					busy = false;
					saved = false;
				}
			}
		});
	}
	
	//on GUI
	public void save(View view) {
		if(!saved) {
			saved = true;
			cm.save(this);
		}
	}
	
	//on GUI
	public void saveAs(View view) {
		cm.saveAs(this);
	}
	
	//on GUI
	public void video(View view) {
		//TODO
	}
	
	//on GUI
	public void videoServer(View view) {
		//TODO
	}
	
	//on GUI
	public void onDestroy() {
		super.onDestroy();
		cm.onDestroy();
	}
}

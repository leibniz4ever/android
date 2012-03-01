package com.overfitters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

abstract public class ImageProcessor extends Activity {
	protected boolean busy, contin, saved;
	protected ContentManager cm;
	
	//processes the given image in place
	abstract public void processImage(Bitmap bm);
	
	//resets this activity according to the current imuImage
	abstract public void reset();
	
	//do updates to UI that need to be done immediately
	abstract protected void firstUpdateUi();
	
	//do updates to UI that occur after image is processed (such as setting the image)
	//must also set contin
	abstract protected void lastUpdateUi();
	
	//does some basic creation
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		cm = ContentManager.getContentManager();
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
	
	//threaded work of image processing
	protected void doProcessThread() {
		processImage(cm.getNewImage());
		cm.runOnUiThread(new Runnable() {
			public void run() {
				lastUpdateUi();
				if(contin)
					doProcessThread();
				else {
					busy = false;
					saved = false;
				}
			}
		});
	}
	
	public void save(View view) {
		if(!saved) {
			saved = true;
			cm.save(this);
		}
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
	public void onDestroy() {
		super.onDestroy();
		cm.onDestroy();
	}
}

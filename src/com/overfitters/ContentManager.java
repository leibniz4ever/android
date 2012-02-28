package com.overfitters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import com.theoverfitters.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

public class ContentManager {
	
	static final int LOCATION = 0;
	private static ContentManager cm;
	private Main main;
	private String path;
	private Bitmap jpg, bigImage, imuImage, image, def, loading;
	private boolean mainImageLoaded;
	
	private ContentManager(Main main) {
		this.main = main;
		def = Bitmap.createBitmap(1,1,Bitmap.Config.ALPHA_8);
		loading = BitmapFactory.decodeResource(main.getResources(), R.drawable.loading2);
	}

	public static ContentManager getContentManager(Main main) {
		if(cm == null)
			cm = new ContentManager(main);
		return cm;
	}
	
	public static ContentManager getContentManager() {
		return cm;
	}
	
	public void loadMainImage(String absPath) {
		mainImageLoaded = false;
		if(absPath == null)
			return;
		path = absPath;
		
		main.iv = (ImageView) main.findViewById(R.id.mainImageView);
		main.iv.setImageBitmap(loading);
		
		(new Thread() {
			public void run() {
				ContentManager cm = ContentManager.getContentManager();
				cm.loadMainImage();
			}
		}).start();
	}
	
	private void loadMainImage() {
		try {
			//clean up memory
			if(imuImage != null)
				imuImage.recycle();
			if(image != null)
				image.recycle();
			if(jpg != null)
				jpg.recycle();
			if(bigImage != null)
				bigImage.recycle();
			imuImage = null;
			image = null;
			jpg = null;
			bigImage = null;
			
			//now load in jpg
			FileInputStream fis = new FileInputStream(path);
			jpg = BitmapFactory.decodeStream(fis);
			fis.close();
			
			//convert jpg to bitmap and recycle jpg
			bigImage = jpg.copy(Bitmap.Config.ARGB_8888, true);
			jpg.recycle();
			jpg = null;
			
			//now create a more manageable form of the big image
			int width = bigImage.getWidth();
			int height = bigImage.getHeight();
			imuImage = Bitmap.createScaledBitmap(bigImage, width/4, height/4, true);
			
			//now dispose of large version
			bigImage.recycle();
			bigImage = null;
			
			//now, finally set small working copy as the image and redraw
			main.runOnUiThread(new Runnable() {
				public void run() {
					finishMainImageSet();
				}
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void finishMainImageSet() {
        main.iv.setImageBitmap(imuImage);
        mainImageLoaded = true;
	}
	
	public Bitmap getNewImage(ImageView iv) {
		iv.setImageBitmap(imuImage);
		if(image != null)
			image.recycle();
		image = null;
		image = imuImage.copy(Bitmap.Config.ARGB_8888, true);
		iv.setImageBitmap(image);
		return image;
	}

	public void loadDefaultMainImage() {
		String s = findMostRecentImage();
		if(s != null)
			loadMainImage(s);
		else
			main.iv.setImageBitmap(def);
	}

	public Bitmap getImage() {
		return image;
	}

    public static String findMostRecentImage() {
		File folder = new File(Environment.getExternalStorageDirectory()+"/DCIM/Camera/");
		String[] files = folder.list();
		if(files.length == 0)
			return null;
		int day = 0;
		int time = 0;
		int bestIndex = 0;
		for(int i = 0; i < files.length; i++) {
			String curr = files[i];
			if(curr.startsWith("IMG_")) {
				try {
					String tem = curr.substring(4,12);
					int td = Integer.parseInt(tem);
					tem = curr.substring(13, 19);
					int tt = Integer.parseInt(tem);
					if(td > day) {
						day = td;
						time = tt;
						bestIndex = i;
					}
					else if(td == day && tt > time) {
						time = tt;
						bestIndex = i;
					}
				}
				catch (Exception e) {
					
				}
			}
		}
		return folder + "/" + files[bestIndex];
	}

	public boolean hasMainImage() {
		return imuImage != null && mainImageLoaded;
	}

	public void runOnUiThread(Runnable runnable) {
		main.runOnUiThread(runnable);
	}

	public void setImage(ImageView iv) {
		iv.setImageBitmap(image);
	}

	public void saveAs(Activity activity) {
    	Intent i = new Intent(activity, SaveAs.class);
    	activity.startActivityForResult(i, LOCATION);
	}

	public void save() {
		this.save(this.path);
	}

	void save(String newPath) {
		//TODO
	}

	public Bitmap getNewImage() {
		image = imuImage.copy(Bitmap.Config.ARGB_8888, true);
		return image;
	}
}

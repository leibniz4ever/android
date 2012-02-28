package com.overfitters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import com.theoverfitters.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

public class ContentManager {
	
	private static ContentManager cm;
	private Main main;
	private String path;
	private Bitmap jpg, bigImage, imuImage, image, imuGrayImage, grayImage, smallGray, def, loading;
	private static final String temp = "temp.jpg";
	private FileInputStream fis;
	
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
		if(absPath == null)
			return;
		path = absPath;
		
		main.iv = (ImageView) main.findViewById(R.id.mainImageView);
		main.iv.setImageBitmap(loading);
		main.iv.invalidate();
		
		(new Thread() {
			public void run() {
				ContentManager cm = ContentManager.getContentManager();
				cm.loadMainImage();
			}
		}).start();
	}
	
	private void loadMainImage() {
		try {
			if(imuImage != null)
				imuImage.recycle();
			if(image != null)
				image.recycle();
			if(jpg != null)
				jpg.recycle();
			if(bigImage != null)
				bigImage.recycle();
			if(imuGrayImage != null)
				imuGrayImage.recycle();
			if(grayImage != null)
				grayImage.recycle();
			if(smallGray != null)
				smallGray.recycle();
			imuImage = null;
			image = null;
			jpg = null;
			bigImage = null;
			imuGrayImage = null;
			grayImage = null;
			smallGray = null;
			//System.gc();
			
			//now load in jpg
			fis = new FileInputStream(path);
			jpg = BitmapFactory.decodeStream(fis);
			fis.close();
			
			//convert jpg to bitmap and recycle jpg
			bigImage = jpg.copy(Bitmap.Config.ARGB_8888, true);
			jpg.recycle();
			jpg = null;
			//System.gc();
			
			//now create a more manageable form of the big image
			int width = bigImage.getWidth();
			int height = bigImage.getHeight();
			imuImage = Bitmap.createScaledBitmap(bigImage, width/2, height/2, true);
			//imuImage = Bitmap.createBitmap(width/2, height/2, Bitmap.Config.ARGB_8888);
			//String s = Native.reduceColored(bigImage, imuImage);
			
			//now create a gray scale version
			imuGrayImage = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
			Native.ToGray(bigImage, imuGrayImage);
			
			//now dispose of large version
			bigImage.recycle();
			bigImage = null;
			//System.gc();
			
			//now, finally set small working copy as the image and redraw
			main.runOnUiThread(new Runnable() {
				public void run() {
					finishImuImageSet();
				}
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void finishImuImageSet() {
        main.iv.setImageBitmap(imuImage);
        main.iv.invalidate();
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
	
	public boolean isTemp() {
		return path.equals(temp);
	}

	public void loadDefaultMainImage() {
		String s = findMostRecentImage();
		if(s != null)
			loadMainImage(s);
		else
			main.iv.setImageBitmap(def);
	}

	public Bitmap getNewBigGrayImage() {
		if(grayImage != null)
			grayImage.recycle();
		grayImage = null;
		grayImage = imuGrayImage.copy(Bitmap.Config.ALPHA_8, true);
		return this.grayImage;
	}

	public Bitmap getNewSmallGrayImage() {
		if(smallGray != null)
			smallGray.recycle();
		smallGray = null;
		smallGray = Bitmap.createBitmap(grayImage.getWidth()/2, grayImage.getHeight()/2, Bitmap.Config.ALPHA_8);
		Native.ReduceGray(grayImage, smallGray);
		return this.smallGray;
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
		return Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + files[bestIndex];
	}

	public Bitmap getSmallGrayImage() {
		if(smallGray == null)
			return getNewSmallGrayImage();
		return smallGray;
	}

	public Bitmap getImuGrayImage() {
		return this.imuGrayImage;
	}
}

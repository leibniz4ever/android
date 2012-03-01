package com.overfitters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.theoverfitters.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

public class ContentManager {
	private static ContentManager cm;
	private Main main;
	private String path, newPath;
	private Bitmap jpg, bigImage, imuImage, image, def, loading;
	private boolean mainImageLoaded;
	private ImageProcessor currActivity;
	private ProgressDialog progDialog;
	
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
	
	private void cleanMem() {
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
	}
	
	private void loadMainImage() {
		cleanMem();
		
		getNewBigImage();
		
		getNewImuImage();
		
		//now, finally set small working copy as the image and redraw
		main.runOnUiThread(new Runnable() {
			public void run() {
				finishMainImageSet();
			}
		});
	}
	
	private void getNewImuImage() {
		//now create a more manageable form of the big image
		int width = bigImage.getWidth();
		int height = bigImage.getHeight();
		imuImage = Bitmap.createScaledBitmap(bigImage, width/4, height/4, true);
		
		//now dispose of large version
		bigImage.recycle();
		bigImage = null;
	}

	private void getNewBigImage() {
		//now load in jpg
		jpg = BitmapFactory.decodeFile(path);
		
		//convert jpg to bitmap and recycle jpg
		bigImage = jpg.copy(Bitmap.Config.ARGB_8888, true);
		jpg.recycle();
		jpg = null;
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

	public void saveAs(ImageProcessor activity) {
    	Intent i = new Intent(activity, SaveAs.class);
    	this.currActivity = activity;
    	activity.startActivity(i);
	}

	//GUI thread
	public boolean save(ImageProcessor ip) {
		return this.save(ip, this.path);
	}

	//GUI thread
	private boolean save(ImageProcessor ip, String newPath) {
		if(!newPath.endsWith(".jpg"))
			newPath = newPath.trim() + ".jpg";
		
		if(!newPath.startsWith("/"))
			newPath = Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + newPath; 
		
		File file = new File(newPath);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			return false;
		}

		currActivity = ip;
		
		progDialog = ProgressDialog.show(currActivity, "", currActivity.getText(R.string.saving), true, true);
		
		this.newPath = newPath;
		
		cleanMem();
		
		(new Thread() {
			public void run() {
				finishSave();
			}
		}).start();
		
		return true;
	}
	
	public void finishSave() {
		getNewBigImage();
		
		currActivity.processImage(bigImage);
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(newPath);
			bigImage.compress(Bitmap.CompressFormat.JPEG, 85, fos);
			fos.close();
			
			path = newPath;
			
			getNewImuImage();
			
			ContentManager.this.runOnUiThread(new Runnable() {
				public void run() {
					main.iv.setImageBitmap(ContentManager.this.imuImage);
					currActivity.reset();
				}
			});
			
			progDialog.dismiss();
		} catch (FileNotFoundException e) {
			progDialog.dismiss();
		} catch (IOException e) {
			progDialog.dismiss();
		}
	}

	public Bitmap getNewImage() {
		image = imuImage.copy(Bitmap.Config.ARGB_8888, true);
		return image;
	}

	//GUI thread
	public boolean saveTo(String file) {
		return save(currActivity, file);
	}
	
	public void onDestroy() {
		currActivity = null;
	}
}

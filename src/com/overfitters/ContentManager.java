package com.overfitters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.theoverfitters.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.Display;
import android.widget.LinearLayout.LayoutParams;

public class ContentManager {
	private static ContentManager cm;
	private Main main;
	private String path, newPath;
	private Bitmap jpg, bigImage, imuImage, image, loading;
	private boolean mainImageLoaded;
	private ImageProcessor currActivity;
	private ProgressDialog progDialog;
	private int width, size, height;
	private LayoutParams layoutParams;
	
	//private constructor, either thread
	private ContentManager(Main main) {
		this.main = main;
		loading = BitmapFactory.decodeResource(main.getResources(), R.drawable.loading2);
		size = getSize(main);
		layoutParams = new LayoutParams(size,size,0.3f);
	}

	//this is to be called once for main, either thread
	protected static ContentManager getContentManager(Main main) {
		if(cm == null)
			cm = new ContentManager(main);
		return cm;
	}
	
	//generic use, either thread
	public static ContentManager getContentManager() {
		return cm;
	}
	
	//just lets the content manager dispose of the current reference, either thread
	public void onDestroy() {
		currActivity = null;
	}
	
	//GUI thread only
	public void loadDefaultMainImage() {
		String s = findMostRecentImage();
		if(s != null)
			loadMainImage(s);
	}
	
	//on GUI
	public void loadMainImage(String absPath) {
		mainImageLoaded = false;
		if(absPath == null)
			return;
		path = absPath;
		
		main.panel.setImageBitmap(loading);
		
		(new Thread() {
			public void run() {
				ContentManager cm = ContentManager.getContentManager();
				cm.loadMainImage();
			}
		}).start();
	}
	
	//on other thread
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
	
	//on GUI
	private void finishMainImageSet() {
		//TODO find solution
        main.panel.setImageBitmap(imuImage);
        mainImageLoaded = true;
	}
	
	//TODO ensure proper use of this
	//on either, note that none of the images being recycled should be active
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
	
	//on other
	private void getNewImuImage() {
		//now create a more manageable form of the big image
		int width = bigImage.getWidth();
		int height = bigImage.getHeight();
		int max = Math.max(width, height);
		int scale = max/size;
		if(max%size > 10)
			scale++;
		//scale*=2;
		//scale = 6;
		//scale = 4;
		this.width = width/scale;
		this.height = height/scale;
		imuImage = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
		Native.Compress(bigImage, imuImage);
		
		//now dispose of large version
		bigImage.recycle();
		bigImage = null;
	}

	//on other thread
	private void getNewBigImage() {
		//now load in jpg
		jpg = BitmapFactory.decodeFile(path);
		
		//convert jpg to bitmap and recycle jpg
		bigImage = jpg.copy(Bitmap.Config.ARGB_8888, true);
		jpg.recycle();
		jpg = null;
	}

	public Bitmap getImuImage() {
		return imuImage;
	}
	
	//on either
	public Bitmap getNewImage() {
		image = imuImage.copy(Bitmap.Config.ARGB_8888, true);
		return image;
	}

	//on either
	public Bitmap getImage() {
		return image;
	}

	//on either
    public static String findMostRecentImage() {
		File folder = new File(Environment.getExternalStorageDirectory()+"/DCIM/Camera/");
		String[] files = folder.list();
		if(files == null || files.length == 0)
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

    //on either
	public boolean hasMainImage() {
		return imuImage != null && mainImageLoaded;
	}
	
	//on other thread
	public void runOnUiThread(Runnable runnable) {
		main.runOnUiThread(runnable);
	}
	
	//GUI thread
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
	
	//other
	public void finishSave() {
		getNewBigImage();
		
		currActivity.processImage(bigImage, bigImage);
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(newPath);
			bigImage.compress(Bitmap.CompressFormat.JPEG, 85, fos);
			fos.close();
			
			path = newPath;
			
			getNewImuImage();
			
			ContentManager.this.runOnUiThread(new Runnable() {
				public void run() {
					main.panel.setImageBitmap(ContentManager.this.imuImage);
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

	//GUI thread
	public boolean saveTo(String file) {
		return save(currActivity, file);
	}
	
	//either thread
	private int getSize(Activity act) {
		Display display = act.getWindowManager().getDefaultDisplay();
		int width = display.getHeight();
		int height = display.getWidth();
		return Math.min(width, height);
	}
	
	public int getSize() {
		return size;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public LayoutParams getLayoutParams() {
		return layoutParams;
	}

	public Bitmap getBackupImage() {
		return this.imuImage.copy(Bitmap.Config.ARGB_8888, true);
	}
}

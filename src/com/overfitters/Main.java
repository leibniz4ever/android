package com.overfitters;

import com.theoverfitters.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

public class Main extends Activity {
    private static final int CAMERA_PICTURE = 0;
	private static final int GALLERY_PICTURE = 1;
	private ContentManager cm;
	protected ImageView iv;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       
		cm = ContentManager.getContentManager(this);
		cm.loadDefaultMainImage();
    }
    
    @Override
    public void onDestroy() {
    	//Android keeps it alive in some way shape or form otherwise
    	System.exit(0);
    }

    //called by button
    public void toCamera(View view) {
    	Intent i = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);//MediaStore.ACTION_IMAGE_CAPTURE);
    	//cm.loadMainImage(findMostRecentImage());
    	//i.setType("image/*");
    	startActivityForResult(i, CAMERA_PICTURE);
    }

	//called by button
    public void toGallery(View view) {
    	Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), GALLERY_PICTURE);
    }
    
    //called by button
    public void toAlg(View view) {
        //String down = Native.Down(Main.image);
        //int[] pixels = getPixels(Main.image);
        //for(int i = 0; i<pixels.length; i++) {
        //	pixels[i] = funct(pixels[i]);
        //}
        //int width = Main.image.getWidth();
        //int height = Main.image.getHeight();
        //try {
        //Main.image.setPixels(pixels, 0, width, 1, 1, width-1, height-1);
        //} catch(Exception e) {
        //	e.printStackTrace();
        //} catch (Error e) {
        //	e.printStackTrace();
        //}
    	Intent i = new Intent(this, Alg.class);
    	this.startActivity(i);
    }

	//called after an intent has returned its value
    public void onActivityResult(int requestCode, int resultCode, Intent i) {
    	if (requestCode == CAMERA_PICTURE) {
    		cm.loadMainImage(ContentManager.findMostRecentImage());
    	}
    	else if (requestCode == GALLERY_PICTURE && resultCode == Activity.RESULT_OK) {
    		cm.loadMainImage(getImagePath(i.getData()));
    	}
    }
    
    //from a forum
    public String getImagePath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
    
//    //this makes the bitmap much easier
//    private static int[] getPixels(Bitmap bm) {
//		int[] pixels;
//		int height = bm.getHeight();
//	    int width = bm.getWidth();
//	    pixels = new int[height * width];
//	    bm.getPixels(pixels, 0, width, 0, 0, width, height);
//	    return pixels;
//    }
}
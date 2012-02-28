package com.overfitters;

import android.graphics.Bitmap;

public class Native {

	//native functions
	
    //inverts a grayscale image
    native public static String InvertGray(Bitmap bitmap);
    
    //compresses the colored image by combining squares of size 2*2
    //TODO have size as a parameter
    native public static String ReduceColored(Bitmap bigImage, Bitmap image);
    
    //compresses the gray image by combining squares of size 2*2
    //TODO have size as a parameter
    native public static String ReduceGray(Bitmap bigImage, Bitmap image);
    
    //converts the colored image into gray
    native public static String ToGray(Bitmap coloredImage, Bitmap grayImage);
    
    //updates the colored image based on adding changes in brightness
    native public static String AddColor(Bitmap coloredImage, Bitmap grayImage);
    
    //copies the gray image into the colored image
    native public static String CopyGray(Bitmap coloredImage, Bitmap grayImage);
    
    //inverts the colors
    native public static String InvertColored(Bitmap coloredImage);
    
    //gets the brightness of the current image
    native public static int GetGrayBrightness(Bitmap gray);
    
    //sets the brightness of the current image
    native public static int ModGrayBrightness(Bitmap gray, int change);
    
    //load the native library
    static {
    	System.loadLibrary("Lib");
    }
}

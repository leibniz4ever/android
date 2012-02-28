package com.overfitters;

import android.graphics.Bitmap;

public class Native {

	//native functions in alphabetical order

    //copies the gray image into the colored image
    native public static String ColorToGray(Bitmap coloredImage);
    
    //gets the brightness of the current image
    native public static int GetBrightness(Bitmap gray);
    
    //inverts the colors
    native public static String InvertColored(Bitmap coloredImage);
    
    //converts and inverts a colored image into a grayscale image
    native public static String InvertGray(Bitmap colored);
    
    //sets the brightness of the current image
    native public static int ModBrightness(Bitmap gray, int change);
    
    //load the native library
    static {
    	System.loadLibrary("Lib");
    }
}

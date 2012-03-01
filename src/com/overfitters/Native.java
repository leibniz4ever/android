package com.overfitters;

import android.graphics.Bitmap;

public class Native {

	//native functions in alphabetical order

    //copies the gray image into the colored image
    native public static int ColorToGray(Bitmap coloredImage);
    
    //gets the brightness of the current image
    native public static int GetBrightness(Bitmap gray);
    
    //inverts the colors
    native public static int InvertColored(Bitmap coloredImage);
    
    //converts and inverts a colored image into a grayscale image
    native public static int InvertGray(Bitmap colored);
    
    //mods the brightness of the current image
    native public static int ModBrightness(Bitmap colored, int alpha);
    
    //mods the contrast by the given alpha
    //TODO fix this
    /*native*/ public static int ModContrast(Bitmap colored, float alpha) {
    	return -1;
    }
    
    //load the native library
    static {
    	System.loadLibrary("Lib");
    }
}

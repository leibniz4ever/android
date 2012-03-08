package com.overfitters;

import android.graphics.Bitmap;

public class Native {

	//native functions in alphabetical order, any thread, but avoid GUI

	//compresses/scales the big bitmap into a smaller one
	native public static int Compress(Bitmap big, Bitmap small);
	
	//simply copies a fresh view of imu on mut
	native public static int Copy(Bitmap imu, Bitmap mut);
	
    //copies sends it over in gray form
    native public static int ColorToGray(Bitmap imu, Bitmap mut);
    
    //gets the brightness of the current image
    native public static int GetBrightness(Bitmap mut);
    
    //inverts the colors
    native public static int InvertColored(Bitmap coloredImage, Bitmap mut);
    
    //converts and inverts a colored image into a grayscale image
    native public static int InvertGray(Bitmap colored, Bitmap mut);
    
    //mods the brightness of the mut image
    native public static int ModBrightness(Bitmap imu, Bitmap mut, int alpha);
    
    //mods the contrast by the given alpha
    //TODO fix this
    /*native*/ public static int ModContrast(Bitmap imu, Bitmap mut, float alpha) {
    	return -1;
    }
    
    //load the native library
    static {
    	System.loadLibrary("Lib");
    }
}

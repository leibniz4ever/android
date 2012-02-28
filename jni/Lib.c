#include <pthread.h>
#include <string.h>
#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <stdlib.h>
#include <ImProc.c>

//typedef struct 
//{
//    unsigned char gray;
//    unsigned char red;
//    unsigned char green;
//    unsigned char blue;
//} grgb;

//#define pixel struct grgb

//note that as an int it is 0xaabbggrr
struct rgba {	
	unsigned char red;
	unsigned char green;
	unsigned char blue;
	unsigned char alpha;
};

#ifdef pixel
#undef pixel
#endif
#define pixel struct rgba

jstring Java_com_overfitters_Native_InvertGray(JNIEnv *env, jobject thiz, jobject bitmap)
{
    AndroidBitmapInfo info;
    void *pixelsP;
    unsigned char *pixels;
    int width, height;
    
    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
        return (*env)->NewStringUTF(env, "Bad Info");
    }

    if (info.format != ANDROID_BITMAP_FORMAT_A_8) {
        return (*env)->NewStringUTF(env, "Wrong Info");
    }

    width = info.width;
    height = info.height;

    AndroidBitmap_lockPixels(env, bitmap, &pixelsP);

    pixels = (unsigned char *)pixelsP;

    Invert_Pixels(pixels, width, height);

    AndroidBitmap_unlockPixels(env, bitmap);

    return (*env)->NewStringUTF(env, "Everything is good!");
}

pixel avePixel(pixel a, pixel b, pixel c, pixel d) {
    pixel ret;
    ret.alpha = (a.alpha + b.alpha + c.alpha + d.alpha)/4;
    ret.red = (a.red + b.red + c.red + d.red)/4;
    ret.green = (a.green + b.green + c.green + d.green)/4;
    ret.blue = (a.blue + b.blue + c.blue + d.blue)/4;
    return ret;
}

jstring Java_com_overfitters_Native_ReduceColored(JNIEnv *env, jobject thiz, jobject big, jobject small)
{
    AndroidBitmapInfo infoB, infoS;
    void *pixelsBP, *pixelsSP;
    pixel *pixelsB, *pixelsS;
    int widthB, heightB, widthS, heightS;
    
    if (AndroidBitmap_getInfo(env, big, &infoB) < 0) {
        return (*env)->NewStringUTF(env, "Bad Info");
    }
    if (AndroidBitmap_getInfo(env, small, &infoS) < 0) {
        return (*env)->NewStringUTF(env, "Bad Info");
    }

    if (infoB.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return (*env)->NewStringUTF(env, "Wrong Info");
    }
    if (infoS.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return (*env)->NewStringUTF(env, "Wrong Info");
    }

    widthB = infoB.width;
    heightB = infoB.height;
    widthS = infoS.width;
    heightS = infoS.height;

    if(!(widthB/2 == widthS && heightB/2 == heightS))
        return (*env)->NewStringUTF(env, "Wrong Bitmap Sizes");

    AndroidBitmap_lockPixels(env, big, &pixelsBP);
    AndroidBitmap_lockPixels(env, small, &pixelsSP);

    pixelsB = (pixel *)pixelsBP;
    pixelsS = (pixel *)pixelsSP;

    int i,j;
    for(i = 0; i<widthS; i++) {
	for(j = 0; j<heightS; j++) {
	    pixelsS[i+j*widthS] = avePixel(pixelsB[2*i+2*j*widthB],pixelsB[2*i+1+2*j*widthB],pixelsB[2*i+(2*j+1)*widthB],pixelsB[2*i+1+(2*j+1)*widthB]);
	}
    }

    AndroidBitmap_unlockPixels(env, big);
    AndroidBitmap_unlockPixels(env, small);

    return (*env)->NewStringUTF(env, "Everything is good!");
}

jstring Java_com_overfitters_Native_ReduceGray(JNIEnv *env, jobject thiz, jobject big, jobject small)
{
    AndroidBitmapInfo infoB, infoS;
    void *pixelsBP, *pixelsSP;
    unsigned char *pixelsB, *pixelsS;
    int widthB, heightB, widthS, heightS;
    
    if (AndroidBitmap_getInfo(env, big, &infoB) < 0) {
        return (*env)->NewStringUTF(env, "Bad Info");
    }
    if (AndroidBitmap_getInfo(env, small, &infoS) < 0) {
        return (*env)->NewStringUTF(env, "Bad Info");
    }

    if (infoB.format != ANDROID_BITMAP_FORMAT_A_8) {
        return (*env)->NewStringUTF(env, "Wrong Info");
    }
    if (infoS.format != ANDROID_BITMAP_FORMAT_A_8) {
        return (*env)->NewStringUTF(env, "Wrong Info");
    }

    widthB = infoB.width;
    heightB = infoB.height;
    widthS = infoS.width;
    heightS = infoS.height;

    if(!(widthB/2 == widthS && heightB/2 == heightS))
        return (*env)->NewStringUTF(env, "Wrong Bitmap Sizes");

    AndroidBitmap_lockPixels(env, big, &pixelsBP);
    AndroidBitmap_lockPixels(env, small, &pixelsSP);

    pixelsB = (unsigned char *)pixelsBP;
    pixelsS = (unsigned char *)pixelsSP;

    int i,j;
    for(i = 0; i<widthS; i++) {
	for(j = 0; j<heightS; j++) {
	    pixelsS[i+j*widthS] = (pixelsB[2*i+2*j*widthB]+pixelsB[2*i+1+2*j*widthB]+pixelsB[2*i+(2*j+1)*widthB]+pixelsB[2*i+1+(2*j+1)*widthB])/4;
	}
    }

    AndroidBitmap_unlockPixels(env, big);
    AndroidBitmap_unlockPixels(env, small);

    return (*env)->NewStringUTF(env, "Everything is good!");
}

unsigned char getGray(pixel color) {
	return (color.red + color.green + color.blue)/3;
}

jstring Java_com_overfitters_Native_ToGray(JNIEnv *env, jobject thiz, jobject color, jobject gray) {
    AndroidBitmapInfo infoB, infoS;
    void *pixelsBP, *pixelsSP;
    pixel *pixelsB;
    unsigned char *pixelsS;
    int widthB, heightB, widthS, heightS;
    
    if (AndroidBitmap_getInfo(env, color, &infoB) < 0) {
        return (*env)->NewStringUTF(env, "Bad Info");
    }
    if (AndroidBitmap_getInfo(env, gray, &infoS) < 0) {
        return (*env)->NewStringUTF(env, "Bad Info");
    }

    if (infoB.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return (*env)->NewStringUTF(env, "Wrong Info");
    }
    if (infoS.format != ANDROID_BITMAP_FORMAT_A_8) {
        return (*env)->NewStringUTF(env, "Wrong Info");
    }

    widthB = infoB.width;
    heightB = infoB.height;
    widthS = infoS.width;
    heightS = infoS.height;

    if(!(widthB == widthS && heightB == heightS))
        return (*env)->NewStringUTF(env, "Wrong Bitmap Sizes");

    AndroidBitmap_lockPixels(env, color, &pixelsBP);
    AndroidBitmap_lockPixels(env, gray, &pixelsSP);

    pixelsB = (pixel *)pixelsBP;
    pixelsS = (unsigned char*)pixelsSP;

    int i,j;
    for(i = 0; i<widthS; i++) {
	for(j = 0; j<heightS; j++) {
	    pixelsS[i+j*widthS] = getGray(pixelsB[i+j*widthB]);
	}
    }

    AndroidBitmap_unlockPixels(env, color);
    AndroidBitmap_unlockPixels(env, gray);

    return (*env)->NewStringUTF(env, "Everything is good!");
}

jstring Java_com_overfitters_Native_AddColor(JNIEnv *env, jobject thiz, jobject color, jobject gray) {
	AndroidBitmapInfo infoC, infoG;
    	void *pixelsCP, *pixelsGP;
    	pixel *pixelsC;
    	unsigned char *pixelsG;
    	int widthC, heightC, widthG, heightG;
    	
    	if (AndroidBitmap_getInfo(env, color, &infoC) < 0) {
    		return (*env)->NewStringUTF(env, "Bad Info");
    	}
    	if (AndroidBitmap_getInfo(env, gray, &infoG) < 0) {
    		return (*env)->NewStringUTF(env, "Bad Info");
    	}
	
	if (infoC.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        	return (*env)->NewStringUTF(env, "Wrong Info");
    	}
    	if (infoG.format != ANDROID_BITMAP_FORMAT_A_8) {
        	return (*env)->NewStringUTF(env, "Wrong Info");
    	}

    	widthC = infoC.width;
    	heightC = infoC.height;
    	widthG = infoG.width;
    	heightG = infoG.height;

	AndroidBitmap_lockPixels(env, color, &pixelsCP);
    	AndroidBitmap_lockPixels(env, gray, &pixelsGP);

    	pixelsC = (pixel *)pixelsCP;
    	pixelsG = (unsigned char*)pixelsGP;

	int i, j;
	for(i = 0; i<widthC; i++) {
		for(j = 0; j<heightC; j++) {
			int index = i+j*widthC;
			pixel curr = pixelsC[index];
			int gray = pixelsG[index];
			int currGray = (curr.red + curr.green + curr.blue -2)/3;
			if(currGray > gray)
				currGray = (curr.red + curr.green + curr.blue + 2)/3;
			int alpha = gray - currGray;

			if(alpha == 0)
				continue;
			
			curr.alpha = 0xff;
			unsigned char *max = &curr.red;
			unsigned char *med = &curr.green;
			unsigned char *min = &curr.blue;
			unsigned char *temp;
			if(*min > *med) {
				temp = min;
				min = med;
				med = temp;
			}
			if(*med > *max) {
				temp = med;
				med = max;
				max = temp;
			}
			if(*min > *med) {
				temp = min;
				min = med;
				med = temp;
			}

			if(alpha > 0) {
				if(alpha + *max > 255) {
					alpha+=(*max+alpha-255+1)/2;
					*max = 255;
				}
				else
					*max += alpha;
				if(alpha + *med > 255) {
					alpha+=(*med+alpha-255);
					*med = 255;
				}
				else
					*med += alpha;
				if(alpha + *min > 255)
					*min = 255;
				else
					*min += alpha;
			}
			else {
				if(alpha + *min < 0) {
					alpha-=(0-*min-alpha-1)/2;
					*min = 0;
				}
				else
					*min += alpha;
				if(alpha + *med < 0) {
					alpha-=(0-*med-alpha);
					*med = 0;
				}
				else
					*med += alpha;
				if(alpha + *max < 0)
					*max = 0;
				else
					*max += alpha;
			}
			pixelsC[index] = curr;
		}
	}

	AndroidBitmap_unlockPixels(env, color);
    	AndroidBitmap_unlockPixels(env, gray);

	return (*env)->NewStringUTF(env, "Success!");
}

unsigned int getColored(unsigned char gray) {
	unsigned int ret;	
	ret = 0xff000000;
	ret+=gray<<16;
	ret+=gray<<8;
	ret+=gray<<0;
	return ret;
}

jstring Java_com_overfitters_Native_CopyGray(JNIEnv *env, jobject thiz, jobject color, jobject gray) {
    AndroidBitmapInfo infoB, infoS;
    void *pixelsBP, *pixelsSP;
    unsigned int *pixelsB;
    unsigned char *pixelsS;
    int widthB, heightB, widthS, heightS;
    
    if (AndroidBitmap_getInfo(env, color, &infoB) < 0) {
        return (*env)->NewStringUTF(env, "Bad Info");
    }
    if (AndroidBitmap_getInfo(env, gray, &infoS) < 0) {
        return (*env)->NewStringUTF(env, "Bad Info");
    }

    if (infoB.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return (*env)->NewStringUTF(env, "Wrong Info");
    }
    if (infoS.format != ANDROID_BITMAP_FORMAT_A_8) {
        return (*env)->NewStringUTF(env, "Wrong Info");
    }

    widthB = infoB.width;
    heightB = infoB.height;
    widthS = infoS.width;
    heightS = infoS.height;

    if(!(widthB == widthS && heightB == heightS))
        return (*env)->NewStringUTF(env, "Wrong Bitmap Sizes");

    AndroidBitmap_lockPixels(env, color, &pixelsBP);
    AndroidBitmap_lockPixels(env, gray, &pixelsSP);

    pixelsB = (unsigned int *)pixelsBP;
    pixelsS = (unsigned char*)pixelsSP;

    int i,j;
    for(i = 0; i<widthB; i++) {
	for(j = 0; j<heightB; j++) {
	    pixelsB[i+j*widthB] = getColored(pixelsS[i+j*widthS]);
	}
    }

    AndroidBitmap_unlockPixels(env, color);
    AndroidBitmap_unlockPixels(env, gray);

    return (*env)->NewStringUTF(env, "Everything is good!");
}

jstring Java_com_overfitters_Native_InvertColored(JNIEnv *env, jobject thiz, jobject color) {
    AndroidBitmapInfo info;
    void *pixelsP;
    unsigned int *pixels;
    int width, height;
    
    if (AndroidBitmap_getInfo(env, color, &info) < 0) {
        return (*env)->NewStringUTF(env, "Bad Info");
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return (*env)->NewStringUTF(env, "Wrong Info");
    }

    width = info.width;
    height = info.height;

    AndroidBitmap_lockPixels(env, color, &pixelsP);

    pixels = (unsigned int *)pixelsP;

    int i,j;
    for(i = 0; i<width; i++) {
	for(j = 0; j<height; j++) {
	    pixels[i+j*width] ^= 0x00ffffff;
	}
    }

    AndroidBitmap_unlockPixels(env, color);

    return (*env)->NewStringUTF(env, "Everything is good!");
}

jint Java_com_overfitters_Native_GetGrayBrightness(JNIEnv *env, jobject thiz, jobject bitmap)
{
    AndroidBitmapInfo info;
    void *pixelsP;
    unsigned char *pixels;
    int width, height;
    
    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
        return -1;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_A_8) {
        return -2;
    }

    width = info.width;
    height = info.height;

    AndroidBitmap_lockPixels(env, bitmap, &pixelsP);

    pixels = (unsigned char *)pixelsP;

	long sum = 0;
	int i;
    	for(i = 0; i<width*height; i++) {
		sum += pixels[i];
	}
	sum/=(width*height);

	jint ret = (jint)sum;

    AndroidBitmap_unlockPixels(env, bitmap);

    return ret;
}

jint Java_com_overfitters_Native_ModGrayBrightness(JNIEnv *env, jobject thiz, jobject bitmap, jint change)
{
    AndroidBitmapInfo info;
    void *pixelsP;
    unsigned char *pixels;
    int width, height;
    
    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
        return -1;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_A_8) {
        return -2;
    }

    	width = info.width;
    	height = info.height;
	int alpha = (int)change;

    	AndroidBitmap_lockPixels(env, bitmap, &pixelsP);

    	pixels = (unsigned char *)pixelsP;

	Modify_Brightness(pixels, alpha, width, height);

    	AndroidBitmap_unlockPixels(env, bitmap);

    	return 0;
}


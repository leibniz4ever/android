#include <pthread.h>
#include <string.h>
#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <stdlib.h>

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

//#define getGray(color) (0.3f * color.red + 0.59f*color.green + 0.11f*color.blue)
#define getGray(color) (color.red + color.green + color.blue)/3
pixel inline getColored(unsigned char gray) {
  pixel pix;
  pix.red = pix.green = pix.blue = gray;
  return pix;
}

jint Java_com_overfitters_Native_ColorToGray(JNIEnv *env, jobject thiz, jobject color) {
  AndroidBitmapInfo info;
  void *pixelsP;
  pixel *pixels;
  int width, height;
  
  if (AndroidBitmap_getInfo(env, color, &info) < 0) {
    return -1;
  }
  
  if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
    return -2;
  }
  
  width = info.width;
  height = info.height;
    
  AndroidBitmap_lockPixels(env, color, &pixelsP);

  pixels = (pixel *)pixelsP;

  pixel pix;
  int i,j;
  for(i = 0; i<width; i++) {
    for(j = 0; j<height; j++) {
      pixels[i+j*width] = getColored((unsigned char)getGray(pixels[i+j*width]));
    }
  }

  AndroidBitmap_unlockPixels(env, color);

  return 0;
}

jint Java_com_overfitters_Native_GetBrightness(JNIEnv *env, jobject thiz, jobject bitmap)
{
  AndroidBitmapInfo info;
  void *pixelsP;
  pixel *pixels;
  int width, height;
    
  if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
    return -1;
  }

  if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
    return -2;
  }

  width = info.width;
  height = info.height;

  AndroidBitmap_lockPixels(env, bitmap, &pixelsP);

  pixels = (pixel *)pixelsP;

  long sum = 0;
  int i;
  for(i = 0; i<width*height; i++) {
    sum += getGray(pixels[i]);
  }
  sum/=(width*height);

  jint ret = (jint)sum;

  AndroidBitmap_unlockPixels(env, bitmap);

  return ret;
}

jint Java_com_overfitters_Native_InvertColored(JNIEnv *env, jobject thiz, jobject color) {
  AndroidBitmapInfo info;
  void *pixelsP;
  unsigned int *pixels;
  int width, height;
    
  if (AndroidBitmap_getInfo(env, color, &info) < 0) {
    return -1;
  }

  if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
    return -2;
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

  return 0;
}

jint Java_com_overfitters_Native_InvertGray(JNIEnv *env, jobject thiz, jobject bitmap)
{
  AndroidBitmapInfo info;
  void *pixelsP;
  pixel *pixels;
  int width, height;
    
  if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
    return -1;
  }

  if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
    return -2;
  }

  width = info.width;
  height = info.height;

  AndroidBitmap_lockPixels(env, bitmap, &pixelsP);

  pixels = (pixel *)pixelsP;

  int i,j;
  for(i = 0; i<width; i++) {
    for(j = 0; j<height; j++) {
      pixels[i+j*width] = getColored(0xff ^ (unsigned char)getGray(pixels[i+j*width]));
    }
  }

  AndroidBitmap_unlockPixels(env, bitmap);

  return 0;
}

void inline modGray(unsigned char *color, int alpha) {
  *color = (*color+alpha > 255 ? 255 : (*color+alpha < 0 ? 0 : *color+alpha));
}
void inline modBright(pixel *pix, int alpha) {
  modGray(&pix->red,alpha);
  modGray(&pix->green,alpha);
  modGray(&pix->blue,alpha);
}

jint Java_com_overfitters_Native_ModBrightness(JNIEnv *env, jobject thiz, jobject bitmap, jint change)
{
  AndroidBitmapInfo info;
  void *pixelsP;
  pixel *pixels;
  int width, height;
    
  if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
    return -1;
  }

  if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
    return -2;
  }

  width = info.width;
  height = info.height;
  int alpha = (int)change;

  AndroidBitmap_lockPixels(env, bitmap, &pixelsP);

  pixels = (pixel *)pixelsP;

  int i;
  for(i = 0; i<width*height; i++) {
    modBright(&pixels[i],alpha);
  }

  AndroidBitmap_unlockPixels(env, bitmap);

  return 0;
}

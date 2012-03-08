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
unsigned char getGray(pixel color) {
  short temp = color.red;
  temp += color.green + color.blue;
  temp /= 3;
  return (unsigned char)temp;
}

pixel getColored(unsigned char gray) {
  pixel pix;
  pix.red = pix.green = pix.blue = gray;
  return pix;
}

int getWidth(JNIEnv *env, jobject bitmap) {
  AndroidBitmapInfo info;
  AndroidBitmap_getInfo(env, bitmap, &info);
  return info.width;
}

int getHeight(JNIEnv *env, jobject bitmap) {
  AndroidBitmapInfo info;
  AndroidBitmap_getInfo(env, bitmap, &info);
  return info.height;
}

pixel *getPixels(JNIEnv *env, jobject bitmap) {
  void *pixelsP;
  AndroidBitmap_lockPixels(env, bitmap, &pixelsP);
  return (pixel *)pixelsP;
}

void freePixels(JNIEnv *env, jobject bitmap) {
  AndroidBitmap_unlockPixels(env, bitmap);
}

jint Java_com_overfitters_Native_Compress(JNIEnv *env, jobject thiz, jobject imu, jobject mut) {
  pixel *pixelsI, *pixelsM;
  int widthI, heightI, widthM, heightM;
  
  widthI = getWidth(env, imu);
  heightI = getHeight(env, imu);

  widthM = getWidth(env, mut);
  heightM = getHeight(env, mut);
    
  pixelsI = getPixels(env, imu);
  pixelsM = getPixels(env, mut);

  int scale = widthI/widthM;
  if(heightI/heightM != scale)
    return -1;

  int eX,eY;
  eX = widthI%scale;
  eY = heightI%scale;

  eX/=2;
  eY/=2;

  //TODO, make a library call instead
  int i,j;
  for(i = 0; i<widthM; i++) {
    for(j = 0; j<heightM; j++) {
      int red, green, blue;
      red = green = blue = 0;
      int m,n;
      for(m = 0; m<scale; m++) {
	for(n = 0; n<scale; n++) {
	  int index = (scale*i+m+eX)+(scale*j+n+eY)*widthI;
	  red += pixelsI[index].red;
	  green += pixelsI[index].green;
	  blue += pixelsI[index].blue;
	}
      }
      red /= (scale*scale);
      green /= (scale*scale);
      blue /= (scale*scale);
      pixelsM[i+j*widthM].red = red;
      pixelsM[i+j*widthM].green = green;
      pixelsM[i+j*widthM].blue = blue;
      pixelsM[i+j*widthM].alpha = 0xff;
    }
  }

  freePixels(env, imu);
  freePixels(env, mut);

  return 0;
}

jint Java_com_overfitters_Native_Copy(JNIEnv *env, jobject thiz, jobject imu, jobject mut) {
  pixel *pixelsI, *pixelsM;
  int width, height;
  
  width = getWidth(env, imu);
  height = getHeight(env, imu);
  
  pixelsI = getPixels(env, imu);
  pixelsM = getPixels(env, mut);

  //TODO, make a library call instead
  pixel pix;
  int i,j;
  for(i = 0; i<width; i++) {
    for(j = 0; j<height; j++) {
      pixelsM[i+j*width] = pixelsI[i+j*width];
      pixelsM[i+j*width].alpha = 0xff;
    }
  }

  freePixels(env, imu);
  freePixels(env, mut);

  return 0;
}

jint Java_com_overfitters_Native_ColorToGray(JNIEnv *env, jobject thiz, jobject imu, jobject mut) {
  pixel *pixelsI, *pixelsM;
  int width, height;
  
  width = getWidth(env, imu);
  height = getHeight(env, imu);
    
  pixelsI = getPixels(env, imu);
  pixelsM = getPixels(env, mut);

  //TODO, make a library call instead
  int i,j;
  for(i = 0; i<width; i++) {
    for(j = 0; j<height; j++) {
      pixelsM[i+j*width] = getColored(getGray(pixelsI[i+j*width]));
    }
  }

  freePixels(env, imu);
  freePixels(env, mut);

  return 0;
}

jint Java_com_overfitters_Native_GetBrightness(JNIEnv *env, jobject thiz, jobject mut) {
  pixel *pixels;
  int width, height;
  
  width = getWidth(env, mut);
  height = getHeight(env, mut);
    
  pixels = getPixels(env, mut);

  //TODO, make a library call instead
  long sum = 0;
  int i,j;
  for(i = 0; i<width; i++) {
    for(j = 0; j<height; j++) {
      sum+= getGray(pixels[i+j*width]);
    }
  }

  sum/=width*height;

  freePixels(env, mut);

  return sum;
}

jint Java_com_overfitters_Native_InvertColored(JNIEnv *env, jobject thiz, jobject imu, jobject mut) {
  unsigned int *pixelsI, *pixelsM;
  int width, height;
  
  width = getWidth(env, imu);
  height = getHeight(env, imu);
    
  pixelsI = (unsigned int *)getPixels(env, imu);
  pixelsM = (unsigned int *)getPixels(env, mut);

  //TODO, make a library call instead
  int i,j;
  for(i = 0; i<width; i++) {
    for(j = 0; j<height; j++) {
      pixelsM[i+j*width] = pixelsI[i+j*width] ^ 0x00ffffff;
    }
  }

  freePixels(env, imu);
  freePixels(env, mut);

  return 0;
}

jint Java_com_overfitters_Native_InvertGray(JNIEnv *env, jobject thiz, jobject imu, jobject mut) {
  pixel *pixelsI, *pixelsM;
  int width, height;
  
  width = getWidth(env, imu);
  height = getHeight(env, imu);
    
  pixelsI = getPixels(env, imu);
  pixelsM = getPixels(env, mut);

  //TODO, make a library call instead
  int i,j;
  for(i = 0; i<width; i++) {
    for(j = 0; j<height; j++) {
      pixelsM[i+j*width] = getColored(0xff^getGray(pixelsI[i+j*width]));
    }
  }

  freePixels(env, imu);
  freePixels(env, mut);

  return 0;
}

unsigned char modBr(unsigned char color, int alpha) {
  short tmp = color;
  tmp += alpha;
  if(tmp < 0)
    tmp = 0;
  else if(tmp > 255)
    tmp = 255;
  return (unsigned char)tmp;
}

pixel modBrightness(pixel in, int alpha) {
  in.red = modBr(in.red, alpha);
  in.green = modBr(in.green, alpha);
  in.blue = modBr(in.blue, alpha);
  return in;
}

jint Java_com_overfitters_Native_ModBrightness(JNIEnv *env, jobject thiz, jobject imu, jobject mut, jint alpha) {
  pixel *pixelsI, *pixelsM;
  int width, height;
  int change = (int)alpha;
  
  width = getWidth(env, imu);
  height = getHeight(env, imu);
    
  pixelsI = getPixels(env, imu);
  pixelsM = getPixels(env, mut);

  //TODO, make a library call instead
  pixel pix;
  int i,j;
  for(i = 0; i<width; i++) {
    for(j = 0; j<height; j++) {
      pixelsM[i+j*width] = modBrightness(pixelsI[i+j*width], change);
    }
  }

  freePixels(env, imu);
  freePixels(env, mut);

  return 0;
}

//not done
jint Java_com_overfitters_Native_ModContrast(JNIEnv *env, jobject thiz, jobject imu, jobject mut) {
  pixel *pixelsI, *pixelsM;
  int width, height;
  
  width = getWidth(env, imu);
  height = getHeight(env, imu);
    
  pixelsI = getPixels(env, imu);
  if(imu != mut)
    pixelsM = getPixels(env, mut);
  else
    pixelsM = pixelsI;

  //TODO, make a library call instead
  pixel pix;
  int i,j;
  for(i = 0; i<width; i++) {
    for(j = 0; j<height; j++) {
      pixelsM[i+j*width] = pixelsI[i+j*width];
    }
  }

  freePixels(env, imu);
  if(imu != mut)
    freePixels(env, mut);

  return 0;
}

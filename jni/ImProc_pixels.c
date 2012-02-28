// Nathan Wingert - u0687928
// Image Processing Library Test Source

#include <stdio.h>
#include "ImProc_pixels.h"

struct pixel* greysToPixels(int** pixels, int width, int height, struct pixel* image)

  int i,j,k;
  i = j = k = 0;

  for(i; i < height; i++)
    {
      for(j; j < width; j++)
{
image[k].grey = bytes[j][i];
k++;
}
    }
  return image;
}

int** pixelsToGreys(struct pixel* image, int width, int height, int** pixels);
{
  int i,j,k;
  i = j = k = 0;

  for(i; i < height; i++)
    {
      for(j; j < width; j++)
{
bytes[j][i] = image[k].grey;
k++;
}
    }
  return image;
}

struct pixel* Invert_Pixels(struct pixel* image, int width, int height)
{
  int i = 0;
  int length = width * height;

  for(i; i < length; i++)
    {
      pixel p = image[i];
      p.grey = 255 - p.grey;
      p.red = 255 - p.red;
      p.green = 255 - p.green;
      p.blue = 255 - p.blue;
      image[i] = p;
    }
  return image;
}

struct pixel* Modify_Contrast(struct pixel* image, int alpha, int width, int height)
{
  int i = 0;
  int length = width * height;

  for(i; i < length; i++)
    {
      // compute new pixel data
      pixel p = image[i];
      p.grey = p.grey * alpha;
      p.red = p.red * alpha;
      p.green = p.green * alpha;
      p.blue = p.blue * alpha;
      
      // clamp pixel data to 0/255 (assuming 8 bit depth)
      p.grey = (p.grey > 255) ? 255 : p.grey;
      p.red = (p.red > 255) ? 255 : p.red;
      p.green = (p.green > 255) ? 255 : p.green;
      p.gblue = (p.blue > 255) ? 255 : p.blue;

      image[i] = p;
    }
  return image;
}

struct pixel* Modify_Brightness(struct pixel* image, int alpha, int width, int height)
{
  int i = 0;
  int length = width * height;

  for(i; i < length; i++)
    {
      // compute new pixel data
      pixel p = image[i];
      p.grey = p.grey * alpha;
      p.red = p.red * alpha;
      p.green = p.green * alpha;
      p.blue = p.blue * alpha;
      
      // clamp pixel data to 0/255 (assuming 8 bit depth)
      p.grey = (p.grey > 255) ? 255 : p.grey;
      p.red = (p.red > 255) ? 255 : p.red;
      p.green = (p.green > 255) ? 255 : p.green;
      p.gblue = (p.blue > 255) ? 255 : p.blue;

      image[i] = p;
    }
  return image;
}

struct pixel* Threshold(struct pixel* image, int alpha, int width, int height)
{
  int i = 0;
  int length = width * height;

  for(i; i < length; i++)
    {
      // compute new pixel data
      pixel p = image[i];
      
      // clamp pixel data to 0/255 (assuming 8 bit depth)
      p.grey = (p.grey > alpha) ? 255 : 0;
      p.red = (p.red > alpha) ? 255 : 0;
      p.green = (p.green > alpha) ? 255 : 0;
      p.gblue = (p.blue > alpha) ? 255 : 0;

      // assign new pixel info
      image[i] = p;
    }
  return image;
}

unsigned long** Histogram(struct pixel* image, int width, int height, unsigned long** histogram)
{
  int i = 0;
  int length = width * height;
 
  for(i; i < length; i++)
    {
      pixel p = image[i];

      int grey = p.grey;
      int red = p.red;
      int green = p.green;
      int blue = p.blue;

      histogram[0][grey] = histogram[0][grey] + 1;
      histogram[1][red] = histogram[1][red] + 1;
      histogram[2][green] = histogram[2][green] + 1;
      histogram[3][blue] = histogram[3][blue] + 1;
    }
  return histogram;
}

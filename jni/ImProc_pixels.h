struct pixel{
  char red;
  char green;
  char blue;
  char alpha;
};

// utility methods for testing
struct pixel* greysToPixels(int** pixels, int width, int height, struct pixel* image);
int** pixelsToGreys(struct pixel* image, int width, int height, int** pixels);


// point operations
struct pixel* Invert_Pixels(struct pixel* image, int width, int height);
struct pixel* Modify_Contrast(struct pixel* image, int alpha, int width, int height);
struct pixel* Modify_Brightness(struct pixel* image, int alpha, int width, int height);
struct pixel* Threshold(struct pixel* image, int alpha, int width, int height);

// analysis
unsigned long** Histogram(struct pixel* image, int width, int height, unsigned long** histogram);

// feature detection

#endif

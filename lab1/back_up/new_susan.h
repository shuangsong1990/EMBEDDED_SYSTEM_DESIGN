#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <sys/file.h>    /* may want to remove this line */

#define x_size 76
#define y_size 95
#define image_size  7220 /// 76 * 95
#define bt 20
#define  exit_error(IFB,IFC) { fprintf(stderr,IFB,IFC); exit(0); }


typedef unsigned char uchar;

//void susan_edges(unsigned char[],int r[],);
//void setup_brightness_lu(unsigned char[], int, int);
//void get_image(char[],unsigned char *);

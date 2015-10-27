#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <sim.sh>

#ifndef PPC
typedef int        TOTAL_TYPE; /* this is faster for "int" but should be "float" for large d masks */
#else
typedef float      TOTAL_TYPE; /* for my PowerPC accelerator only */
#endif

/*#define FOPENB*/           /* uncomment if using djgpp gnu C for DOS or certain Win95 compilers */
#define SEVEN_SUPP           /* size for non-max corner suppression; SEVEN_SUPP or FIVE_SUPP */
#define MAX_CORNERS   15000  /* max corners per frame */

/* ********** Leave the rest - but you may need to remove one or both of sys/file.h and malloc.h lines */

#define  exit_error(IFB,IFC) { fprintf(stderr,IFB,IFC); exit(0); }
#define  FTOI(a) ( (a) < 0 ? ((int)(a-0.5)) : ((int)(a+0.5)) )
typedef  unsigned char uchar;

#define X_SIZE 76
#define Y_SIZE 95
#define IMAGE_SIZE 7220 /*(76*95)*/

#define NR_IMGS     5
#define PROCESSORS 2
#define BARRIER_NUM 2ul

#define BT 20
#define DRAWING_MODE 0
#define MAX_NO_EDGES 2650

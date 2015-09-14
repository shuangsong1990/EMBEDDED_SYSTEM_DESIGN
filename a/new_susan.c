#include "new_susan.h"


void main(int argc, char *argv[]){
/*******vars*******************/
unsigned char in[image_size];
unsigned char bp[516];
unsigned char mid[image_size];
int r[image_size];
/******************************/


/*******functions calls*******************/

get_image(argv[1],in);

setup_brightness_lut(bp, 20, 6);

memset(mid, 100, x_size * y_size);

susan_edges(in,r,mid,bp,2650);

susan_thin(r,mid);

edge_draw(in,mid,0);

put_image(argv[2],in);

/*****************************************/

}




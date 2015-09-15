#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>


#define x_size 76
#define y_size 95
#define image_size  7220 /// 76 * 95
#define bt 20
#define  exit_error(IFB,IFC) { fprintf(stderr,IFB,IFC); exit(0); }

#define drawing_mode 0

import "c_double_handshake";
import "c_queue";
import "get_image";
import "put_image";
import "DetectEdges";
import "EdgeDraw";
import "SusanThin";


//const unsigned long SIZE = 7220 * sizeof(unsigned)

behavior susan(i_receiver start, unsigned char image_buffer[image_size], i_sender sd){
	int r [image_size];
	unsigned char mid[image_size];

	DetectEdges D(start, image_buffer, r, mid);
	SusanThin S(r, mid);
	EdgeDraw E(image_buffer, mid, drawing_mode, sd);

	
	void main(void){
		fsm{
			D: {goto S;}
			S: {goto E;}
			E: {goto D;}
				
		}	
	}
};


behavior Main(){
	const unsigned long SIZE = 7220;
	c_queue mal_image((SIZE));
	c_double_handshake start;
	unsigned char image_buffer[image_size];
	char inputfile[200] = "input_small.pgm";
	char outputfile[200] = "output_edge.pgm";
  	
	get_image stimulus(start,image_buffer,inputfile);
	susan susan_fsm(start, image_buffer, mal_image);
	put_image monitor(mal_image,outputfile);	


	int main(void){
		par{
		    stimulus.main();
		    susan_fsm.main();
		    monitor.main();
			}
		return 0;
	}

};

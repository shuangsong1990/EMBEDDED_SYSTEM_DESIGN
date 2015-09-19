#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>


#define x_size 76
#define y_size 95
#define image_size  7220 
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

behavior susan(i_receiver start, i_sender sd){
	int r [image_size];
	unsigned char image_buffer[image_size];
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


behavior stimulus(i_sender start, inout unsigned char in_sc[7220],in char filename[200]){
	unsigned int counter = 0;
	unsigned int i;
	get_image g(start,in_sc,filename);
  	
	void main(void){
		waitfor 1000;
		for(i = 0; i < 10; i++){
			g.main();
			waitfor 500;
		}
		exit(0);
	}	
};

behavior monitor(i_receiver rec, in char filename[200]){
	put_image p(rec, filename);
	void main(void){
		fsm{
			p: {goto p;}
		}
	}
};

/*

behavior Main(){
	const unsigned long SIZE = 7220*sizeof(unsigned char);
	unsigned char image_buffer[image_size];
	char inputfile[200] = "input_small.pgm";
	char outputfile[200] = "output_edge.pgm";
	c_double_handshake start;  	
	c_queue mal_image((SIZE));

	stimulus sti(start,image_buffer,inputfile);
	design susan_fsm(start, image_buffer, mal_image);
	monitor mon(mal_image,outputfile);	


	int main(void){
		par{
		    sti.main();
		    susan_fsm.main();
		    mon.main();
			}
		return 0;
	}

};

*/

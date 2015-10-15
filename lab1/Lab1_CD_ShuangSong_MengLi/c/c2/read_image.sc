#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>


#define x_size 76
#define y_size 95
#define image_size  7220 
#define bt 20
#define  exit_error(IFB,IFC) { fprintf(stderr,IFB,IFC); exit(0); }


import "c_double_handshake";
import "c_queue";


behavior rprocess(i_receiver start, inout unsigned char in_sc[7220], i_sender sd){
	void main(void){
		int handshake = 0;
		start.receive(&handshake, sizeof(unsigned int));
		sd.send(in_sc, 7220*sizeof(unsigned char));
	}		
};


behavior ReadImage(i_receiver start, inout unsigned char in_sc[7220], i_sender sd){
		
	rprocess r(start,in_sc,sd);
	
	void main(void){
		fsm{
			r: {goto r;}
		}
	}
};

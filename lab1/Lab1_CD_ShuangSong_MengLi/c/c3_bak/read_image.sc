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
import "c_type_define";

behavior rprocess(i_receiver start, inout unsigned char in_sc[7220], i_in_sender sd, i_in_sender se){
	void main(void){
		int handshake = 0;
		int i;
		start.receive(&handshake, sizeof(unsigned int));
//		sd.send(in_sc, 7220*sizeof(unsigned char));
		for(i = 0; i < image_size; i++ ){
			sd.send(in_sc[i]); ///////here new commit///
			se.send(in_sc[i]);
		}
		//printf("read_iamge fine\n");
	}		
};


behavior ReadImage(i_receiver start, inout unsigned char in_sc[7220], i_in_sender sd, i_in_sender se){
		
	rprocess r(start,in_sc,sd,se);
	
	void main(void){
		fsm{
			r: {goto r;}
		}
	}
};

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
import "c_type_define";

//const unsigned long SIZE = 7220 * sizeof(unsigned)


behavior DetectEdges_wp(
	i_in_receiver in_buffer,
	i_r_sender r_s,
	i_mid_sender mid_s)
{
	DetectEdges D(in_buffer, r_s, mid_s);

	void main(void){
		fsm{
			D: {goto D;}
		}
	}
};

behavior SusanThin_wp(
	i_r_receiver r_r,
	i_mid_receiver mid_r,
	i_mid_sender mid_s)
{

	SusanThin S(r_r, mid_r, mid_s);
	
	void main(void){

		fsm{
			S: {goto S;}
		}

	}

};

behavior EdgeDraw_wp(
	i_in_receiver in_buffer,
	i_mid_receiver mid_r,
	i_in_sender sd)
{
//	unsigned int drawing_mode = 0;

//	EdgeDraw E(in_buffer, mid_r, drawing_mode, sd);

	EdgeDraw E(in_buffer, mid_r, 0, sd);
	void main(void){

		fsm{
			E: {goto E;}
		}

	}
	
};


behavior susan(i_in_receiver buffer_in_d, i_in_receiver buffer_in_e,  i_in_sender buffer_out){
//	int r [image_size];
//	unsigned char image_buffer[image_size];
//	unsigned char mid[image_size];

	const unsigned long q_len = 7220;

	c_mid_queue mq(q_len);
	c_mid_queue mq2(q_len);
	c_r_queue rq(q_len);
		

	DetectEdges_wp D(buffer_in_d, rq, mq);
	SusanThin_wp S(rq, mq, mq2);
	EdgeDraw_wp E(buffer_in_e, mq2, buffer_out);
	
	void main(void){
		fsm{
			D: {goto S;}
			S: {goto E;}
			E: {goto D;}
				
		}	
/*
		par{
			D.main();
			S.main();
			E.main();
		}
*/
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


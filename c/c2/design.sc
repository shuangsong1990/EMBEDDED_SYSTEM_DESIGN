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
import "Susan";
import "ReadImage";
import "WriteImage";

import "monitor";
import "stimulus";



behavior Design(
	i_receiver start, 
	unsigned char image_buffer[image_size], 
	i_sender sd)
{
	const unsigned long SIZE = image_size * sizeof(unsigned char);
	c_queue data_buffer_in(SIZE), data_buffer_out;

	ReadImage 	R(start, image_buffer, c_queue);
	Susan 	  	S(data_buffer_in, data_buffer_out);
	WriteImage	W(data_buffer_out, sd);
	
	void main(void){
		par{
			R.main();
			S.main();
			W.main();
		}
	}

};


behavior Main{
	unsigned char image_buffer[image_size];
        char inputfile[200] = "input_small.pgm";
        char outputfile[200] = "output_edge.pgm";
	c_double_handshake start;
	c_double_handshake end;
	
	stimulus sti(start, image_buffer, inputfile);
	Design d(start, image_buffer, end);
	monitor mon(end, outputfile);
	
	int main(void){
		par{
			sti.main();
			d.main();
			mon.main();
		}
		return 0;
	}

};

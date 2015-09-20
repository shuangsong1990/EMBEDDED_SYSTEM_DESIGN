#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <string.h>
#include "sim.sh"

#define  exit_error(IFB,IFC) { fprintf(stderr,IFB,IFC); exit(0); }
#define x_size  76
#define y_size  95
#define image_size 7220
import "c_queue";
import "c_type_define";

behavior WriteImage(
	i_in_receiver rec,
	i_sender   sd)
{
	unsigned char out_sc[x_size * y_size];

	void main(void)
	{
		unsigned int i;
		while(1){
			for(i = 0; i < image_size; i++){
				rec.receive(out_sc+i);
			}
			

//			rec.receive(out_sc, x_size * y_size * sizeof(unsigned char));

			sd.send(out_sc, x_size * y_size * sizeof(unsigned char));
		}
	}

};

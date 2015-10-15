#include <stdlib.h>
#include <stdio.h>
#include "sim.sh"
#include <string.h>

#define x_size 76
#define y_size 95
#define image_size 7220


import "c_queue";
import "c_type_define";

behavior EdgeDraw(
	i_in_receiver in_buffer,
	i_mid_receiver  mid_r,
	in int drawing_mode,
	i_in_sender sd)
{
	unsigned char in_sc[image_size];
	unsigned char mid[image_size];
	void main(void)
	{

		int   i;
		unsigned char *inp, *midp;

		for (i = 0; i < image_size; i ++)
			in_buffer.receive(in_sc + i);

		for (i = 0; i < image_size; i ++)
			mid_r.receive(mid + i);
		
		if (drawing_mode==0)
		{
		/* mark 3x3 white block around each edge point */
			midp=mid;
			for (i=0; i<x_size*y_size; i++)
			{
				if (*midp<8)
	  		        {
		        		inp = in_sc + (midp - mid) - x_size - 1;
		        		*inp++=255; *inp++=255; *inp=255; inp+=x_size-2;
		        		*inp++=255; *inp++;     *inp=255; inp+=x_size-2;
		        		*inp++=255; *inp++=255; *inp=255;
		      		}
		      		midp++;
		    	}
		  }
		
		  /* now mark 1 black pixel at each edge point */
		  midp=mid;
		  for (i=0; i<x_size*y_size; i++)
		  {
		  	if (*midp<8)
		      		*(in_sc + (midp - mid)) = 0;
		    	midp++;
		  }

		  for (i = 0; i < image_size; i ++)
			sd.send(*(in_sc + i));

//		  sd.send(in_sc,7220*sizeof(unsigned char));
		 // printf("the end of E\n");
	}
};

#include <stdlib.h>
#include <stdio.h>
#include "sim.sh"
#include <string.h>

#define x_size 76
#define y_size 95
#define image_size 7220


import "c_queue";

behavior EdgeDraw(
	inout unsigned char in_sc[image_size],
	inout unsigned char mid[image_size],
	in int drawing_mode,
	i_sender sd)
{
	void main(void)
	{

		int   i;
		unsigned char *inp, *midp;
		
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
		  sd.send(in_sc,7220*sizeof(unsigned char));
		  printf("the end of E\n");
	}
};

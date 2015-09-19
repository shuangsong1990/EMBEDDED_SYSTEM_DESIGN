#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <string.h>


#define  exit_error(IFB,IFC) { fprintf(stderr,IFB,IFC); exit(0); }
#define x_size  76
#define y_size  95
#define image_size 7220
import "c_queue";

behavior put_image(i_receiver rec,in char filename[200]){
	
        unsigned char out_sc[x_size*y_size];
	
        void main(void)
	{
		FILE  *fd;
		rec.receive(out_sc, x_size * y_size * sizeof(unsigned char));	
		
		if ((fd=fopen(filename,"w")) == NULL)
		  	exit_error("Can't output image%s.\n",filename);
		
		fprintf(fd,"P5\n");
		fprintf(fd,"%d %d\n",x_size,y_size);
		fprintf(fd,"255\n");
		
		if(fwrite(out_sc,x_size * y_size,1,fd) != 1)
		  	exit_error("Can't write image %s.\n",filename);
		
		fclose(fd);
		exit(0);
	}
};

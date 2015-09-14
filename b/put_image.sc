#include <stdlib.h>
#include <stdio.h>
#include "sim.sh"
#include <string.h>

behavior put_image(
	in char filename[200],
	i_receiver receiver)
{
	int x_size = 76;
	int y_size = 95;
	unsigned char out_sc[x_size * y_size];
	void main(void)
	{
		receiver.receive(out_sc, x_size * y_size * sizeof(unsigned char));	
		FILE  *fd;
		
		if ((fd=fopen(filename,"w")) == NULL)
		  	exit_error("Can't output image%s.\n",filename);
		
		fprintf(fd,"P5\n");
		fprintf(fd,"%d %d\n",x_size,y_size);
		fprintf(fd,"255\n");
		
		if (fwrite(in,x_size*y_size,1,fd) != 1)
		  	exit_error("Can't write image %s.\n",filename);
		
		fclose(fd);
	}
};

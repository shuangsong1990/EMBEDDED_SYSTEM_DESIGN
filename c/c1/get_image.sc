#include <stdlib.h>
#include <stdio.h>
#include "sim.sh"
#include <string.h>


#define  exit_error(IFB,IFC) { fprintf(stderr,IFB,IFC); exit(0); }
#define x_size 76
#define y_size 95

import "c_double_handshake";

behavior get_image(
	i_sender start,
	inout unsigned char in_sc[7220],
	in char filename[200])
{

	int getint(FILE *fd)
	{
		int c, i;
		char dummy[10000];

		c = getc(fd);
		while (1) /* find next integer */
		{
			if (c=='#')    /* if we're at a comment, read to end of line */
			fgets(dummy,9000,fd);
			if (c==EOF)
				exit_error("Image %s not binary PGM.\n","is");
			if (c>='0' && c<='9')
				break;   /* found what we were looking for */
			c = getc(fd);
		}

		/* we're at the start of a number, continue until we hit a non-number */
		i = 0;
		while (1) {
			i = (i*10) + (c - '0');
			c = getc(fd);
			if (c==EOF) return (i);
			if (c<'0' || c>'9') break;
		}
		
		return (i);
	}

	void main(void)
	{
		FILE *fd;
		char header [100];
		int tmp;
		int handshake = 0;
		int i = 0;
		int total_comm = 10;

		if ((fd=fopen(filename,"r")) == NULL)
			exit_error("Can't input image %s.\n",filename);


		/* {{{ read header */

		header[0]=fgetc(fd);
		header[1]=fgetc(fd);

		if(!(header[0]=='P' && header[1]=='5'))
			exit_error("Image %s does not have binary PGM header.\n",filename);

  		tmp = getint(fd);
  		tmp = getint(fd);
  		tmp = getint(fd);

		if (fread(in_sc,1,x_size * y_size,fd) == 0)
			exit_error("Image %s is wrong size.\n",filename);

		fclose(fd);

		waitfor 1000;

		sim_time_string buf_start;

		printf("Time%4s: start sending images\n", time2str(buf_start, now()));

		for (i = 0; i < total_comm; i ++){
			start.send(&handshake, sizeof(unsigned int));				
			waitfor 500;
		}
	
	//	printf("the end of get_image\n");
	}
};

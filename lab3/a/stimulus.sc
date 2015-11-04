#include "stdio.h"
#include "susan.sh"

import "i_send";
import "i_receive";
import "i_sender";


behavior Stimulus(i_send start, inout uchar  image_buffer[IMAGE_SIZE], i_sender start_time_channel)
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


    void get_image(char filename[200], uchar  *image)
    {

        FILE  *fd;
        char header [100];
        int  x_size, y_size, tmp;
       

#ifdef FOPENB
        if ((fd=fopen(filename,"rb")) == NULL)
#else
        if ((fd=fopen(filename,"r")) == NULL)
#endif
            exit_error("Can't input image %s.\n",filename);

        /* {{{ read header */
        header[0]=fgetc(fd);
        header[1]=fgetc(fd);
        if(!(header[0]=='P' && header[1]=='5'))
            exit_error("Image %s does not have binary PGM header.\n",filename);

        x_size = getint(fd);
        y_size = getint(fd);
        tmp = getint(fd);
        // check image size
        if ((x_size != X_SIZE) || (y_size != Y_SIZE)) {
            fprintf(stderr, "Input picture does not match size of %dx%d\n", X_SIZE, Y_SIZE);
            fclose (fd);
            exit(1);
        }
      
        if (fread(image, 1, X_SIZE * Y_SIZE, fd) == 0)
            exit_error("Image %s is wrong size.\n",filename);
       fclose(fd);
    }


    void main(void) 
    {
        unsigned int n;
        char filename[100];
        sim_time start_time;

        sprintf(filename, "%s", "input_small.pgm");
        get_image(filename, image_buffer);
	for(n = 0; n < NR_IMGS; n++) {
	        printf("Starting process %d...\n", n+1);
                start.send();
	        start_time = now();
		start_time_channel.send(&start_time, sizeof(sim_time));
	        waitfor(1000);
	}       
    
    }

};

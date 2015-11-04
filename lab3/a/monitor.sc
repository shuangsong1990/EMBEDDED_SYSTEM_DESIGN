#include "susan.sh"

import "i_receive";
import "i_receiver";
import "i_send";

behavior Monitor(i_receiver img, i_receiver start_time_channel)
{

    void main (void)
    {
        FILE  *fd;
        uchar  image_buffer[IMAGE_SIZE];
        char filename[100];
        int n;
        sim_time start_time;
        for(n = 0; n < NR_IMGS; n++) 
        {
	    start_time_channel.receive(&start_time, sizeof(sim_time));
            sprintf(filename, "out.pgm");

#ifdef FOPENB
            if ((fd=fopen(filename,"wb")) == NULL) 
#else
            if ((fd=fopen(filename,"w")) == NULL) 
#endif
                exit_error("Can't output image%s.\n",filename);

            fprintf(fd,"P5\n");
            fprintf(fd,"%d %d\n", X_SIZE, Y_SIZE);
            fprintf(fd,"255\n");

            img.receive(image_buffer, IMAGE_SIZE*sizeof(char));
    
            if (fwrite(image_buffer,X_SIZE*Y_SIZE,1,fd) != 1)
                exit_error("Can't write image %s.\n",filename);

            fclose(fd);        
            printf ("Total process time: %llu us\n", (now() - start_time)/(1 MICRO_SEC));

        } 
	printf ("Current time: %llu us\n", (now())/ (1 MICRO_SEC));    
        exit(0);
    }


};


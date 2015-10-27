#include "susan.sh"

import "i_sender";
import "c_uchar7220write_queue";
import "slave_driver_read";

behavior WriteImage(i_slave_receiver slave_driver_read, i_sender out_image)
{
    void main(void) {

        uchar image_buffer[IMAGE_SIZE];

        while (true) {
            //in_image.receive(&image_buffer);
	    
	    slave_driver_read.receive(image_buffer);
            out_image.send(image_buffer, sizeof(image_buffer));
        }
    }
};

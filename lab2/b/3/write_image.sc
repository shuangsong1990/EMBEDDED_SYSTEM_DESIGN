#include "susan.sh"

import "i_sender";
//import "c_uchar7220write_queue";

import "HWBus";
import "SlaveDriveRead";

behavior WriteImage(i_sender out_image,  slave_receiver in_image)
{
    void main(void) {

        uchar image_buffer[IMAGE_SIZE];

        while (true) {
	    
	    in_image.receive(image_buffer);
            out_image.send(image_buffer, sizeof(image_buffer));
        }
    }
};

#include "susan.sh"

import "i_sender";
import "c_uchar7220_queue_s";

behavior WriteImage(i_uchar7220s_receiver in_image, i_sender out_image)
{

    void main(void) {
        
        uchar image_buffer[IMAGE_SIZE];
        
        while (true) {
            in_image.receive(&image_buffer);
            out_image.send(image_buffer, sizeof(image_buffer));       
        }
    }
         
}; 

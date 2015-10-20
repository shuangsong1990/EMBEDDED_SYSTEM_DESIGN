#include "susan.sh"

import "susan";
import "read_image";
import "write_image";
import "c_uchar7220_queue_sr";
import "c_uchar7220_queue_r";
import "c_uchar7220_queue_s";
import "os";

behavior ReadImage_wp(i_receive start, in uchar image_buffer[IMAGE_SIZE], i_uchar7220_sender out_image){
	ReadImage R(start, image_buffer, out_image);
	
	void main(void){
		fsm{
			R: {goto R;}
		}
	}
};


behavior WriteImage_wp(i_uchar7220_receiver in_image, i_sender out_image){
	WriteImage W(in_image, out_image);

	void main(void){
		fsm{
			W: {goto W;}
		}
	}
};

behavior Design(i_receive start, in uchar image_buffer[IMAGE_SIZE], i_sender out_image_susan, OSAPI rtos)
{

    OS rtos;

    c_uchar7220_queue_r in_image(1ul);
    c_uchar7220_queue_s out_image(1ul);
    
    ReadImage_wp read_image(start, image_buffer, in_image);

    PE1 pe1(in_image, out_image, rtos);

//    Susan susan(in_image, out_image);

    WriteImage_wp write_image(out_image, out_image_susan);

    void main(void) {
       par {
            read_image.main();
	    pe1.main();
//            susan.main();
            write_image.main();
        }
    }
    
};

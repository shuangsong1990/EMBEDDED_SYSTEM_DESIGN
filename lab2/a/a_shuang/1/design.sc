#include "susan.sh"

import "susan";
import "read_image";
import "write_image";
import "c_uchar7220_queue";

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


behavior Design(i_receive start, in uchar image_buffer[IMAGE_SIZE], i_sender out_image_susan)
{

    c_uchar7220_queue in_image(1ul);
    c_uchar7220_queue out_image(1ul);


    c_int7220_queue r(1ul);
    c_uchar7220_queue mid(1ul);
    c_uchar7220_queue image_edge_draw(1ul);

    
    ReadImage_wp read_image(start, image_buffer, in_image);
    //Susan susan(in_image, out_image);
   
    PE1 p1(in_image, r, mid, image_edge_draw);
    PE2 p2(r, mid, image_edge_draw, out_image);
 
    WriteImage_wp write_image(out_image, out_image_susan);

    void main(void) {
       par {
            read_image.main();
	    p1.main();
	    p2.main();
            //susan.main();
            write_image.main();
        }
    }
    
};




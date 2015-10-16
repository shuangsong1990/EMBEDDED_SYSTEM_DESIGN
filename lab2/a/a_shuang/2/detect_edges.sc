#include "susan.sh"
#include <stdio.h>

import "c_uchar7220_queue";
import "c_int7220_queue";
import "setup_brightness_lut";
import "susan_edges";


interface OSAPI
{
	void time_wait(unsigned long t);
};


channel OS implements OSAPI{
	Task current = 0;
	os_queue rdyq;	


	void time_wait(unsigned long t){
		waitfor(t);
	}

};

behavior DetectEdges(i_uchar7220_receiver in_image,  i_int7220_sender out_r, i_uchar7220_sender out_mid, i_uchar7220_sender out_image)
{

    uchar bp[516];
        
    SetupBrightnessLut setup_brightness_lut(bp);
    SusanEdges susan_edges(in_image, out_r, out_mid, bp, out_image);
    
    void main(void) {
        setup_brightness_lut.main(); 
        susan_edges.main();
    }

};

behavior Edges(i_uchar7220_receiver in_image,  i_int7220_sender out_r, i_uchar7220_sender out_mid, i_uchar7220_sender out_image)
{

    DetectEdges detect_edges(in_image,  out_r, out_mid, out_image);
    
    void main(void) {
        fsm{
            detect_edges: {goto detect_edges;}
        }
    }
};


#include "susan.sh"
#include <stdio.h>

import "c_uchar7220_queue";
import "c_int7220_queue";
import "setup_brightness_lut";
import "susan_edges";
import "os";


behavior DetectEdges(i_uchar7220_receiver in_image,  i_int7220_sender out_r, i_uchar7220_sender out_mid, i_uchar7220_sender out_image)
{

    uchar bp[516];
       
    OS rtos;
 
    struct Task sblut;
    struct Task sedge;

    SetupBrightnessLut setup_brightness_lut(bp, rtos);
    SusanEdges susan_edges(in_image, out_r, out_mid, bp, out_image);
    
    void main(void) {

	sblut = rtos.task_create("SBLUT");
	sedge = rtos.task_create("SEDGE");
	rtos.init(sblut);

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


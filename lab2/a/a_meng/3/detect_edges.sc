#include "susan.sh"

import "c_uchar7220_queue_s";
import "c_uchar7220_queue_r";
import "c_uchar7220_queue_sr";
import "c_int7220_queue_s";
import "c_int7220_queue_r";
import "c_int7220_queue_sr";
import "setup_brightness_lut";
import "susan_edges";
import "os";


behavior DetectEdges(i_uchar7220r_receiver in_image,  i_int7220sr_sender out_r, i_uchar7220sr_sender out_mid, i_uchar7220sr_sender out_image, OSAPI rtos)
{

    uchar bp[516];
        
    SetupBrightnessLut setup_brightness_lut(bp, rtos);
    SusanEdges susan_edges(in_image, out_r, out_mid, bp, out_image, rtos);

    struct Task sblut;
    struct Task sedge;
    
    void main(void) {
	sblut = rtos.task_create("SBLUT");
	sedge = rtos.task_create("SEDGE");
	rtos.init(sblut);
        setup_brightness_lut.main(); 
	rtos.init(sedge);
        susan_edges.main();
    }

};

interface DE{
    void init(void);
    void main(void);
};

behavior Edges(i_uchar7220r_receiver in_image,  i_int7220sr_sender out_r, i_uchar7220sr_sender out_mid, i_uchar7220sr_sender out_image, OSAPI rtos) implements DE
{

    struct Task me;

    DetectEdges detect_edges(in_image,  out_r, out_mid, out_image, rtos);

    void init(void){
	me = rtos.task_create("SEDGE");
	rtos.push_t(me);
    }
    
    void main(void) {
	rtos.task_activate(me);
	printf("activate! current id is %d\n", me.id);
        detect_edges.main();
	rtos.task_terminate();
	printf("terminate! current id is %d\n", me.id);
    }
};


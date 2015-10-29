#include "susan.sh"


import "c_uchar7220_queue_r";
import "c_uchar7220_queue_s";
import "c_uchar7220_queue_sr";
import "c_int7220_queue_r";
import "c_int7220_queue_s";
import "c_int7220_queue_sr";
import "setup_brightness_lut";
import "susan_edges";
import "os";
import "init";

behavior DetectEdges(i_uchar7220r_receiver in_image, i_int7220sr_sender out_r, i_uchar7220sr_sender out_mid, i_uchar7220sr_sender out_image, OSAPI rtos)
{

    uchar bp[516];
        
    SetupBrightnessLut setup_brightness_lut( bp, rtos);
    SusanEdges susan_edges(in_image, out_r, out_mid, bp, out_image, rtos);
    
    void main(void) {
        setup_brightness_lut.main();
	printf("SETUP BRIGHTNESS DONE\n"); 
        susan_edges.main();
	printf("SUSAN EDGES DONE\n"); 
    }

};

behavior Edges(i_uchar7220r_receiver in_image,  i_int7220sr_sender out_r, i_uchar7220sr_sender out_mid, i_uchar7220sr_sender out_image, OSAPI rtos) implements INIT
{


    struct Task task;
    DetectEdges detect_edges(in_image, out_r, out_mid, out_image, rtos);


    void init(void){
	task = rtos.task_create("EDGES");
	rtos.push_t(task);
	return;
    }
    
    void main(void) {

	rtos.task_activate(task);

        fsm{
            detect_edges: {goto detect_edges;}
        }
	printf("escape fsm of detect edges\n");
	rtos.task_terminate();
    }
};


#include "susan.sh"

import "c_uchar7220_queue_r";
import "c_uchar7220_queue_s";
import "c_uchar7220_queue_sr";
import "c_int7220_queue_r";
import "c_int7220_queue_s";
import "c_int7220_queue_sr";

import "detect_edges";
import "susan_thin";
import "edge_draw";

     
behavior Susan(i_uchar7220r_receiver in_image, i_uchar7220s_sender out_image, OSAPI rtos) implements INIT
{

    c_int7220sr_queue r(1ul, rtos);
    c_uchar7220sr_queue mid(1ul, rtos);
    c_uchar7220sr_queue mid_edge_draw(1ul, rtos);
    c_uchar7220sr_queue image_edge_draw(1ul, rtos);

    struct Task task;

    Edges edges(in_image, r, mid, image_edge_draw, rtos);
    Thin thin(r, mid, mid_edge_draw, rtos);
    Draw draw(image_edge_draw, mid_edge_draw, out_image, rtos);

    void init(void){
	task = rtos.task_create("PE1");
	//rtos.push_t(task);
	rtos.init(task);
	return;
    }
        
    void main(void)
    {

	init();
	edges.init();
	thin.init();
	draw.init();

	printf("susan: par start\n");
	task = rtos.par_start();

        par {
            edges;
            thin;
            draw;
        }      

	rtos.par_end(task);

	rtos.task_terminate();

	return;
    }
   
};   

behavior PE1(i_uchar7220r_receiver in_image, i_uchar7220s_sender out_image, OSAPI rtos){

    Susan susan(in_image, out_image, rtos);

    void main(void){
	susan.main();
    }
};

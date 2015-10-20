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
import "os";

behavior TASK_PE1(i_uchar7220r_receiver in_image, i_uchar7220s_sender out_image, OSAPI rtos){


    c_int7220sr_queue r(1ul, rtos);
    c_uchar7220sr_queue mid(1ul, rtos);
    c_uchar7220sr_queue mid_edge_draw(1ul, rtos);
    c_uchar7220sr_queue image_edge_draw(1ul, rtos);

    Edges edges(in_image, r, mid, image_edge_draw, rtos);
    Thin thin(r, mid, mid_edge_draw, rtos);
    Draw draw(image_edge_draw, mid_edge_draw, out_image, rtos);

    struct Task my_t;

    struct Task e;
    struct Task t;
    struct Task d;


    void main(void){

	//e = rtos.task_create("edges");
	//t = rtos.task_create("thin");
	//d = rtos.task_create("draw"):

	//rtos.init(e);
	edges.init();
	thin.init();
	draw.init();

	my_t = rtos.par_start();
	
	par{
	    edges;
	    thin;
	    draw;
	}

//	rtos.par_end(my_t);

    }

};

behavior PE1(i_uchar7220r_receiver in_image, i_uchar7220s_sender out_image, OSAPI rtos){
    TASK_PE1 task_pe1(in_image, out_image, rtos);

    void main(void){

	fsm{
	    task_pe1: {goto task_pe1;}
	}

    }
};

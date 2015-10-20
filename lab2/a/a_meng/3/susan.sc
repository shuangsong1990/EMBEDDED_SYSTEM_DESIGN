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

     
behavior Susan(i_uchar7220_receiver in_image, i_uchar7220_sender out_image) 
{

    OS rtos;

    c_int7220_queue_sr r(1ul);
    c_uchar7220_queue_sr mid(1ul);
    c_uchar7220_queue_sr mid_edge_draw(1ul);
    c_uchar7220_queue_sr image_edge_draw(1ul);

    Edges edges(in_image, r, mid, image_edge_draw, rtos);
    Thin thin(r, mid, mid_edge_draw, rtos);
    Draw draw(image_edge_draw, mid_edge_draw, out_image, rtos);
        
    void main(void)
    {
        par {
            edges;
            thin;
            draw;
        }      
    }
   
};   

behavior TASK_PE1(i_uchar7220_receiver_r in_image, i_uchar7220_sender_s out_image, OSAPI rtos){

//    struct Task sedge;
//    struct Task sthin;
//    struct Task sdraw;

    c_int7220_queue_sr r(1ul, rtos);
    c_uchar7220_queue_sr mid(1ul, rtos);
    c_uchar7220_queue_sr mid_edge_draw(1ul, rtos);
    c_uchar7220_queue_sr image_edge_draw(1ul, rtos);

    Edges edges(in_image, r, mid, image_edge_draw, rtos);
    Thin thin(r, mid, mid_edge_draw, rtos);
    Draw draw(image_edge_draw, mid_edge_draw, out_image, rtos);

    struct Task my_t;

    void main(void){

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

behavior PE1(i_uchar7220_receiver_r in_image, i_uchar7220_sender_s out_image, OSAPI rtos){
    TASK_PE1 task_pe1(in_image, out_image, rtos);

    void main(void){

	fsm{
	    task_pe1: {goto task_pe1;}
	}

    }
};

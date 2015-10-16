#include "susan.sh"

import "c_uchar7220_queue";
import "c_int7220_queue";

import "detect_edges";
import "susan_thin";
import "edge_draw";

     
behavior Susan(i_uchar7220_receiver in_image, i_uchar7220_sender out_image) 
{

    c_int7220_queue r(1ul);
    c_uchar7220_queue mid(1ul);
    c_uchar7220_queue mid_edge_draw(1ul);
    c_uchar7220_queue image_edge_draw(1ul);

    Edges edges(in_image, r, mid, image_edge_draw);
    Thin thin(r, mid, mid_edge_draw);
    Draw draw(image_edge_draw, mid_edge_draw, out_image);
        
    void main(void)
    {
        par {
            edges;
            thin;
            draw;
        }      
    }
   
};   

behavior PE1(i_uchar7220_receiver in_image, i_int7220_sender out_r, i_uchar7220_sender out_mid, i_uchar7220_sender out_image){
	Edges edges(in_image, out_r, out_mid, out_image);
	
	void main(void){
		fsm{
			edges: {goto edges;}
		}
	}
};


behavior PE2(i_int7220_receiver in_r, i_uchar7220_receiver in_mid, i_uchar7220_receiver in_image, i_uchar7220_sender out_image){

	c_uchar7220_queue mid_edge_draw(1ul);

	Thin thin(in_r, in_mid, mid_edge_draw);
	Draw draw(in_image, mid_edge_draw, out_image);

	void main(void){
		par {
		    thin;
		    draw;
		}
	}

};

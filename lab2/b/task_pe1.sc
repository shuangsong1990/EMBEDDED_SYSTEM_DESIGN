#include "susan.sh"

import "c_uchar7220_queue";
import "c_int7220_queue";

import "c_uchar7220read_queue";
import "c_uchar7220write_queue";

import "detect_edges";
import "susan_thin";
import "edge_draw";
import "os";

import "init";
     
behavior TASK_PE1(i_uchar7220read_receiver in_image, i_uchar7220write_sender out_image, OSAPI os) implements Init
{
    c_int7220_queue r(1ul, os);
    c_uchar7220_queue mid(1ul, os);
    c_uchar7220_queue mid_edge_draw(1ul, os);
    c_uchar7220_queue image_edge_draw(1ul, os);

    Edges edges(in_image, r, mid, image_edge_draw, os);
    Thin thin(r, mid, mid_edge_draw, os);
    Draw draw(image_edge_draw, mid_edge_draw, out_image, os);

	Task *task;

    void init(void) {
        task = os.task_create("pe1");
    }


    void main(void)
    {
        init();
        edges.init();
        thin.init();
        draw.init();

        task=os.par_start();
        par{
            edges;
            thin;
            draw;
        }
        os.par_end(task);

        os.task_terminate();
    }
};


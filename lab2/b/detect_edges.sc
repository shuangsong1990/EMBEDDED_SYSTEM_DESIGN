#include "susan.sh"

import "c_uchar7220_queue";
import "c_int7220_queue";
import "setup_brightness_lut";
import "susan_edges";
import "os";
import "init";
import "read";

import "c_uchar7220read_queue";

import "HWBus";
import "master_driver_read";

behavior DetectEdges(i_master_receiver master_driver_read,  i_int7220_sender out_r, i_uchar7220_sender out_mid, i_uchar7220_sender out_image, OSAPI os)
{

    uchar bp[516];

    SetupBrightnessLut setup_brightness_lut(bp,os);
    SusanEdges susan_edges(master_driver_read, out_r, out_mid, bp, out_image, os);

    void main(void) {
	susan_edges.read_image();
        setup_brightness_lut.main();
        susan_edges.main();

    }

};

behavior Edges(i_master_receiver master_driver_read,  i_int7220_sender out_r, i_uchar7220_sender out_mid, i_uchar7220_sender out_image, OSAPI os) implements Init
{
    Task *task;

    DetectEdges detect_edges(master_driver_read,  out_r, out_mid, out_image, os);

    void init(void) {
        task = os.task_create("edge");
    }

    void main(void) {
        os.task_activate(task);
        fsm{
            detect_edges: {goto detect_edges;}
        }
        os.task_terminate();
    }
};


#include "susan.sh"

import "c_uchar7220_queue";
import "c_int7220_queue";
import "setup_brightness_lut";
import "susan_edges";
import "os";
import "init";

import "c_uchar7220read_queue";

behavior DetectEdges(i_uchar7220read_receiver in_image, i_int7220_sender out_r, i_uchar7220_sender out_mid, i_uchar7220_sender out_image, OSAPI os)
{

    uchar bp[516];
    uchar image_buffer[IMAGE_SIZE];

    SetupBright setup(in_image, image_buffer, bp, os);
    SusanEdges susan_edges(image_buffer, out_r, out_mid, bp, out_image, os);

    void main(void) {
        setup.main();
        susan_edges.main();
    }

};

behavior Edges(i_uchar7220read_receiver in_image,  i_int7220_sender out_r, i_uchar7220_sender out_mid, i_uchar7220_sender out_image, OSAPI os) implements Init
{
    Task *task;

    DetectEdges detect_edges(in_image,  out_r, out_mid, out_image, os);

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


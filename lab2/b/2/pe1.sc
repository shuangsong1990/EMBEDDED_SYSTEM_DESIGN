#include "susan.sh"

//import "c_uchar7220_queue";
import "c_int7220_queue";

import "detect_edges";
import "susan_thin";
import "edge_draw";

import "os";
import "task_pe1";

import "c_uchar7220read_queue";
import "c_uchar7220write_queue";

behavior PE1(i_uchar7220read_receiver in_image, i_uchar7220write_sender out_image, OSAPI os)
{

	TASK_PE1 task_pe1(in_image, out_image, os);

    void main(void)
    {

        task_pe1;
    }

};


#include "susan.sh"

//import "c_uchar7220_queue";
//import "c_int7220_queue";

import "detect_edges";
import "susan_thin";
import "edge_draw";

import "os";
import "HWBus";
import "task_pe1";

import "MasterDriveRead";
import "MasterDriveWrite";

//import "c_uchar7220read_queue";
//import "c_uchar7220write_queue";

behavior PE1(OSAPI os, IMasterHardwareBus hdbus)
{

    MasterDriveRead master_driver_read(hdbus, os);
    MasterDriveWrite master_driver_write(hdbus, os);

    //TASK_PE1 task_pe1(in_image, out_image, os);
	
    TASK_PE1 task_pe1(master_driver_read, master_driver_write, os);

    void main(void)
    {
        task_pe1;
    }

};


#include "susan.sh"
//#include "c_uchar7220_queue"

//import "c_uchar7220_queue";
//import "c_int7220_queue";

import "detect_edges";
import "susan_thin";
import "edge_draw";

import "os";
import "task_pe1";

//import "c_uchar7220read_queue";
//import "c_uchar7220write_queue";

//import "c_uchar7220ORG_queue";
import "HWBus";
import "master_driver_read";
import "master_driver_write";

behavior PE1(IMasterHardwareBus hardware_bus, OSAPI os)
{
	//need two original queues, for detectedges and edgedraw.
	//c_uchar7220ORG_queue driver_in(1ul);
	//c_uchar7220ORG_queue driver_out(1ul);
	//need two master driver, for detectedges and edgedraw.---pe1_task
	MasterDriverRead master_driver_read(hardware_bus, os);
	MasterDriverWrite master_driver_write(hardware_bus, os); 

	TASK_PE1 task_pe1( master_driver_read, os, master_driver_write);

    void main(void)
    {
        task_pe1;
    }

};


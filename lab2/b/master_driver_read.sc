#include "susan.sh"
//#include "c_uchar7220_queue"

import "HWBus";
import "os";

interface i_master_receiver					
{									
    void receive(unsigned char *data);						
};

channel MasterDriverRead(IMasterHardwareBus hardware_bus, OSAPI os)implements i_master_receiver
{

	bit[15:0] addr = 0000000000000000b;

    void receive(unsigned char *data)
    {
	Task *task;
	task=os.pre_wait();
	hardware_bus.MasterSyncReceive0();

//hardware_bus.print();

	hardware_bus.MasterRead(addr, data, 7220);
	os.post_wait(task);
    }
};


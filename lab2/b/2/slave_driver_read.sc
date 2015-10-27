#include "susan.sh"
//#include "c_uchar7220_queue"

import "HWBus";

interface i_slave_receiver					
{									
    void receive(unsigned char *data);						
};

channel SlaveDriverRead(ISlaveHardwareBus hardware_bus)implements i_slave_receiver
{

	bit[15:0] addr = 0000000000000001b;


    void receive(unsigned char *data)
    {
	hardware_bus.SlaveSyncSend1();
	hardware_bus.SlaveRead(addr, data, 7220);
    }
};


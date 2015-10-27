#include "susan.sh"

//import "c_uchar7220ORG_queue";
import "HWBus";

interface i_slave_sender					
{									
    void send(unsigned char *data);						
};

channel SlaveDriverWrite(ISlaveHardwareBus hardware_bus)implements i_slave_sender
{

	bit[15:0] addr = 0000000000000000b;


    void send(unsigned char *data)
    {
	hardware_bus.SlaveSyncSend0();

//hardware_bus.print();
	hardware_bus.SlaveWrite(addr, data, 7220);
    }
};


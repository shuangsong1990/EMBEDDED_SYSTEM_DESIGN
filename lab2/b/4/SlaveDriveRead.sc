#include "susan.sh"

import "HWBus";


interface slave_receiver{
	void receive(unsigned char *data);
};

channel SlaveDriveRead(ISlaveHardwareBus hdbus) implements slave_receiver{

	bit[15:0] addr = 0000000000000010b;

	void receive(unsigned char *data){
		hdbus.SlaveSyncSend1();
		hdbus.SlaveRead(addr, data, 7220);
	}
};

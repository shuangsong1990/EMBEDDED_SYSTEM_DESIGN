#include "susan.sh"

import "HWBus";


interface slave_sender{
	void send(unsigned char *data);
};

channel SlaveDriveWrite(ISlaveHardwareBus hdbus) implements slave_sender{

	bit[15:0] addr = 0000000000000001b;

	void send(unsigned char *data){
		hdbus.SlaveSyncSend0();
		hdbus.SlaveWrite(addr, data, 7220);
	}
};

#include "susan.sh"
import "os";
import "HWBus";

interface master_sender{

    void send (unsigned char* data);

};

channel MasterDriveWrite (IMasterHardwareBus i_sender, OSAPI os) implements master_sender{

    bit [15:0] addr = 0000000000000010b;

    void send (unsigned char* data){

	Task *task;
	task = os.pre_wait();
	i_sender.MasterSyncReceive1();
	i_sender.MasterWrite(addr, data, 7220);
	os.post_wait(task);

    }

};

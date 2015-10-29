#include "susan.sh"
import "os";
import "HWBus";

interface master_receiver{

    void receive(uchar* data);

};

channel MasterDriveRead(IMasterHardwareBus hdbus, OSAPI os) implements master_receiver{

    bit[15:0] addr = 000000000000001b;
    Task *task;
    
    void receive(uchar* data){

	task = os.pre_wait();
	hdbus.MasterSyncReceive0();
	hdbus.MasterRead(addr, data, 7220);
	os.post_wait(task);
    }

};

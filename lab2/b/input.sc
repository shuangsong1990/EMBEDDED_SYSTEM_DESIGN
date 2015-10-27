#include "susan.sh"

import "i_receive";
import "c_uchar7220read_queue";

import "read_image";
import "HWBus";
import "slave_driver_write";

behavior INPUT(i_receive start, in uchar image_buffer[IMAGE_SIZE], ISlaveHardwareBus hardware_bus)
{
	SlaveDriverWrite slave_driver_write(hardware_bus); 
	ReadImage read_image(start, image_buffer, slave_driver_write);

    void main(void) {
        par{
            read_image.main();
        }
    }
};

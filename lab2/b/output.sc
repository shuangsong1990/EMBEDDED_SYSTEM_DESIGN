#include "susan.sh"

import "i_sender";
import "c_uchar7220write_queue";

import "write_image";
import "slave_driver_read";

behavior OUTPUT(ISlaveHardwareBus hardware_bus, i_sender out_image)
{
   SlaveDriverRead slave_driver_read(hardware_bus);  
   WriteImage write_image(slave_driver_read, out_image);

    void main(void) {
        par{
            write_image.main();
        }
    }
};

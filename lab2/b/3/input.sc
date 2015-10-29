#include "susan.sh"

import "i_receive";
import "c_uchar7220read_queue";

import "read_image";

import "HWBus";

import "SlaveDriveWrite";

behavior INPUT(i_receive start, in uchar image_buffer[IMAGE_SIZE], ISlaveHardwareBus hdbus)
{
    
    SlaveDriveWrite slave_drive_write(hdbus);

    ReadImage read_image(start, image_buffer, slave_drive_write);

    void main(void) {
        par{
            read_image.main();
        }
    }
};

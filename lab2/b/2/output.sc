#include "susan.sh"

import "i_sender";
import "c_uchar7220write_queue";

import "write_image";

import "HWBus";

import "SlaveDriveRead";

behavior OUTPUT(i_uchar7220write_receiver in_image, i_sender out_image, ISlaveHardwareBus hdbus)
{

    SlaveDriveRead slave_drive_read(hdbus);

    WriteImage write_image(out_image, slave_drive_read);

    void main(void) {
        par{
            write_image.main();
        }
    }
};

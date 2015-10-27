#include "susan.sh"

import "i_sender";
import "c_uchar7220write_queue";

import "write_image";

behavior OUTPUT(i_uchar7220write_receiver in_image, i_sender out_image)
{
    WriteImage write_image(in_image, out_image);

    void main(void) {
        par{
            write_image.main();
        }
    }
};

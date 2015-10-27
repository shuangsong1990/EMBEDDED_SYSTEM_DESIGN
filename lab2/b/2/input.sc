#include "susan.sh"

import "i_receive";
import "c_uchar7220read_queue";

import "read_image";

behavior INPUT(i_receive start, in uchar image_buffer[IMAGE_SIZE], i_uchar7220read_sender out_image)
{
	ReadImage read_image(start, image_buffer, out_image);

    void main(void) {
        par{
            read_image.main();
        }
    }
};

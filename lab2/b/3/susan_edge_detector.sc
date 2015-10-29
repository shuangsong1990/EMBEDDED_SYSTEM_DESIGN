#include "susan.sh"

import "c_double_handshake_signal";
import "c_double_handshake";
import "c_queue";
import "design";
import "stimulus";
import "monitor";

behavior Main
{
    c_queue start_time(1000ul);
    uchar image_buffer[IMAGE_SIZE];
    c_double_handshake_signal start;
    c_double_handshake out_image;

    Stimulus stimulus(start, image_buffer, start_time);
    Design design(start, image_buffer, out_image);
    Monitor monitor(out_image, start_time);

    int main(void) {
        par {
            stimulus;
            design;
            monitor;
        }

        return 0;
    }

};

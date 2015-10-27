#include "susan.sh"
import "c_uchar7220_queue"; //Need another Queue(Receive)!!
import "c_uchar7220read_queue";
import "os";
import "init";

behavior SetupBrightnessLutThread(uchar bp[516], in int thID, OSAPI os) implements Init
{
    Task *task;

    void init(void) {
        task = os.task_create("sbl_t");
    }


    void main(void) {
        int   k;
        float temp;
        int thresh, form;

        thresh = BT;
        form = 6;

        os.task_activate(task);
        //for(k=-256;k<257;k++)
        for(k=(-256)+512/PROCESSORS*thID; k<(-256)+512/PROCESSORS*thID+512/PROCESSORS+1; k++){
            //waitfor(2700);  //insertion for the timing based on the performance profiling
            temp=((float)k)/((float)thresh);
            temp=temp*temp;
            if (form==6)
                temp=temp*temp*temp;
            temp=100.0*exp(-temp);
            bp[(k+258)] = (uchar) temp;

            os.time_wait(2700);
        }
        os.task_terminate();
    }

};

behavior SetupBrightnessLut_ReadInput(i_uchar7220read_receiver in_image, uchar in_image_buffer[IMAGE_SIZE])
{
    void main(void) {
        in_image.receive(&in_image_buffer);
    }
};

behavior SetupBrightnessLut(uchar bp[516], OSAPI os)
{
    Task *task;

    SetupBrightnessLutThread setup_brightness_thread_0(bp, 0, os);
    SetupBrightnessLutThread setup_brightness_thread_1(bp, 1, os);

    void main(void) {
        setup_brightness_thread_0.init();
        setup_brightness_thread_1.init();

        task = os.par_start();
        par {
            setup_brightness_thread_0;
            setup_brightness_thread_1;
        }
        os.par_end(task);
    }
};

behavior SetupBright(i_uchar7220read_receiver in_image, uchar image_buffer[IMAGE_SIZE], uchar bp[516], OSAPI os)
{
    SetupBrightnessLut_ReadInput setup_read_input(in_image, image_buffer);
    SetupBrightnessLut setup(bp, os);

    void main(void) {
        fsm {
            setup_read_input: goto setup;
            setup: {}
        }
    }
};


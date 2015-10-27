#include "susan.sh"
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

behavior SetupBrightnessLut(uchar bp[516], OSAPI os) implements Init
{
    Task *task;
    SetupBrightnessLutThread setup_brightness_thread_0(bp, 0, os);
    SetupBrightnessLutThread setup_brightness_thread_1(bp, 1, os);

    void init(void) {
        task = os.task_create("sbl");
    }

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


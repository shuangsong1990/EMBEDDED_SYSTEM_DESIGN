#include "susan.sh"
import "os";
import "init";

behavior SetupBrightnessLutThread(uchar bp[516], in int thID, OSAPI rtos) implements INIT
{
    struct Task task;
 
    void init(void){
	task = rtos.task_create("SLUT");
	rtos.push_t(task);
	return;
    }
       
    void main(void) {
        int   k;
        float temp;
        int thresh, form;
        
        thresh = BT;
        form = 6;

	rtos.task_activate(task);

        //for(k=-256;k<257;k++)
       for(k=(-256)+512/PROCESSORS*thID; k<(-256)+512/PROCESSORS*thID+512/PROCESSORS+1; k++){

	    rtos.time_wait(2700); /////waitfor statement in LUT
        
            temp=((float)k)/((float)thresh);
            temp=temp*temp;
            if (form==6)
                temp=temp*temp*temp;
            temp=100.0*exp(-temp);
            bp[(k+258)] = (uchar) temp;
        }

	rtos.task_terminate();

	return;

    }

};
 
behavior SetupBrightnessLut(uchar bp[516], OSAPI rtos)
{
       
    SetupBrightnessLutThread setup_brightness_thread_0(bp, 0, rtos);
    SetupBrightnessLutThread setup_brightness_thread_1(bp, 1, rtos);

    struct Task task;
       
    void main(void) {

        setup_brightness_thread_0.init();
        setup_brightness_thread_1.init();

      printf("set up brightness: par start\n");
	task = rtos.par_start();

        par {
            setup_brightness_thread_0;
            setup_brightness_thread_1;
        }

	rtos.par_end(task);
	return;
	
    }

};


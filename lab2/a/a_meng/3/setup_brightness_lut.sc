#include "susan.sh"
import "os";


interface SLUT{
	void init(void);
	void main(void);	
};


behavior SetupBrightnessLutThread(uchar bp[516], in int thID, OSAPI rtos) implements SLUT
{

    struct Task me;

    void init(void){
	me = rtos.task_create("SLUT");
	rtos.push_t(me);
    }
       
    void main(void) {
        int   k;
        float temp;
        int thresh, form;

	rtos.task_activate(me);
        
        thresh = BT;
        form = 6;

        //for(k=-256;k<257;k++)
       for(k=(-256)+512/PROCESSORS*thID; k<(-256)+512/PROCESSORS*thID+512/PROCESSORS+1; k++){

	   /// waitfor(2700); /////waitfor statement in LUT

	    printf("compute thread id is %d \n", me.id);
	    rtos.time_wait(2700); /// only for 2        
        
            temp=((float)k)/((float)thresh);
            temp=temp*temp;
            if (form==6)
                temp=temp*temp*temp;
            temp=100.0*exp(-temp);
            bp[(k+258)] = (uchar) temp;
        }

	printf("current task to be termnidate is %d\n", me.id);
	rtos.task_terminate();
    }

};
 
behavior SetupBrightnessLut(uchar bp[516], OSAPI rtos)
{
       
    SetupBrightnessLutThread setup_brightness_thread_0(bp, 0, rtos);
    SetupBrightnessLutThread setup_brightness_thread_1(bp, 1, rtos);

    struct Task my_t;
       
    void main(void) {

       	setup_brightness_thread_0.init();
    	setup_brightness_thread_1.init();
	
	my_t = rtos.par_start();

	par{

            setup_brightness_thread_0.main();
            setup_brightness_thread_1.main();

	}

//	rtos.par_end(my_t);
    }

};


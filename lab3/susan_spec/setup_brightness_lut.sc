#include "susan.sh"

behavior SetupBrightnessLutThread(uchar bp[516], in int thID)
{
       
    void main(void) {
        int   k;
        float temp;
        int thresh, form;
        
        thresh = BT;
        form = 6;

        //for(k=-256;k<257;k++)
       for(k=(-256)+512/PROCESSORS*thID; k<(-256)+512/PROCESSORS*thID+512/PROCESSORS+1; k++){
            temp=((float)k)/((float)thresh);
            temp=temp*temp;
            if (form==6)
                temp=temp*temp*temp;
            temp=100.0*exp(-temp);
            bp[(k+258)] = (uchar) temp;
        }
    }

};
 
behavior SetupBrightnessLut(uchar bp[516])
{
       
    SetupBrightnessLutThread setup_brightness_thread_0(bp, 0);
    SetupBrightnessLutThread setup_brightness_thread_1(bp, 1);
       
    void main(void) {
        par {
            setup_brightness_thread_0;
            setup_brightness_thread_1;
        }
    }

};


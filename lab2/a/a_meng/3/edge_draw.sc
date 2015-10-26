#include "susan.sh"

import "c_uchar7220_queue_sr";
import "c_uchar7220_queue_s";
import "os";

interface EDTPA{

    void init(void);
    void main(void);
};

behavior EdgeDrawThread_PartA(uchar image_buffer[7220], uchar mid[7220], in int thID, OSAPI rtos) implements EDTPA
{

    struct Task me;

    void init(void){
	me = rtos.task_create("EDTPA");
	rtos.push_t(me);
    }

    void main(void) {
    
        int   i;
        uchar *inp, *midp;
        int drawing_mode;

	rtos.task_activate(me);
        
        drawing_mode = DRAWING_MODE;
        if (drawing_mode==0)
        {
            /* mark 3x3 white block around each edge point */
            midp=mid + IMAGE_SIZE/PROCESSORS *thID;
            for (i=X_SIZE*Y_SIZE/PROCESSORS*thID; i<X_SIZE*Y_SIZE/PROCESSORS*(thID+1) + (thID+1==PROCESSORS && X_SIZE*Y_SIZE%PROCESSORS!=0 ?X_SIZE*Y_SIZE%PROCESSORS : 0); i++)
            {
		printf("EDPTA i is %d, maximum is %d, id is %d \n", i, X_SIZE*Y_SIZE/PROCESSORS*(thID+1) + (thID+1==PROCESSORS && X_SIZE*Y_SIZE%PROCESSORS!=0 ?X_SIZE*Y_SIZE%PROCESSORS : 0), thID);
		rtos.time_wait(12000000);
//		waitfor(12000000); //////waitfor statements
                if (*midp<8) 
                {
                    inp = image_buffer + (midp - mid) - X_SIZE - 1;
                    *inp++=255; *inp++=255; *inp=255; inp+=X_SIZE-2;
                    *inp++=255; *inp++;     *inp=255; inp+=X_SIZE-2;
                    *inp++=255; *inp++=255; *inp=255;
                }
                midp++;
            }
        }
	printf("jump out of EDPTA");
	rtos.task_terminate();
     }   
   
};    

interface EDTPB{

    void init(void);
    void main(void);

};

behavior EdgeDrawThread_PartB(uchar image_buffer[7220], uchar mid[7220], in int thID, OSAPI rtos) implements EDTPB
{

    struct Task me;

    void init(void){
	me = rtos.task_create("EDPTB");
	rtos.push_t(me);
    }

    void main(void) {
    
        int   i;
        uchar  *midp;
        int drawing_mode;

	rtos.task_activate(me);
        
        drawing_mode = DRAWING_MODE;
     
        /* now mark 1 black pixel at each edge point */
        midp=mid+ IMAGE_SIZE/PROCESSORS *thID;
        //for (i=0; i<X_SIZE*Y_SIZE; i++)
        for (i=X_SIZE*Y_SIZE/PROCESSORS*thID; i<X_SIZE*Y_SIZE/PROCESSORS*(thID+1) + (thID+1==PROCESSORS && X_SIZE*Y_SIZE%PROCESSORS!=0 ?X_SIZE*Y_SIZE%PROCESSORS : 0); i++)
        {
	    rtos.time_wait(12000000);
//	    waitfor(12000000); ///waitfor statements
            if (*midp<8) 
                *(image_buffer+ (midp - mid)) = 0;
            midp++;
        }

	rtos.task_terminate();
    }
    
};    

behavior EdgeDraw_ReadInput(i_uchar7220sr_receiver in_image, i_uchar7220sr_receiver in_mid, uchar image_buffer[IMAGE_SIZE], uchar mid[IMAGE_SIZE])
{
    void main(void) {
        in_mid.receive(&mid);
	printf("receive mid in edge draw\n");
        in_image.receive(&image_buffer);
	printf("receive image in edge draw\n");
    }      
};

behavior EdgeDraw_WriteOutput(uchar image_buffer[IMAGE_SIZE],  i_uchar7220s_sender out_image)
{
    void main(void) {
        out_image.send(image_buffer);
	printf("edge draw terminate\n");
    }
};

behavior EdgeDraw_PartA(uchar image_buffer[7220], uchar mid[7220], OSAPI rtos)
{

    EdgeDrawThread_PartA edge_draw_a_thread_0(image_buffer, mid, 0, rtos);
    EdgeDrawThread_PartA edge_draw_a_thread_1(image_buffer, mid, 1, rtos);

    struct Task my_t;
    
    void main(void) {

        edge_draw_a_thread_0.init();
        edge_draw_a_thread_1.init();

	my_t = rtos.par_start();

	par{
            edge_draw_a_thread_0;
            edge_draw_a_thread_1;
	}

	printf("end of EDPTA\n");

//	rtos.par_end(my_t);
    }     
};

behavior EdgeDraw_PartB(uchar image_buffer[7220], uchar mid[7220], OSAPI rtos)
{

    EdgeDrawThread_PartB edge_draw_b_thread_0(image_buffer, mid, 0, rtos);
    EdgeDrawThread_PartB edge_draw_b_thread_1(image_buffer, mid, 1, rtos);

    struct Task my_t;
    struct Task edtp2;
    
    void main(void) {

	edtp2 = rtos.task_create("EDPT2");

	rtos.init(edtp2);

        edge_draw_b_thread_0.init();
        edge_draw_b_thread_1.init();

	my_t = rtos.par_start();

	par{
            edge_draw_b_thread_0;
            edge_draw_b_thread_1;
	}

//	rtos.par_end(my_t);
    }     
};


behavior EdgeDraw(i_uchar7220sr_receiver in_image, i_uchar7220sr_receiver in_mid,  i_uchar7220s_sender out_image, OSAPI rtos)
{

    
    uchar image_buffer[IMAGE_SIZE];
    uchar mid[IMAGE_SIZE];         
    
    EdgeDraw_ReadInput edge_draw_read_input(in_image, in_mid, image_buffer, mid);
    EdgeDraw_WriteOutput edge_draw_write_output(image_buffer, out_image);
    EdgeDraw_PartA edge_draw_a(image_buffer, mid, rtos);
    EdgeDraw_PartB edge_draw_b(image_buffer, mid, rtos);
    
    void main(void) { 
	fsm{
            edge_draw_read_input: goto edge_draw_a;
            edge_draw_a: goto edge_draw_b;
            edge_draw_b: goto edge_draw_write_output;
            edge_draw_write_output: {}
	}
    }     
    
};    

interface ED{
    void init(void);
    void main(void);
};

behavior Draw(i_uchar7220sr_receiver in_image, i_uchar7220sr_receiver in_mid,  i_uchar7220s_sender out_image, OSAPI rtos) implements ED
{

    struct Task me;

    EdgeDraw edge_draw(in_image, in_mid,  out_image, rtos);

    void init(void) {
	me = rtos.task_create("SDRAW");
	rtos.push_t(me);
    }
    
    void main(void) {
	rtos.task_activate(me);
	printf("activate! current id is %d\n", me.id);
	fsm{
            edge_draw: {goto edge_draw;}
	}
	printf("terminate! current id is %d\n", me.id);
	rtos.task_terminate();
    }
    
};



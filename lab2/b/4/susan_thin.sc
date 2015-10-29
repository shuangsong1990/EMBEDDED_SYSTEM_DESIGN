#include "susan.sh"

import "c_uchar7220_queue";
import "c_int7220_queue";
import "os";
import "init";

behavior SusanThinThread(int r[IMAGE_SIZE], uchar mid[IMAGE_SIZE], in int thID, OSAPI os) implements Init
{

    Task *task;

    void init(void){
        task=os.task_create("st_t");
    }

    void main (void) {

        int    l[9], centre,
               b01, b12, b21, b10,
               p1, p2, p3, p4,
               b00, b02, b20, b22,
               m, n, a, b, x, y, i, j;
        uchar *mp;

        os.task_activate(task);

        for (i=4+(Y_SIZE-4-4)/PROCESSORS*thID; i<4+(Y_SIZE-4-4)/PROCESSORS*(thID+1) + (thID+1==PROCESSORS && (Y_SIZE-4-4)%PROCESSORS!=0 ? (Y_SIZE-4-4)%PROCESSORS : 0); i++)         		    {

            //for (i=4;i<Y_SIZE-4;i++)
            for (j=4;j<X_SIZE-4;j++)
                if (mid[i*X_SIZE+j]<8)
                {
                    centre = r[i*X_SIZE+j];
                    /* count number of neighbours */

                    mp=mid + (i-1)*X_SIZE + j-1;

                    n = (*mp<8) +
                        (*(mp+1)<8) +
                        (*(mp+2)<8) +
                        (*(mp+X_SIZE)<8) +
                        (*(mp+X_SIZE+2)<8) +
                        (*(mp+X_SIZE+X_SIZE)<8) +
                        (*(mp+X_SIZE+X_SIZE+1)<8) +
                        (*(mp+X_SIZE+X_SIZE+2)<8);

                    /* n==0 no neighbours - remove point */
                    if (n==0)
                        mid[i*X_SIZE+j]=100;
                    /* n==1 - extend line if I can */
                    /* extension is only allowed a few times - the value of mid is used to control this */

                    if ( (n==1) && (mid[i*X_SIZE+j]<6) )
                    {
                        /* find maximum neighbour weighted in direction opposite the
                           neighbour already present. e.g.
have: O O O  weight r by 0 2 3
X X O              0 0 4
O O O              0 2 3     */

                        l[0]=r[(i-1)*X_SIZE+j-1]; l[1]=r[(i-1)*X_SIZE+j]; l[2]=r[(i-1)*X_SIZE+j+1];
                        l[3]=r[(i  )*X_SIZE+j-1]; l[4]=0;                 l[5]=r[(i  )*X_SIZE+j+1];
                        l[6]=r[(i+1)*X_SIZE+j-1]; l[7]=r[(i+1)*X_SIZE+j]; l[8]=r[(i+1)*X_SIZE+j+1];

                        if (mid[(i-1)*X_SIZE+j-1]<8)        { l[0]=0; l[1]=0; l[3]=0; l[2]*=2;
                            l[6]*=2; l[5]*=3; l[7]*=3; l[8]*=4; }
                        else { if (mid[(i-1)*X_SIZE+j]<8)   { l[1]=0; l[0]=0; l[2]=0; l[3]*=2;
                            l[5]*=2; l[6]*=3; l[8]*=3; l[7]*=4; }
                            else { if (mid[(i-1)*X_SIZE+j+1]<8) { l[2]=0; l[1]=0; l[5]=0; l[0]*=2;
                                l[8]*=2; l[3]*=3; l[7]*=3; l[6]*=4; }
                                else { if (mid[(i)*X_SIZE+j-1]<8)   { l[3]=0; l[0]=0; l[6]=0; l[1]*=2;
                                    l[7]*=2; l[2]*=3; l[8]*=3; l[5]*=4; }
                                    else { if (mid[(i)*X_SIZE+j+1]<8)   { l[5]=0; l[2]=0; l[8]=0; l[1]*=2;
                                        l[7]*=2; l[0]*=3; l[6]*=3; l[3]*=4; }
                                        else { if (mid[(i+1)*X_SIZE+j-1]<8) { l[6]=0; l[3]=0; l[7]=0; l[0]*=2;
                                            l[8]*=2; l[1]*=3; l[5]*=3; l[2]*=4; }
                                            else { if (mid[(i+1)*X_SIZE+j]<8)   { l[7]=0; l[6]=0; l[8]=0; l[3]*=2;
                                                l[5]*=2; l[0]*=3; l[2]*=3; l[1]*=4; }
                                                else { if (mid[(i+1)*X_SIZE+j+1]<8) { l[8]=0; l[5]=0; l[7]=0; l[6]*=2;
                                                    l[2]*=2; l[1]*=3; l[3]*=3; l[0]*=4; } }}}}}}}

                                                    m=0;     /* find the highest point */
                                                    for(y=0; y<3; y++)
                                                        for(x=0; x<3; x++)
                                                            if (l[y+y+y+x]>m) { m=l[y+y+y+x]; a=y; b=x; }

                                                    if (m>0)
                                                    {
                                                        if (mid[i*X_SIZE+j]<4)
                                                            mid[(i+a-1)*X_SIZE+j+b-1] = 4;
                                                        else
                                                            mid[(i+a-1)*X_SIZE+j+b-1] = mid[i*X_SIZE+j]+1;
                                                        if ( (a+a+b) < 3 ) /* need to jump back in image */
                                                        {
                                                            i+=a-1;
                                                            j+=b-2;
                                                            if (i<4) i=4;
                                                            if (j<4) j=4;
                                                        }
                                                    }
                    }

                    if (n==2)
                    {
                        /* put in a bit here to straighten edges */
                        b00 = mid[(i-1)*X_SIZE+j-1]<8; /* corners of 3x3 */
                        b02 = mid[(i-1)*X_SIZE+j+1]<8;
                        b20 = mid[(i+1)*X_SIZE+j-1]<8;
                        b22 = mid[(i+1)*X_SIZE+j+1]<8;
                        if ( ((b00+b02+b20+b22)==2) && ((b00|b22)&(b02|b20)))
                        {  /* case: move a point back into line.
                              e.g. X O X  CAN  become X X X
                              O X O              O O O
                              O O O              O O O    */
                            if (b00)
                            {
                                if (b02) { x=0; y=-1; }
                                else     { x=-1; y=0; }
                            }
                            else
                            {
                                if (b02) { x=1; y=0; }
                                else     { x=0; y=1; }
                            }
                            if (((float)r[(i+y)*X_SIZE+j+x]/(float)centre) > 0.7)
                            {
                                if ( ( (x==0) && (mid[(i+(2*y))*X_SIZE+j]>7) && (mid[(i+(2*y))*X_SIZE+j-1]>7) && (mid[(i+(2*y))*X_SIZE+j+1]>7) ) ||
                                        ( (y==0) && (mid[(i)*X_SIZE+j+(2*x)]>7) && (mid[(i+1)*X_SIZE+j+(2*x)]>7) && (mid[(i-1)*X_SIZE+j+(2*x)]>7) ) )
                                {
                                    mid[(i)*X_SIZE+j]=100;
                                    mid[(i+y)*X_SIZE+j+x]=3;  /* no jumping needed */
                                }
                            }
                        }
                        else
                        {
                            b01 = mid[(i-1)*X_SIZE+j  ]<8;
                            b12 = mid[(i  )*X_SIZE+j+1]<8;
                            b21 = mid[(i+1)*X_SIZE+j  ]<8;
                            b10 = mid[(i  )*X_SIZE+j-1]<8;
                            /* right angle ends - not currently used */

#ifdef IGNORETHIS
                            if ( (b00&b01)|(b00&b10)|(b02&b01)|(b02&b12)|(b20&b10)|(b20&b21)|(b22&b21)|(b22&b12) )
                            { /* case; right angle ends. clean up.
                                 e.g.; X X O  CAN  become X X O
                                 O X O              O O O
                                 O O O              O O O        */
                                if ( ((b01)&(mid[(i-2)*X_SIZE+j-1]>7)&(mid[(i-2)*X_SIZE+j]>7)&(mid[(i-2)*X_SIZE+j+1]>7)&
                                            ((b00&((2*r[(i-1)*X_SIZE+j+1])>centre))|(b02&((2*r[(i-1)*X_SIZE+j-1])>centre)))) |
                                        ((b10)&(mid[(i-1)*X_SIZE+j-2]>7)&(mid[(i)*X_SIZE+j-2]>7)&(mid[(i+1)*X_SIZE+j-2]>7)&
                                         ((b00&((2*r[(i+1)*X_SIZE+j-1])>centre))|(b20&((2*r[(i-1)*X_SIZE+j-1])>centre)))) |
                                        ((b12)&(mid[(i-1)*X_SIZE+j+2]>7)&(mid[(i)*X_SIZE+j+2]>7)&(mid[(i+1)*X_SIZE+j+2]>7)&
                                         ((b02&((2*r[(i+1)*X_SIZE+j+1])>centre))|(b22&((2*r[(i-1)*X_SIZE+j+1])>centre)))) |
                                        ((b21)&(mid[(i+2)*X_SIZE+j-1]>7)&(mid[(i+2)*X_SIZE+j]>7)&(mid[(i+2)*X_SIZE+j+1]>7)&
                                         ((b20&((2*r[(i+1)*X_SIZE+j+1])>centre))|(b22&((2*r[(i+1)*X_SIZE+j-1])>centre)))) )
                                {
                                    mid[(i)*X_SIZE+j]=100;
                                    if (b10&b20) j-=2;
                                    if (b00|b01|b02) { i--; j-=2; }
                                }
                            }
#endif

                            if  (((b01+b12+b21+b10)==2) && ((b10|b12)&(b01|b21)) &&
                                    ((b01&((mid[(i-2)*X_SIZE+j-1]<8)|(mid[(i-2)*X_SIZE+j+1]<8)))|(b10&((mid[(i-1)*X_SIZE+j-2]<8)|(mid[(i+1)*X_SIZE+j-2]<8)))|
                                     (b12&((mid[(i-1)*X_SIZE+j+2]<8)|(mid[(i+1)*X_SIZE+j+2]<8)))|(b21&((mid[(i+2)*X_SIZE+j-1]<8)|(mid[(i+2)*X_SIZE+j+1]<8)))) )
                            { /* case; clears odd right angles.
                                 e.g.; O O O  becomes O O O
                                 X X O          X O O
                                 O X O          O X O     */
                                mid[(i)*X_SIZE+j]=100;
                                i--;               /* jump back */
                                j-=2;
                                if (i<4) i=4;
                                if (j<4) j=4;
                            }
                        }
                    }

                    if (n>2)
                    {
                        b01 = mid[(i-1)*X_SIZE+j  ]<8;
                        b12 = mid[(i  )*X_SIZE+j+1]<8;
                        b21 = mid[(i+1)*X_SIZE+j  ]<8;
                        b10 = mid[(i  )*X_SIZE+j-1]<8;
                        if((b01+b12+b21+b10)>1)
                        {
                            b00 = mid[(i-1)*X_SIZE+j-1]<8;
                            b02 = mid[(i-1)*X_SIZE+j+1]<8;
                            b20 = mid[(i+1)*X_SIZE+j-1]<8;
                            b22 = mid[(i+1)*X_SIZE+j+1]<8;
                            p1 = b00 | b01;
                            p2 = b02 | b12;
                            p3 = b22 | b21;
                            p4 = b20 | b10;

                            if( ((p1 + p2 + p3 + p4) - ((b01 & p2)+(b12 & p3)+(b21 & p4)+(b10 & p1))) < 2)
                            {
                                mid[(i)*X_SIZE+j]=100;
                                i--;
                                j-=2;
                                if (i<4) i=4;
                                if (j<4) j=4;
                            }
                        }
                    }
                }

            os.time_wait(6400000);
        }
        os.task_terminate();//changed here
    }//main
};

behavior SusanThin_ReadInput(i_int7220_receiver in_r, i_uchar7220_receiver in_mid, int r[IMAGE_SIZE], uchar mid[IMAGE_SIZE])
{

    void main(void) {
        in_r.receive(&r);
        in_mid.receive(&mid);
    }
};

behavior SusanThin_WriteOutput(i_uchar7220_sender out_mid, uchar mid[IMAGE_SIZE])
{
    void main(void) {
        out_mid.send(mid);
    }
};

behavior SusanThin(int r[IMAGE_SIZE], uchar mid[IMAGE_SIZE], OSAPI os)
{
    Task *task;

    SusanThinThread susan_thin_thread_0(r, mid, 0, os);
    SusanThinThread susan_thin_thread_1(r, mid, 1, os);

    void main(void) {

        susan_thin_thread_0.init(); //task(1)
        susan_thin_thread_1.init(); //task(2)

        task=os.par_start();
        par {
            susan_thin_thread_0;
            susan_thin_thread_1;
        }
        os.par_end(task);

    }
};

behavior Thin(i_int7220_receiver in_r, i_uchar7220_receiver in_mid, i_uchar7220_sender out_mid, OSAPI os) implements Init
{
    Task *task;

    int r[IMAGE_SIZE];
    uchar mid[IMAGE_SIZE];

    SusanThin_ReadInput susan_thin_read_input(in_r, in_mid, r, mid);
    SusanThin_WriteOutput susan_thin_write_output(out_mid, mid);
    SusanThin susan_thin(r, mid, os);

    void init(void){
        task = os.task_create("thin");
    }

    void main(void) {
        os.task_activate(task);
        fsm {
            susan_thin_read_input: goto susan_thin;
            susan_thin: { goto susan_thin_write_output;}
            susan_thin_write_output: goto susan_thin_read_input;
        }
        os.task_terminate();
    }
};



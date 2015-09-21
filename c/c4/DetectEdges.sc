#include <stdlib.h>
#include <stdio.h>
#include "sim.sh"
#include <string.h>
#include <math.h>

#define  exit_error(IFB,IFC) { fprintf(stderr,IFB,IFC); exit(0); }
#define x_size 76
#define y_size 95
#define image_size 7220

import "c_double_handshake";
import "c_type_define";



behavior cal_mid_thread(
			inout unsigend char mid[image_size],
			inout unsigned char in_sc[image_size],
			inout unsigned char bp[516],
			inout unsigned int r[image_size],
			in unsigend int sp,
			in unsigned int ep,
			in unsigend int max_no) 
{

                int cir_mask(unsigned char cir_in[image_size], unsigned char cir_bp[516], int i, int j, int sel){
                        int n = 0;
                        unsigned char *cp;

                        int ci = 3;
                        int cj = 3;
                        int i_d = 0, j_d = 0;

                        int c;

                        int mask[7][7] = {{0,0,1,1,1,0,0},
                                          {0,1,1,1,1,1,0},
                                          {1,1,1,1,1,1,1},
                                          {1,1,1,0,1,1,1},
                                          {1,1,1,1,1,1,1},
                                          {0,1,1,1,1,1,0},
                                          {0,0,1,1,1,0,0}};

                        //cp = cir_bp + cir_in[i*x_size+j] + 258;

                        cp = cir_bp + cir_in[i*x_size+j] + 258;
                                
                        for(i_d = -3; i_d <=  3; i_d++){
                                for(j_d = -3; j_d <= 3 ; j_d++){
                                        if(mask[ci + i_d][cj + j_d] == 1){
                                                c =  (unsigned int)*(cp - cir_in[(i + i_d) * x_size + j + j_d]);
                                                //printf("%d\n",c);
                                                if(sel == 0){ /// sel is the selector
                                                        n = n + c;
                                        //              printf("%d\n",n);
                                                }
                                                }
                                                else if (sel == 1){
                                                        n = n + j_d * c;
                                                }
                                                else if (sel == 2){
                                                        n = n + i_d * c;
                                                }
                                                else if (sel == 3){
                                                        n = n + j_d * j_d * c;
                //                                      printf("%d\n", n);
                                                }
                                                else if (sel == 4){
                                                        n = n + i_d * i_d * c;
                                                }
                                                else if (sel == 5){
                                                        n = n + i_d * j_d * c;
                                                }
                                        }
                                }
                        }

                        switch (sel){
                                case 0: return (n + 100);
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5: return n;
                                default: return n;
                        }
        }

        void main(void){
               float z;
               int do_symmetry, i, j, m, n, a, b, x, y, w;

               for (i=4;i<y_size-4;i++){
                        for (j=sp;j<ep;j++){
                                if (r[i*x_size + j] <= 0)
                                        continue;
                                m = r[i*x_size + j];
                                n = max_no - m;
                                if (n > 600){
                                        x = cir_mask(in_sc, bp, i, j, 1);
                                        y = cir_mask(in_sc, bp, i, j, 2);
                                        z = sqrt((float)((x*x) + (y*y)));
                                        if (z > (0.9*(float)n)) /* 0.5 */{
                                                do_symmetry=0;
                                                if (x==0)
                                                        z=1000000.0;
                                                else
                                                        z=((float)y) / ((float)x);
                                                if (z < 0) { z=-z; w=-1; }
                                                else w=1;
                                                if (z < 0.5) { /* vert_edge */ a=0; b=1; }
                                                else {
                                                        if (z > 2.0) { /* hor_edge */ a=1; b=0; }
                                                        else { /* diag_edge */
                                                                if (w>0) { a=1; b=1; }
                                                                else { a=-1; b=1; }
                                                        }
                                                }
                                                if ( (m > r[(i+a)*x_size+j+b]) && (m >= r[(i-a)*x_size+j-b]) && (m > r[(i+(2*a))*x_size+j+(2*b)]) && (m >= r[(i-(2*a))*x_size+j-(2*b)]) )
                                                        mid[i*x_size+j] = 1;
                                        }
                                        else
                                                do_symmetry = 1;
                                }
                                else
                                        do_symmetry = 1;

                                if (do_symmetry == 1){
                                        x = cir_mask(in_sc, bp, i, j, 3);
                                        y = cir_mask(in_sc, bp, i, j, 4);
                                        w = cir_mask(in_sc, bp, i, j, 5);
                                        if (y==0)
                                                z = 1000000.0;
                                        else
                                                z = ((float)x) / ((float)y);
                //                      printf("%d, %d, %d, %d\n", x, y, z, w);
                                        if (z < 0.5) { /* vertical */ a=0; b=1; }
                                        else {
                                                if (z > 2.0) { /* horizontal */ a=1; b=0; }
                                                else { /* diagonal */
                                                        if (w>0) { a=-1; b=1; }
                                                        else { a=1; b=1; }
                                                }
                                        }
                                        if ( (m > r[(i+a)*x_size+j+b]) && (m >= r[(i-a)*x_size+j-b]) && (m > r[(i+(2*a))*x_size+j+(2*b)]) && (m >= r[(i-(2*a))*x_size+j-(2*b)]) )
                                                mid[i*x_size+j] = 2;
                                }

                        }
                }

        }
};






behavior cal_r_thread(
	inout int r[image_size],
	inout unsigned char in_sc[image_size],
	inout unsigned char bp[516],
	inout int row_start,
	inout int row_end,
	inout int max_no)
{
	int cir_mask(unsigned char cir_in[image_size], unsigned char cir_bp[516], int i, int j, int sel){
		int n = 0;
		unsigned char *cp;
	
		int ci = 3;
		int cj = 3;
		int i_d = 0, j_d = 0;
	
		int c;
	
		int mask[7][7] = {{0,0,1,1,1,0,0},
				  {0,1,1,1,1,1,0},
				  {1,1,1,1,1,1,1},
				  {1,1,1,0,1,1,1},
				  {1,1,1,1,1,1,1},
				  {0,1,1,1,1,1,0},
				  {0,0,1,1,1,0,0}};
		
		//cp = cir_bp + cir_in[i*x_size+j] + 258;	

		cp = cir_bp + cir_in[i*x_size+j] + 258;	
			
		for(i_d = -3; i_d <=  3; i_d++){
			for(j_d = -3; j_d <= 3 ; j_d++){
				if(mask[ci + i_d][cj + j_d] == 1){
				 	c =  (unsigned int)*(cp - cir_in[(i + i_d) * x_size + j + j_d]);
					//printf("%d\n",c);
					if(sel == 0){ /// sel is the selector
						n = n + c;
				//		printf("%d\n",n);
					}	
					else if (sel == 1){
						n = n + j_d * c;
					}
					else if (sel == 2){
						n = n + i_d * c;
					}
					else if (sel == 3){
						n = n + j_d * j_d * c;
	//					printf("%d\n", n);
					}
					else if (sel == 4){
						n = n + i_d * i_d * c;
					}
					else if (sel == 5){
						n = n + i_d * j_d * c;
					}	
				}
			}
		}
	
	
		switch (sel){
			case 0:	return (n + 100);	
			case 1:
			case 2:
			case 3:	
			case 4:
			case 5: return n;
			default: return n;
		}
	}

	void main(void){

		int i = 0, j = 0;
		for (i=3; i<y_size-3; i++){
			for (j=row_start; j<row_end; j++){
				n = cir_mask(in_sc, bp, i, j, 0);
				if (n <= max_no){
					r[i*x_size + j] = max_no - n;
				}
			}
		}

	}

};

behavior SusanEdges(
//	i_receiver start, 
	inout unsigned char in_sc[image_size],
	inout int r[image_size],
	inout unsigned char mid[image_size],
	inout unsigned char bp[516])
{

	int row_sep = x_size / 2;

	

	int max_no = 2650;	
	int r_thread1[image_size], r_thread2[image_size];
	unsigned char mid_thread1[image_size], mid_thread2[image_size];
	cal_r_thread cal_r_thread1(r_thread1, in_sc, bp, 3, row_sep, max_no);
	cal_r_thread cal_r_thread2(r_thread2, in_sc, bp, row_sep, x_size-3, max_no);	
	cal_mid_thread cal_mid_thread1(mid_thread1, in_sc, bp, r, 4, row_sep, max_no );
	cal_mid_thread cal_mid_thread2(mid_thread2, in_sc, bp, r, row_sep, x_size-4, max_no );


	void main(void)
	{
				
		float z;
		int do_symmetry, i, j, m, n, a, b, x, y, w;

//		start.receive(in_sc, image_size * sizeof(unsigned char));
		
//		memset (r,0,x_size*y_size*sizeof(int));


		for(i = 0; i < y_size; i++){
			for(j = 0; j <x_size; j++ ){
			 	mid[i*x_size + j] = 100;
				mid_thread1[i*x_size + j] = 100;
				mid_thread1[i*x_size + j] = 100;
			}
		}

		for (i=0;i<y_size;i++){
			for (j=0;j<x_size;j++){
				r[i*x_size + j] = 0;
				r_thread1[i*x_size + j] = 0;
				r_thread2[i*x_size + j] = 0;
			}
		}

		/*separate*/

		par{
			cal_r_thread1.main();
			cal_r_thread2.main();
		}

		for (i=3;i<y_size-3;i++){
		 	for (j=3;j<x_size-3;j++){
				if (j < row_sep)
					r[i*x_size + j] = r_thread1[i*x_size + j];
				else
					r[i*x_size + j] = r_thread2[i*x_size + j];
		 	}
		}

		/*combine*/

		/*separate*/
		
		par{
			cal_mid_thread1.main();
			cal_mid_thread2.main();
		}

		for (i=4;i<y_size-4;i++){
			for (j=4;j<x_size-4;j++){
				if (j < row_sep)
					mid[i*x_size + j] = mid_thread1[i*x_size + j];
				else
					mid[i*x_size + j] = mid_thread2[i*x_size + j];
			}
		}
		

		/*combine*/
	}

};

behavior SetupBrightnessLut(
	inout unsigned char bp[516])
{
	int thresh = 20;
	void main(void)
	{
		
		int   k;
		float temp;

	//	printf("thresh is %d\n",thresh);

	//	bp = bp + 258;

		for(k=-256;k<257;k++)
		{
			temp=((float)k)/((float)thresh);
		  	temp=temp*temp;
		  	temp=temp*temp*temp;
		  	temp=100.0*exp(-temp);
		  	*(bp+258+k)= (unsigned char)temp;
			//printf("%f\n", temp);
		}

	}
};

behavior DetectEdges(
	i_in_receiver in_buffer,
	i_r_sender r_s,
	i_mid_sender mid_s)
{
	unsigned char in_sc[image_size];
	int r[image_size];
	unsigned char mid[image_size];

	unsigned char bp[516];
	SetupBrightnessLut SBLut(bp);
//	SusanEdges Edge(start, in_sc, r, mid, bp);
	SusanEdges Edge(in_sc,r,mid,bp);

	void main(void)
	{

		int i = 0;

		for (i = 0; i < image_size; i ++)
			in_buffer.receive(in_sc + i);
		//printf("receive image fine at detect edges\n");


		SBLut.main();
		Edge.main();		
		
		//printf("receive image fine at detect edges\n");

		for (i = 0; i < image_size; i++){
			r_s.send(r[i]);
			mid_s.send(mid[i]);
		}
		
//		printf("the end of D\n");
	}
};

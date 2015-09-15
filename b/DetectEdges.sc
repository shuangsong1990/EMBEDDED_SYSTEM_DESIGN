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

behavior SusanEdges(
	i_receiver start, 
	inout unsigned char in_sc[image_size],
	inout int r[image_size],
	inout unsigned char mid[image_size],
	inout unsigned char bp[516])
{

	int cir_mask(unsigned char cir_in[image_size], unsigned char cir_bp[516], int i, int j, int sel){
		int n = 0;
		unsigned char *cp;
	
		int ci = 3;
		int cj = 3;
		int i_d = 0, j_d = 0;
	
		int c;
	
		int mask[7][7] = {{0,0,1,1,1,0,0},{0,1,1,1,1,1,0},{1,1,1,1,1,1,1},{1,1,1,0,1,1,1},{1,1,1,1,1,1,1},{0,1,1,1,1,1,0},{0,0,1,1,1,0,0}};
		cp = cir_bp + cir_in[i*x_size+j] + 258;
		


	
		for(i_d = -3; i_d <=  3; i_d++){
			for(j_d = -3; j_d <= 3 ; j_d++){
				if(mask[ci + i_d][cj + j_d] == 1){
				 	c =  (unsigned int)*(cp - cir_in[(i + i_d) * x_size + j + j_d]);
					if(sel == 0){ /// sel is the selector
						n = n + c;
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

	void main(void)
	{

		int max_no = 2650;	
		int handshake;
				
		float z;
		int do_symmetry, i, j, m, n, a, b, x, y, w;

		start.receive(&handshake, sizeof(int));
		
//		memset (r,0,x_size*y_size*sizeof(int));

		for (i=0;i<y_size;i++){
			for (j=0;j<x_size;j++){
				r[i*x_size + j] = 0;
			}
		}
		   
		for (i=3;i<y_size-3;i++){
		 	for (j=3;j<x_size-3;j++){
		 		n = cir_mask(in_sc, bp, i, j, 0);
		     	if(n <= max_no){
		     		r[i*x_size + j] = max_no - n;
		     	}
		 	}
		}
		
		for (i=4;i<y_size-4;i++){
			for (j=4;j<x_size-4;j++){
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
		//			printf("%d, %d, %d, %d\n", x, y, z, w);
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

behavior SetupBrightnessLut(
	inout unsigned char bp[516])
{
	int thresh = 20;
	void main(void)
	{
		
		int   k;
		float temp;

	//	printf("thresh is %d\n",thresh);

		bp = bp + 258;

		for(k=-256;k<257;k++)
		{
			temp=((float)k)/((float)thresh);
		  	temp=temp*temp;
		  	temp=temp*temp*temp;
		  	temp=100.0*exp(-temp);
		  	*(bp+k)= (unsigned char)temp;
		}

	}
};

behavior DetectEdges(
	i_receiver start, 
	inout unsigned char in_sc[image_size],
	inout int r[image_size],
	inout unsigned char mid[image_size])
{

	unsigned char bp[516];
	SetupBrightnessLut SBLut(bp);
	SusanEdges Edge(start, in_sc, r, mid, bp);

	void main(void)
	{
		SBLut.main();
		Edge.main();		
		printf("the end of D\n");
	}
};

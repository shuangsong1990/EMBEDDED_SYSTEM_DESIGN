#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <sys/file.h>    /* may want to remove this line */

#define x_size 76
#define y_size 95
#define image_size  7220 /// 76 * 95
#define bt 20
#define  exit_error(IFB,IFC) { fprintf(stderr,IFB,IFC); exit(0); }


typedef unsigned char uchar;

//void susan_edges(unsigned char[],int r[],);
//void setup_brightness_lu(unsigned char[], int, int);
//void get_image(char[],unsigned char *);

int getint(fd)
  FILE *fd;
{
  int c, i;
  char dummy[10000];

  c = getc(fd);
  while (1) /* find next integer */
  {
    if (c=='#')    /* if we're at a comment, read to end of line */
      fgets(dummy,9000,fd);
    if (c==EOF)
      exit_error("Image %s not binary PGM.\n","is");
    if (c>='0' && c<='9')
      break;   /* found what we were looking for */
    c = getc(fd);
  }

  /* we're at the start of a number, continue until we hit a non-number */
  i = 0;
  while (1) {
    i = (i*10) + (c - '0');
    c = getc(fd);
    if (c==EOF) return (i);
    if (c<'0' || c>'9') break;
  }

  return (i);
}


int cir_mask(unsigned char in[image_size], unsigned char bp[516], int i, int j, int sel){
	int n = 0, ii = 0;
	uchar *cp;
	
	cp = bp + in[i*x_size+j] + 258;
	
	int mask[7][7] = {{0,0,1,1,1,0,0},
			  {0,1,1,1,1,1,0},
			  {1,1,1,1,1,1,1},
			  {1,1,1,0,1,1,1},
			  {1,1,1,1,1,1,1},
			  {0,1,1,1,1,1,0},
			  {0,0,1,1,1,0,0}};

	int ci = 3;
	int cj = 3;
	int i_d = 0, j_d = 0;

	for(i_d = -3; i_d <=  3; i_d++){
		for(j_d = -3; j_d <= 3 ; j_d++){
			if(mask[ci + i_d][cj + j_d] == 1){
				int c =  (unsigned int)*(cp - in[(i + i_d) * x_size + j + j_d]);
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


/* }}} */
/* {{{ susan_edges(in,r,sf,max_no,out) */

void susan_edges(unsigned char in[image_size], int r[image_size], unsigned char mid[image_size], unsigned char bp[516], int max_no){

float z;
int   do_symmetry, i, j, m, n, a, b, x, y, w;
//uchar c,*p,*cp;

   memset (r,0,x_size*y_size*sizeof(int));
   
   for (i=3;i<y_size-3;i++){
    	for (j=3;j<x_size-3;j++){
    		n = cir_mask(in, bp, i, j, 0);
		if(n <= max_no){
			r[i*x_size + j] = max_no - n;
		}
    	}
   }

   for (i=4;i<y_size-4;i++)
	for (j=4;j<x_size-4;j++){
		if (r[i*x_size + j] <= 0)
			continue;
		m = r[i*x_size + j];
		n = max_no - m;
		if (n > 600){
			x = cir_mask(in, bp, i, j, 1);
			y = cir_mask(in, bp, i, j, 2);
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
			x = cir_mask(in, bp, i, j, 3);
			y = cir_mask(in, bp, i, j, 4);
			w = cir_mask(in, bp, i, j, 5);
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








/* }}} */
/* {{{ susan_thin(r,mid,x_size,y_size) */

/* only one pass is needed as i,j are decremented if necessary to go
   back and do bits again */

void susan_thin( int r[image_size], unsigned char mid[image_size]){


int   l[9], centre, nlinks, npieces,
      b01, b12, b21, b10,
      p1, p2, p3, p4,
      b00, b02, b20, b22,
      m, n, a, b, x, y, i, j;
uchar *mp;

  for (i=4;i<y_size-4;i++)
    for (j=4;j<x_size-4;j++)
      if (mid[i*x_size+j]<8)
      {
        centre = r[i*x_size+j];
        /* {{{ count number of neighbours */

        mp=mid + (i-1)*x_size + j-1;

        n = (*mp<8) +
            (*(mp+1)<8) +
            (*(mp+2)<8) +
            (*(mp+x_size)<8) +
            (*(mp+x_size+2)<8) +
            (*(mp+x_size+x_size)<8) +
            (*(mp+x_size+x_size+1)<8) +
            (*(mp+x_size+x_size+2)<8);

/* }}} */
        /* {{{ n==0 no neighbours - remove point */

        if (n==0)
          mid[i*x_size+j]=100;

/* }}} */
        /* {{{ n==1 - extend line if I can */

        /* extension is only allowed a few times - the value of mid is used to control this */

        if ( (n==1) && (mid[i*x_size+j]<6) )
        {
          /* find maximum neighbour weighted in direction opposite the
             neighbour already present. e.g.
             have: O O O  weight r by 0 2 3
                   X X O              0 0 4
                   O O O              0 2 3     */

          l[0]=r[(i-1)*x_size+j-1]; l[1]=r[(i-1)*x_size+j]; l[2]=r[(i-1)*x_size+j+1];
          l[3]=r[(i  )*x_size+j-1]; l[4]=0;                 l[5]=r[(i  )*x_size+j+1];
          l[6]=r[(i+1)*x_size+j-1]; l[7]=r[(i+1)*x_size+j]; l[8]=r[(i+1)*x_size+j+1];

          if (mid[(i-1)*x_size+j-1]<8)        { l[0]=0; l[1]=0; l[3]=0; l[2]*=2;
                                                l[6]*=2; l[5]*=3; l[7]*=3; l[8]*=4; }
          else { if (mid[(i-1)*x_size+j]<8)   { l[1]=0; l[0]=0; l[2]=0; l[3]*=2;
                                                l[5]*=2; l[6]*=3; l[8]*=3; l[7]*=4; }
          else { if (mid[(i-1)*x_size+j+1]<8) { l[2]=0; l[1]=0; l[5]=0; l[0]*=2;
                                                l[8]*=2; l[3]*=3; l[7]*=3; l[6]*=4; }
          else { if (mid[(i)*x_size+j-1]<8)   { l[3]=0; l[0]=0; l[6]=0; l[1]*=2;
                                                l[7]*=2; l[2]*=3; l[8]*=3; l[5]*=4; }
          else { if (mid[(i)*x_size+j+1]<8)   { l[5]=0; l[2]=0; l[8]=0; l[1]*=2;
                                                l[7]*=2; l[0]*=3; l[6]*=3; l[3]*=4; }
          else { if (mid[(i+1)*x_size+j-1]<8) { l[6]=0; l[3]=0; l[7]=0; l[0]*=2;
                                                l[8]*=2; l[1]*=3; l[5]*=3; l[2]*=4; }
          else { if (mid[(i+1)*x_size+j]<8)   { l[7]=0; l[6]=0; l[8]=0; l[3]*=2;
                                                l[5]*=2; l[0]*=3; l[2]*=3; l[1]*=4; }
          else { if (mid[(i+1)*x_size+j+1]<8) { l[8]=0; l[5]=0; l[7]=0; l[6]*=2;
                                                l[2]*=2; l[1]*=3; l[3]*=3; l[0]*=4; } }}}}}}}

          m=0;     /* find the highest point */
          for(y=0; y<3; y++)
            for(x=0; x<3; x++)
              if (l[y+y+y+x]>m) { m=l[y+y+y+x]; a=y; b=x; }

          if (m>0)
          {
            if (mid[i*x_size+j]<4)
              mid[(i+a-1)*x_size+j+b-1] = 4;
            else
              mid[(i+a-1)*x_size+j+b-1] = mid[i*x_size+j]+1;
            if ( (a+a+b) < 3 ) /* need to jump back in image */
            {
              i+=a-1;
              j+=b-2;
              if (i<4) i=4;
              if (j<4) j=4;
            }
          }
        }

/* }}} */
        /* {{{ n==2 */
        if (n==2)
        {
          /* put in a bit here to straighten edges */
          b00 = mid[(i-1)*x_size+j-1]<8; /* corners of 3x3 */
          b02 = mid[(i-1)*x_size+j+1]<8;
          b20 = mid[(i+1)*x_size+j-1]<8;
          b22 = mid[(i+1)*x_size+j+1]<8;
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
            if (((float)r[(i+y)*x_size+j+x]/(float)centre) > 0.7)
            {
              if ( ( (x==0) && (mid[(i+(2*y))*x_size+j]>7) && (mid[(i+(2*y))*x_size+j-1]>7) && (mid[(i+(2*y))*x_size+j+1]>7) ) ||
                   ( (y==0) && (mid[(i)*x_size+j+(2*x)]>7) && (mid[(i+1)*x_size+j+(2*x)]>7) && (mid[(i-1)*x_size+j+(2*x)]>7) ) )
              {
                mid[(i)*x_size+j]=100;
                mid[(i+y)*x_size+j+x]=3;  /* no jumping needed */
              }
            }
          }
          else
          {
            b01 = mid[(i-1)*x_size+j  ]<8;
            b12 = mid[(i  )*x_size+j+1]<8;
            b21 = mid[(i+1)*x_size+j  ]<8;
            b10 = mid[(i  )*x_size+j-1]<8;
            /* {{{ right angle ends - not currently used */


/* }}} */
            if ( ((b01+b12+b21+b10)==2) && ((b10|b12)&(b01|b21)) &&
                 ((b01&((mid[(i-2)*x_size+j-1]<8)|(mid[(i-2)*x_size+j+1]<8)))|(b10&((mid[(i-1)*x_size+j-2]<8)|(mid[(i+1)*x_size+j-2]<8)))|
                (b12&((mid[(i-1)*x_size+j+2]<8)|(mid[(i+1)*x_size+j+2]<8)))|(b21&((mid[(i+2)*x_size+j-1]<8)|(mid[(i+2)*x_size+j+1]<8)))) )
            { /* case; clears odd right angles.
                 e.g.; O O O  becomes O O O
                       X X O          X O O
                       O X O          O X O     */
              mid[(i)*x_size+j]=100;
              i--;               /* jump back */
              j-=2;
              if (i<4) i=4;
              if (j<4) j=4;
            }
          }
        }

/* }}} */
        /* {{{ n>2 the thinning is done here without breaking connectivity */

        if (n>2)
        {
          b01 = mid[(i-1)*x_size+j  ]<8;
          b12 = mid[(i  )*x_size+j+1]<8;
          b21 = mid[(i+1)*x_size+j  ]<8;
          b10 = mid[(i  )*x_size+j-1]<8;
          if((b01+b12+b21+b10)>1)
          {
            b00 = mid[(i-1)*x_size+j-1]<8;
            b02 = mid[(i-1)*x_size+j+1]<8;
            b20 = mid[(i+1)*x_size+j-1]<8;
            b22 = mid[(i+1)*x_size+j+1]<8;
            p1 = b00 | b01;
            p2 = b02 | b12;
            p3 = b22 | b21;
            p4 = b20 | b10;

            if( ((p1 + p2 + p3 + p4) - ((b01 & p2)+(b12 & p3)+(b21 & p4)+(b10 & p1))) < 2)
            {
              mid[(i)*x_size+j]=100;
              i--;
              j-=2;
              if (i<4) i=4;
              if (j<4) j=4;
            }
          }
        }

/* }}} */
      }
}










/* }}} */
/* {{{ put_image(filename,in,x_size,y_size) */

put_image(char filename[100],uchar in[image_size]){
FILE  *fd;

  if ((fd=fopen(filename,"w")) == NULL)
    exit_error("Can't output image%s.\n",filename);

  fprintf(fd,"P5\n");
  fprintf(fd,"%d %d\n",x_size,y_size);
  fprintf(fd,"255\n");

  if (fwrite(in,x_size*y_size,1,fd) != 1)
    exit_error("Can't write image %s.\n",filename);

  fclose(fd);
}



/* }}} */
/* {{{ edges */

/* {{{ edge_draw(in,corner_list,drawing_mode) */

void edge_draw(uchar in[image_size], uchar mid[image_size], int drawing_mode){
int   i;
uchar *inp, *midp;

  if (drawing_mode==0)
  {
    /* mark 3x3 white block around each edge point */
    midp=mid;
    for (i=0; i<x_size*y_size; i++)
    {
      if (*midp<8)
      {
        inp = in + (midp - mid) - x_size - 1;
        *inp++=255; *inp++=255; *inp=255; inp+=x_size-2;
        *inp++=255; *inp++;     *inp=255; inp+=x_size-2;
        *inp++=255; *inp++=255; *inp=255;
      }
      midp++;
    }
  }

  /* now mark 1 black pixel at each edge point */
  midp=mid;
  for (i=0; i<x_size*y_size; i++)
  {
    if (*midp<8)
      *(in + (midp - mid)) = 0;
    midp++;
  }
}





/* }}} */

void get_image(char filename[200], unsigned char *in){

FILE  *fd;
char header [100];
int  tmp;

  if ((fd=fopen(filename,"r")) == NULL)
    exit_error("Can't input image %s.\n",filename);


  /* {{{ read header */

  header[0]=fgetc(fd);
  header[1]=fgetc(fd);

  if(!(header[0]=='P' && header[1]=='5'))
    exit_error("Image %s does not have binary PGM header.\n",filename);

//  *x_size = getint(fd);
//  *y_size = getint(fd);
  tmp = getint(fd);
  tmp = getint(fd);
  tmp = getint(fd);

/* }}} */

//  *in = (uchar *) malloc(*x_size * *y_size);

  if (fread(in,1,x_size * y_size,fd) == 0)
    exit_error("Image %s is wrong size.\n",filename);

  fclose(fd);
}


void setup_brightness_lut(unsigned char bp[516], int thresh, int form){

int   k;
float temp;

  printf("thresh is %d\n",thresh);

//  *bp=(unsigned char *)malloc(516);
//  *bp=*bp+258;

  bp = bp + 258;

  for(k=-256;k<257;k++)
  {
    temp=((float)k)/((float)thresh);
    temp=temp*temp;
    if (form==6)
      temp=temp*temp*temp;
    temp=100.0*exp(-temp);
    *(bp+k)= (uchar)temp;
  }

}

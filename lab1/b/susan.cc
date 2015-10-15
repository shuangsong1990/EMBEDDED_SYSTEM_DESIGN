//////////////////////////////////////////////////////////////////////
// C++ source file generated by SpecC V2.2.1
// Design: susan
// File:   susan.cc
// Time:   Mon Sep 14 23:29:16 2015
//////////////////////////////////////////////////////////////////////

// Note: User-defined include files are inlined in this file.

// Note: System-defined include files are inlined in this file.

#include "susan.h"


unsigned int _IDcnt = 0;
// channel class definitions /////////////////////////////////////////

c_double_handshake::c_double_handshake()
    : _specc::channel(),
    v(false),
    w(false)
{   /* nothing */
}

c_double_handshake::~c_double_handshake(void)
{   /* nothing */
}

#line 81 "c_double_handshake.sc"
void c_double_handshake::receive(void *d, unsigned long int l)
{   
    if ( !v)
    {   
	w = true;
	_specc::wait(event(&req), ((void*)0));
	w = false;
    }
    if (l != tmpl)
    {   
	abort();
    }
    memcpy(d, tmpd, l);
    v = false;
    _specc::notify(event(&ack), ((void*)0));
    _specc::wait(event(&ack), ((void*)0));
}

void c_double_handshake::send(const void *d, unsigned long int l)
{   
    tmpd = d;
    tmpl = l;
    v = true;
    if (w)
    {   
	_specc::notify(event(&req), ((void*)0));
    }
    _specc::wait(event(&ack), ((void*)0));
}

#line 61 "susan.cc"
c_queue::c_queue(const unsigned long int (&size))
    : _specc::channel(), size(size),
    buffer(0),
    n(0ul),
    p(0ul),
    wr(0ul),
    ws(0ul)
{   /* nothing */
}

c_queue::~c_queue(void)
{   /* nothing */
}

#line 99 "c_queue.sc"
void c_queue::cleanup(void)
{   
    if ( !n)
    {   
	free(buffer);
	buffer = 0;
    }
}

void c_queue::receive(void *d, unsigned long int l)
{   
    unsigned long int p0;

    while(l > n)
    {   
	wr++ ;
	_specc::wait(event(&r), ((void*)0));
	wr-- ;
    }

    if (n <= p)
    {   
	p0 = p - n;
    }
    else 
    {   
	p0 = p + size - n;
    }
    if (l <= size - p0)
    {   
	memcpy(d,  &buffer[p0], l);
	n -= l;
    }
    else 
    {   
	memcpy(d,  &buffer[p0], size - p0);
	memcpy(((char *)d) + (size - p0),  &buffer[0], l - (size - p0));
	n -= l;
    }

    if (ws)
    {   
	_specc::notify(event(&s), ((void*)0));
    }

    cleanup();
}

void c_queue::send(const void *d, unsigned long int l)
{   
    while(l > size - n)
    {   
	ws++ ;
	_specc::wait(event(&s), ((void*)0));
	ws-- ;
    }

    setup();

    if (l <= size - p)
    {   
	memcpy( &buffer[p], d, l);
	p += l;
	n += l;
    }
    else 
    {   
	memcpy( &buffer[p], d, size - p);
	memcpy( &buffer[0], ((char *)d) + (size - p), l - (size - p));
	p = l - (size - p);
	n += l;
    }

    if (wr)
    {   
	_specc::notify(event(&r), ((void*)0));
    }
}

#line 87 "c_queue.sc"
void c_queue::setup(void)
{   
    if ( !buffer)
    {   
	if ( !(buffer = (char *)malloc(size)))
	{   
	    perror("c_queue");
	    abort();
	}
    }
}

// behavior class definitions ////////////////////////////////////////

#line 171 "susan.cc"
SusanEdges::SusanEdges(unsigned int _idcnt, i_receiver (&start), unsigned char (&in_sc)[7220], int (&r)[7220], unsigned char (&mid)[7220], unsigned char (&bp)[516])
    : _specc::behavior(_idcnt), start(start), in_sc(in_sc), r(r), mid(mid), bp(bp)
{   /* nothing */
}

SusanEdges::~SusanEdges(void)
{   /* nothing */
}

#line 22 "./DetectEdges.sc"
int SusanEdges::cir_mask(unsigned char cir_in[7220], unsigned char cir_bp[516], int i, int j, int sel) {
    int n = 0;
    unsigned char *cp;

    int ci = 3;
    int cj = 3;
    int i_d = 0; int j_d = 0;

    int c;

    int mask[7][7] = { { 0,0,1,1,1,0,0 },{ 
	0,1,1,1,1,1,0 },{ 
	1,1,1,1,1,1,1 },{ 
	1,1,1,0,1,1,1 },{ 
	1,1,1,1,1,1,1 },{ 
	0,1,1,1,1,1,0 },{ 
	0,0,1,1,1,0,0 } };



    cp = cir_bp + cir_in[i * 76 + j];

    for(i_d =  -3; i_d <= 3; i_d++ ) {
	for(j_d =  -3; j_d <= 3; j_d++ ) {
	    if (mask[ci + i_d][cj + j_d] == 1) {
		c = (unsigned int) *(cp - cir_in[(i + i_d) * 76 + j + j_d]);

		if (sel == 0) {
		    n = n + c;
		}
		else 
		    if (sel == 1) {
			n = n + j_d * c;
		    }
		    else  if (sel == 2) {
			    n = n + i_d * c;
			}
			else  if (sel == 3) {
				n = n + j_d * j_d * c;
			    }
			    else 
				if (sel == 4) {
				    n = n + i_d * i_d * c;
				}
				else  if (sel == 5) {
					n = n + i_d * j_d * c;
				    }
	    }
	}
    }


    switch(sel) {
	case 0: return (n + 100);
	case 1:
	    case 2:
		case 3:
		    case 4:
			case 5: return n;
	default: return n;
    }
}

void SusanEdges::main(void)
{   

    int max_no = 2650;
    int handshake;

    float z;
    int a; int b; int do_symmetry; int i; int j; int m; int n; int w; int x; int y;

    start.receive( &handshake, sizeof(int));




    for(i = 0; i < 95; i++ ) {
	for(j = 0; j < 76; j++ ) {
	    mid[i * 76 + j] = 100;
	}
    }

    for(i = 0; i < 95; i++ ) {
	for(j = 0; j < 76; j++ ) {
	    r[i * 76 + j] = 0;
	}
    }

    for(i = 3; i < 95 - 3; i++ ) {
	for(j = 3; j < 76 - 3; j++ ) {
	    n = cir_mask(in_sc, bp, i, j, 0);
	    if (n <= max_no) {
		r[i * 76 + j] = max_no - n;
	    }
	}
    }

    for(i = 4; i < 95 - 4; i++ ) {
	for(j = 4; j < 76 - 4; j++ ) {
	    if (r[i * 76 + j] <= 0)
		continue;
	    m = r[i * 76 + j];
	    n = max_no - m;
	    if (n > 600) {
		x = cir_mask(in_sc, bp, i, j, 1);
		y = cir_mask(in_sc, bp, i, j, 2);
		z = sqrt((float)((x * x) + (y * y)));
		if (z > (9.000000000000000e-01 * (float)n)) {
		    do_symmetry = 0;
		    if (x == 0)
			z = 1.000000000000000e+06;
		    else 
			z = ((float)y) / ((float)x);
		    if (z < 0) { z =  -z; w =  -1;
		    }
		    else 

#line 137 "./DetectEdges.sc"
			w = 1;
		    if (z < 5.000000000000000e-01) { a = 0; b = 1;
		    }
		    else 

#line 139 "./DetectEdges.sc"
		    {   
			if (z > 2.000000000000000e+00) { a = 1; b = 0;
			}
			else 

#line 141 "./DetectEdges.sc"
			{   
			    if (w > 0) { a = 1; b = 1;
			    }
			    else 

#line 143 "./DetectEdges.sc"
			    {    a =  -1; b = 1;
			    }
			}
		    } if ((m > r[(i + a) * 76 + j + b]) && (m >= r[(i - a) * 76 + j - b]) && (m > r[(i + (2 * a)) * 76 + j + (2 * b)]) && (m >= r[(i - (2 * a)) * 76 + j - (2 * b)]))
			mid[i * 76 + j] = 1;
		}
		else 
		    do_symmetry = 1;
	    }
	    else 
		do_symmetry = 1;

	    if (do_symmetry == 1) {
		x = cir_mask(in_sc, bp, i, j, 3);
		y = cir_mask(in_sc, bp, i, j, 4);
		w = cir_mask(in_sc, bp, i, j, 5);
		if (y == 0)
		    z = 1.000000000000000e+06;
		else 
		    z = ((float)x) / ((float)y);

		if (z < 5.000000000000000e-01) { a = 0; b = 1;
		}
		else 

#line 165 "./DetectEdges.sc"
		{   
		    if (z > 2.000000000000000e+00) { a = 1; b = 0;
		    }
		    else 

#line 167 "./DetectEdges.sc"
		    {   
			if (w > 0) { a =  -1; b = 1;
			}
			else 

#line 169 "./DetectEdges.sc"
			{    a = 1; b = 1;
			}
		    }
		} if ((m > r[(i + a) * 76 + j + b]) && (m >= r[(i - a) * 76 + j - b]) && (m > r[(i + (2 * a)) * 76 + j + (2 * b)]) && (m >= r[(i - (2 * a)) * 76 + j - (2 * b)]))
		    mid[i * 76 + j] = 2;
	    }
	}
    }
}

#line 367 "susan.cc"
SetupBrightnessLut::SetupBrightnessLut(unsigned int _idcnt, unsigned char (&bp)[516])
    : _specc::behavior(_idcnt), bp(bp),
    thresh(20)
{   /* nothing */
}

SetupBrightnessLut::~SetupBrightnessLut(void)
{   /* nothing */
}

#line 186 "./DetectEdges.sc"
void SetupBrightnessLut::main(void)
{   

    int k;
    float temp;



    { unsigned int _scc_index_0; for(_scc_index_0=0;_scc_index_0<516;_scc_index_0++) (bp)[_scc_index_0] = (bp + 258)[_scc_index_0]; }

    for(k =  -256; k < 257; k++ )
    {   
	temp = ((float)k) / ((float)thresh);
	temp = temp * temp;
	temp = temp * temp * temp;
	temp = 1.000000000000000e+02 * exp( -temp);
	 *(bp + k) = (unsigned char)temp;
    }
}

#line 399 "susan.cc"
DetectEdges::DetectEdges(unsigned int _idcnt, i_receiver (&start), unsigned char (&in_sc)[7220], int (&r)[7220], unsigned char (&mid)[7220])
    : _specc::behavior(_idcnt), start(start), in_sc(in_sc), r(r), mid(mid),
    Edge(_IDcnt, start, in_sc, r, mid, bp),
    SBLut(_IDcnt, bp)
{   /* nothing */
}

DetectEdges::~DetectEdges(void)
{   /* nothing */
}

#line 220 "./DetectEdges.sc"
void DetectEdges::main(void)
{   
    SBLut.main();
    Edge.main();
}

#line 418 "susan.cc"
EdgeDraw::EdgeDraw(unsigned int _idcnt, unsigned char (&in_sc)[7220], unsigned char (&mid)[7220], int (&drawing_mode), i_sender (&sd))
    : _specc::behavior(_idcnt), in_sc(in_sc), mid(mid), drawing_mode(drawing_mode), sd(sd)
{   /* nothing */
}

EdgeDraw::~EdgeDraw(void)
{   /* nothing */
}

#line 19 "./EdgeDraw.sc"
void EdgeDraw::main(void)
{   

    int i;
    unsigned char *inp; unsigned char *midp;

    if (drawing_mode == 0)
    {   

	midp = mid;
	for(i = 0; i < 76 * 95; i++ )
	{   
	    if ( *midp < 8)
	    {   
		inp = in_sc + (midp - mid) - 76 - 1;
		 *inp++  = 255;  *inp++  = 255;  *inp = 255; inp += 76 - 2;
		 *inp++  = 255;  *inp++ ;  *inp = 255; inp += 76 - 2;
		 *inp++  = 255;  *inp++  = 255;  *inp = 255;
	    }
	    midp++ ;
	}
    }


    midp = mid;
    for(i = 0; i < 76 * 95; i++ )
    {   
	if ( *midp < 8)
	     *(in_sc + (midp - mid)) = 0;
	midp++ ;
    }
    sd.send(in_sc, 7220 * sizeof(unsigned char));
}

#line 463 "susan.cc"
SusanThin::SusanThin(unsigned int _idcnt, int (&r)[7220], unsigned char (&mid)[7220])
    : _specc::behavior(_idcnt), r(r), mid(mid)
{   /* nothing */
}

SusanThin::~SusanThin(void)
{   /* nothing */
}

#line 14 "./SusanThin.sc"
void SusanThin::main(void)
{   

    int a; int b; int b00; int b01; int b02; int b10; int b12; int b20; int b21; int b22; int centre; int i; int j; int l[9]; int m; int n; int p1; int p2; int p3; int p4; int x; int y;
    unsigned char *mp;

    for(i = 4; i < 95 - 4; i++ )
	for(j = 4; j < 76 - 4; j++ )
	    if (mid[i * 76 + j] < 8)
	    {   
		centre = r[i * 76 + j];


		mp = mid + (i - 1) * 76 + j - 1;
		n = ( *mp < 8) + 
		( *(mp + 1) < 8) + 
		( *(mp + 2) < 8) + 
		( *(mp + 76) < 8) + 
		( *(mp + 76 + 2) < 8) + 
		( *(mp + 76 + 76) < 8) + 
		( *(mp + 76 + 76 + 1) < 8) + 
		( *(mp + 76 + 76 + 2) < 8);




		if (n == 0)
		    mid[i * 76 + j] = 100;

#line 48 "./SusanThin.sc"
		if ((n == 1) && (mid[i * 76 + j] < 6))
		{   

#line 56 "./SusanThin.sc"
		    l[0] = r[(i - 1) * 76 + j - 1];
		    l[1] = r[(i - 1) * 76 + j];
		    l[2] = r[(i - 1) * 76 + j + 1];
		    l[3] = r[(i) * 76 + j - 1];
		    l[4] = 0;
		    l[5] = r[(i) * 76 + j + 1];
		    l[6] = r[(i + 1) * 76 + j - 1];
		    l[7] = r[(i + 1) * 76 + j];
		    l[8] = r[(i + 1) * 76 + j + 1];

		    if (mid[(i - 1) * 76 + j - 1] < 8) { l[0] = 0; l[1] = 0; l[3] = 0; l[2] *= 2;
			l[6] *= 2; l[5] *= 3; l[7] *= 3; l[8] *= 4;
		    }
		    else 

#line 68 "./SusanThin.sc"
		    {    if (mid[(i - 1) * 76 + j] < 8) { l[1] = 0; l[0] = 0; l[2] = 0; l[3] *= 2;
			    l[5] *= 2; l[6] *= 3; l[8] *= 3; l[7] *= 4;
			}
			else 

#line 70 "./SusanThin.sc"
			{    if (mid[(i - 1) * 76 + j + 1] < 8) { l[2] = 0; l[1] = 0; l[5] = 0; l[0] *= 2;
				l[8] *= 2; l[3] *= 3; l[7] *= 3; l[6] *= 4;
			    }
			    else 

#line 72 "./SusanThin.sc"
			    {    if (mid[(i) * 76 + j - 1] < 8) { l[3] = 0; l[0] = 0; l[6] = 0; l[1] *= 2;
				    l[7] *= 2; l[2] *= 3; l[8] *= 3; l[5] *= 4;
				}
				else 

#line 74 "./SusanThin.sc"
				{    if (mid[(i) * 76 + j + 1] < 8) { l[5] = 0; l[2] = 0; l[8] = 0; l[1] *= 2;
					l[7] *= 2; l[0] *= 3; l[6] *= 3; l[3] *= 4;
				    }
				    else 

#line 76 "./SusanThin.sc"
				    {    if (mid[(i + 1) * 76 + j - 1] < 8) { l[6] = 0; l[3] = 0; l[7] = 0; l[0] *= 2;
					    l[8] *= 2; l[1] *= 3; l[5] *= 3; l[2] *= 4;
					}
					else 

#line 78 "./SusanThin.sc"
					{    if (mid[(i + 1) * 76 + j] < 8) { l[7] = 0; l[6] = 0; l[8] = 0; l[3] *= 2;
						l[5] *= 2; l[0] *= 3; l[2] *= 3; l[1] *= 4;
					    }
					    else 

#line 80 "./SusanThin.sc"
					    {    if (mid[(i + 1) * 76 + j + 1] < 8) { l[8] = 0; l[5] = 0; l[7] = 0; l[6] *= 2;
						    l[2] *= 2; l[1] *= 3; l[3] *= 3; l[0] *= 4;
						}
					    }
					}
				    }
				}
			    }
			}
		    }

#line 83 "./SusanThin.sc"
		    m = 0;
		    for(y = 0; y < 3; y++ )
			for(x = 0; x < 3; x++ )
			    if (l[y + y + y + x] > m) { m = l[y + y + y + x]; a = y; b = x;
			    }
		    if (m > 0)
		    {   
			if (mid[i * 76 + j] < 4)
			    mid[(i + a - 1) * 76 + j + b - 1] = 4;
			else 
			    mid[(i + a - 1) * 76 + j + b - 1] = mid[i * 76 + j] + 1;
			if ((a + a + b) < 3)
			{   
			    i += a - 1;
			    j += b - 2;
			    if (i < 4) i = 4;
			    if (j < 4) j = 4;
			}
		    }
		}



		if (n == 2)
		{   

		    b00 = mid[(i - 1) * 76 + j - 1] < 8;
		    b02 = mid[(i - 1) * 76 + j + 1] < 8;
		    b20 = mid[(i + 1) * 76 + j - 1] < 8;
		    b22 = mid[(i + 1) * 76 + j + 1] < 8;
		    if (((b00 + b02 + b20 + b22) == 2) && ((b00 | b22) & (b02 | b20)))
		    {   



			if (b00)
			{   
			    if (b02) { x = 0; y =  -1;
			    }
			    else 

#line 121 "./SusanThin.sc"
			    {    x =  -1; y = 0;
			    }
			}
			else  {
			    if (b02) { x = 1; y = 0;
			    }
			    else 

#line 126 "./SusanThin.sc"
			    {    x = 0; y = 1;
			    }
			} if (((float)r[(i + y) * 76 + j + x] / (float)centre) > 7.000000000000000e-01)
			{   
			    if (((x == 0) && (mid[(i + (2 * y)) * 76 + j] > 7) && (mid[(i + (2 * y)) * 76 + j - 1] > 7) && (mid[(i + (2 * y)) * 76 + j + 1] > 7)) || ((y == 0) && (mid[(i) * 76 + j + (2 * x)] > 7) && (mid[(i + 1) * 76 + j + (2 * x)] > 7) && (mid[(i - 1) * 76 + j + (2 * x)] > 7)))
			    {   
				mid[(i) * 76 + j] = 100;
				mid[(i + y) * 76 + j + x] = 3;
			    }
			}
		    }
		    else 
		    {   
			b01 = mid[(i - 1) * 76 + j] < 8;
			b12 = mid[(i) * 76 + j + 1] < 8;
			b21 = mid[(i + 1) * 76 + j] < 8;
			b10 = mid[(i) * 76 + j - 1] < 8;



			if (((b01 + b12 + b21 + b10) == 2) && ((b10 | b12) & (b01 | b21)) && ((b01 & ((mid[(i - 2) * 76 + j - 1] < 8) | (mid[(i - 2) * 76 + j + 1] < 8))) | (b10 & ((mid[(i - 1) * 76 + j - 2] < 8) | (mid[(i + 1) * 76 + j - 2] < 8))) | (b12 & ((mid[(i - 1) * 76 + j + 2] < 8) | (mid[(i + 1) * 76 + j + 2] < 8))) | (b21 & ((mid[(i + 2) * 76 + j - 1] < 8) | (mid[(i + 2) * 76 + j + 1] < 8)))))
			{   



			    mid[(i) * 76 + j] = 100;
			    i-- ;
			    j -= 2;
			    if (i < 4) i = 4;
			    if (j < 4) j = 4;
			}
		    }
		}


		if (n > 2)
		{   
		    b01 = mid[(i - 1) * 76 + j] < 8;
		    b12 = mid[(i) * 76 + j + 1] < 8;
		    b21 = mid[(i + 1) * 76 + j] < 8;
		    b10 = mid[(i) * 76 + j - 1] < 8;
		    if ((b01 + b12 + b21 + b10) > 1)
		    {   
			b00 = mid[(i - 1) * 76 + j - 1] < 8;
			b02 = mid[(i - 1) * 76 + j + 1] < 8;
			b20 = mid[(i + 1) * 76 + j - 1] < 8;
			b22 = mid[(i + 1) * 76 + j + 1] < 8;
			p1 = b00 | b01;
			p2 = b02 | b12;
			p3 = b22 | b21;
			p4 = b20 | b10;

			if (((p1 + p2 + p3 + p4) - ((b01 & p2) + (b12 & p3) + (b21 & p4) + (b10 & p1))) < 2)
			{   
			    mid[(i) * 76 + j] = 100;
			    i-- ;
			    j -= 2;
			    if (i < 4) i = 4;
			    if (j < 4) j = 4;
			}
		    }
		}
	    }
}

#line 688 "susan.cc"
get_image::get_image(unsigned int _idcnt, i_sender (&start), unsigned char (&in_sc)[7220], char (&filename)[200])
    : _specc::behavior(_idcnt), start(start), in_sc(in_sc), filename(filename)
{   /* nothing */
}

get_image::~get_image(void)
{   /* nothing */
}

#line 19 "./get_image.sc"
int get_image::getint(struct _IO_FILE *fd)
{   
    int c; int i;
    char dummy[10000];

    c = _IO_getc(fd);
    while(1)
    {   
	if (c == '#')
	    fgets(dummy, 9000, fd);
	if (c == ( -1))
	{    fprintf(stderr, "Image %s not binary PGM.\n", "is"); exit(0);
	}

#line 30 "./get_image.sc"
	;
	if (c >= '0' && c <= '9')
	    break;
	c = _IO_getc(fd);
    }


    i = 0;
    while(1) {
	i = (i * 10) + (c - '0');
	c = _IO_getc(fd);
	if (c == ( -1)) return (i);
	if (c < '0' || c > '9') break;
    }

    return (i);
}

void get_image::main(void)
{   
    struct _IO_FILE *fd;
    char header[100];
    int tmp;
    int handshake = 0;

    if ((fd = fopen(filename, "r")) == ((void *)0))
    {    fprintf(stderr, "Can't input image %s.\n", filename); exit(0);
    }

#line 56 "./get_image.sc"
    ;




    header[0] = fgetc(fd);
    header[1] = fgetc(fd);

    if ( !(header[0] == 'P' && header[1] == '5'))
    {    fprintf(stderr, "Image %s does not have binary PGM header.\n", filename); exit(0);
    }

#line 65 "./get_image.sc"
    ;

    tmp = getint(fd);
    tmp = getint(fd);
    tmp = getint(fd);

    if (fread(in_sc, 1, 76 * 95, fd) == 0)
    {    fprintf(stderr, "Image %s is wrong size.\n", filename); exit(0);
    }

#line 72 "./get_image.sc"
    ;

    fclose(fd);

    start.send( &handshake, sizeof(unsigned int));
}

#line 775 "susan.cc"
put_image::put_image(unsigned int _idcnt, i_receiver (&rec), char (&filename)[200])
    : _specc::behavior(_idcnt), rec(rec), filename(filename)
{   /* nothing */
}

put_image::~put_image(void)
{   /* nothing */
}

#line 17 "./put_image.sc"
void put_image::main(void)
{   
    struct _IO_FILE *fd;
    rec.receive(out_sc, 76 * 95 * sizeof(unsigned char));

    if ((fd = fopen(filename, "w")) == ((void *)0))
    {    fprintf(stderr, "Can't output image%s.\n", filename); exit(0);
    }

#line 23 "./put_image.sc"
    ;

    fprintf(fd, "P5\n");
    fprintf(fd, "%d %d\n", 76, 95);
    fprintf(fd, "255\n");

    if (fwrite(out_sc, 76 * 95, 1, fd) != 1)
    {    fprintf(stderr, "Can't write image %s.\n", filename); exit(0);
    }

#line 30 "./put_image.sc"
    ;

    fclose(fd);
    exit(0);
}

#line 813 "susan.cc"
susan::susan(unsigned int _idcnt, i_receiver (&start), unsigned char (&image_buffer)[7220], i_sender (&sd))
    : _specc::behavior(_idcnt), start(start), image_buffer(image_buffer), sd(sd),
    _scc_const_port_0(0),
    D(_IDcnt, start, image_buffer, r, mid),
    E(_IDcnt, image_buffer, mid, _scc_const_port_0, sd),
    S(_IDcnt, r, mid)
{   /* nothing */
}

susan::~susan(void)
{   /* nothing */
}

#line 35 "susan.sc"
void susan::main(void) {
    { enum { _scc_state_0, _scc_state_D, _scc_state_S, _scc_state_E } _scc_next_state = _scc_state_D; do switch(_scc_next_state) {
	    case _scc_state_D: { D.main(); { _scc_next_state = _scc_state_S; break; } }
	    case _scc_state_S: { S.main(); { _scc_next_state = _scc_state_E; break; } }
	    case _scc_state_E: { E.main(); { _scc_next_state = _scc_state_D; break; } } case _scc_state_0: { _scc_next_state = _scc_state_0; break; } } while(_scc_next_state != _scc_state_0);
    }
}

#line 836 "susan.cc"
Main::Main(unsigned int _idcnt)
    : _specc::class_type(_idcnt),
    SIZE(7220ul),
    _scc_const_port_0(7220ul),
    mal_image(_scc_const_port_0),
    monitor(_IDcnt, mal_image, outputfile),
    start(),
    stimulus(_IDcnt, start, image_buffer, inputfile),
    susan_fsm(_IDcnt, start, image_buffer, mal_image)
{   
    char _scc_init_inputfile[200] = 

#line 49 "susan.sc"
    "input_small.pgm";
    
    {   unsigned int _scc_index_0;
	for(_scc_index_0 = 0; _scc_index_0 < 200; _scc_index_0++)
	    inputfile[_scc_index_0] = _scc_init_inputfile[_scc_index_0];
    }
    char _scc_init_outputfile[200] = 

#line 50 "susan.sc"
    "output_edge.pgm";
    
    {   unsigned int _scc_index_0;
	for(_scc_index_0 = 0; _scc_index_0 < 200; _scc_index_0++)
	    outputfile[_scc_index_0] = _scc_init_outputfile[_scc_index_0];
    }
}

#line 867 "susan.cc"
Main::~Main(void)
{   /* nothing */
}

#line 59 "susan.sc"
int Main::main(void) {
    { _specc::fork _scc_fork_0(&stimulus), _scc_fork_1(&susan_fsm), _scc_fork_2(&monitor); _specc::par(
	    &_scc_fork_0, 
	    &_scc_fork_1, 
	    &_scc_fork_2, ((_specc::fork*)0));
    }
    return 0;
}

#line 882 "susan.cc"
Main _scc_main(_IDcnt);

int main(void)
{   
    int _scc_main_return;
    
    _specc::start();
    _scc_main_return = _scc_main.main();
    _specc::end();
    return(_scc_main_return);
}

void _scc_bit4_err_handle(
    const _bit4& bit4vec)
{   
    char temp_bits[1024], *p;
    p=bit2str(2,&temp_bits[1023], bit4vec);
    _specc::abort(
	"ERROR:\t Casting a bit4 vector failed \n"
	"Bit4 vector contains X/Z values %s\n"
	"Simulation aborted.\n", p);
	
}

//////////////////////////////////////////////////////////////////////
// End of file susan.cc
//////////////////////////////////////////////////////////////////////

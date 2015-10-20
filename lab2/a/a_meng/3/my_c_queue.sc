// c_queue.sc:	type-less, fixed-size queue for use with 1 to N threads
// (general queue between any number of senders and receivers)
//
// author: Rainer Doemer
//
// modifications: (most recent first)
//
// 10/14/05 RD	avoid including platform dependent header files (bug #39)
// 04/22/03 RD	added 'const' qualifier to sender interface
// 10/04/02 RD	added rule about safety of exceptions
// 04/05/02 RD	replaced NULL with 0 (Linux problem)
// 02/14/02 RD	fixed and refined dynamic memory handling
// 02/13/02 RD	applied naming convention, integrated with distribution
// 02/13/02 RD	converted 'size' from template parameter into constant port
// 02/05/02 RD	parameterized name of the queue
// 02/04/02 RD	refined usage rules
// 02/01/02 RD	defined specific features and inserted actual code
// 01/31/02 RD	initial version
//
//
// interface rules:
//
// - see files i_sender.sc, i_receiver.sc, i_tranceiver.sc
//
// channel rules:
//
// - the queue operates in first-in-first-out (FIFO) mode
// - one channel instance is required for each queue
// - up to N threads may use the same channel instance, N=2**32-1
// - the size (number of bytes) of the queue must be specified at the time
//   of the channel instantiation (by a constant port mapping); valid sizes
//   are in the range from 1 to M=2**32-1 bytes
// - each connected thread may act as a receiver or sender or both
//   (depending on the interface used)
// - a sender calls send() to store a packet of data into the queue
// - a receiver calls receive() to retrieve a packet of data from the queue
// - data packets may vary in size; packet size must be in the range between
//   1 and the specified size of the queue, inclusively
// - if different packet sizes are used with the same queue, a receiver
//   may receive only partial and/or multiple packets depending on the
//   requested packet size (the queue does not take care about packet sizes,
//   it simple handles the bytes contained in the packets)
// - if insufficient space is available in the queue, send() will suspend
//   the calling thread until sufficient space becomes available
// - if insufficient data is available in the queue, receive() will suspend
//   the calling thread until sufficient data becomes available
// - no guarantees are given for fairness of access
// - calling send() or receive() may suspend the calling thread indefinitely
// - this channel is only safe with respect to exceptions, if any exceptions
//   are guaranteed to occur only for all communicating threads simultaneously;
//   the behavior is undefined, if any exceptions occur only for a subset
//   of the communicating threads
// - no restrictions exist for use of 'waitfor'
// - no restrictions exist for use of 'wait', 'notify', 'notifyone'


//#include <stdio.h>	// avoid platform-dependent contents
extern void perror(const char*);

//#include <string.h>	// avoid platform-dependent contents
extern void *memcpy(void*, const void*, unsigned int);

//#include <stdlib.h>	// avoid platform-dependent contents
extern void abort(void);
extern void *malloc(unsigned int);
extern void free(void*);


import "i_sender";
import "i_receiver";
import "i_tranceiver";
import "os";


channel my_c_queue(in const unsigned long size, OSAPI rtos)
	implements i_sender, i_receiver, i_tranceiver
{
    note _SCE_STANDARD_LIB = { "my_c_queue" };
  
    event         r,
                  s;
    unsigned long wr = 0;
    unsigned long ws = 0;
    unsigned long p = 0;
    unsigned long n = 0;
    char          *buffer = 0;

    void setup(void)
    {
	if (!buffer)
	{
	    if (!(buffer = (char*) malloc(size)))
	    {
		perror("c_queue");
		abort();
	    }
	}
    }

    void cleanup(void)
    {
	if (! n)
	{
	    free(buffer);
	    buffer = 0;
	}
    }

    void receive(void *d, unsigned long l)
    {
	unsigned long p0;
	int position;

	while(l > n)
	{
	    wr++;
	    position = rtos.pre_wait();
	    wait r;
	    rtos.post_wait(position);
	    wr--;
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
	    memcpy(d, &buffer[p0], l);
	    n -= l;
	}
	else
	{
	    memcpy(d, &buffer[p0], size-p0);
	    memcpy(((char*)d)+(size-p0), &buffer[0], l-(size-p0));
	    n -= l;
	}

	if (ws)
	{
	    notify s;
	}

	cleanup();
    }

    void send(const void *d, unsigned long l)
    {
	int position;
	while(l > size - n)
	{
	    ws++;
	    position = rtos.pre_wait();
	    wait s;
	    rtos.post_wait(position);
	    ws--;
	}

	setup();

	if (l <= size - p)
	{
	    memcpy(&buffer[p], d, l);
	    p += l;
	    n += l;
	}
	else
	{
	    memcpy(&buffer[p], d, size-p);
	    memcpy(&buffer[0], ((char*)d)+(size-p), l-(size-p));
	    p = l-(size-p);
	    n += l;
	}

	if (wr)
	{
	    notify r;
	}
    }
};


// EOF c_queue.sc

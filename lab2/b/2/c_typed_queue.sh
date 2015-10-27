// c_typed_queue.sh:	template (macro) for a typed, fixed-size queue
//			for use with 1 to N threads
// (typed, fixed-size packet queue between any number of senders and receivers)
//
// author: Rainer Doemer
//
// modifications: (most recent first)
//
// 07/30/10 GS	Added combined channel + types definition 
// 04/05/06 RD	bug fix: force construction of buffer space (for bitvectors)
// 10/14/05 RD	avoid including platform dependent header files (bug #39)
// 04/25/03 RD	changed receiver interface to support array types
// 04/22/03 RD	added comment about non-support of array types
// 10/04/02 RD	added rule about safety of exceptions
// 04/05/02 RD	replaced NULL with 0 (Linux problem)
// 02/14/02 RD	converted 'size' from template parameter into constant port
// 02/13/02 RD	applied naming convention, integrated with distribution
// 02/08/02 RD	converted into macro version, started naming convention
// 02/05/02 RD	initial version (based on queue.sc)
//
//
// template instantiation parameters (macro arguments):
//
// - type:     the SpecC type of the data to be transferred (i.e. bit[64]);
//             valid types are all basic and composite types including arrays
// - typename: identifier describing the data type (i.e. bit64)
//
// interface rules:
//
// - see files i_typed_sender.sh, i_typed_receiver.sh, i_typed_tranceiver.sh
// - appropriate interfaces must be defined before the channel instantiation
//
// channel rules:
//
// - the queue operates in first-in-first-out (FIFO) mode
// - one channel instance is required for each queue
// - up to N threads may use the same channel instance, N=2**32-1
// - the size (number of packets) of the queue must be specified at the time
//   of the channel instantiation (by a constant port mapping); valid sizes
//   are in the range from 1 to M=2**32-1 packets
// - each connected thread may act as a receiver or sender or both
//   (depending on the interface used)
// - a sender calls send() to store a packet of data into the queue
// - a receiver calls receive() to retrieve a packet of data from the queue
// - data packets must be of the specified type
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


#ifndef C_TYPED_QUEUE_SH
#define C_TYPED_QUEUE_SH

//#include <stdio.h>	// avoid platform-dependent contents
extern void perror(const char*);

//#include <stdlib.h>	// avoid platform-dependent contents
extern void abort(void);
extern void *malloc(unsigned int);
extern void free(void*);
extern void *memcpy(void*, const void*, unsigned int);


#include <i_typed_sender.sh>
#include <i_typed_receiver.sh>
#include <i_typed_tranceiver.sh>
import "os";

#define DEFINE_C_TYPED_QUEUE(typename, type, OSAPI)			\
									\
channel c_ ## typename ## _queue(in const unsigned long size, OSAPI os)	\
	implements i_ ## typename ## _sender,				\
		i_ ## typename ## _receiver,				\
		i_ ## typename ## _tranceiver				\
{									\
    note _SCE_STANDARD_LIB = { "c_queue", #typename, #type };		\
									\
    event         r,							\
                  s;							\
    unsigned long wr = 0;						\
    unsigned long ws = 0;						\
    unsigned long p = 0;						\
    unsigned long n = 0;						\
    type          *buffer = 0;						\
									\
    void setup(void)							\
    {									\
	if (!buffer)							\
	{								\
	    unsigned long i;	/* (bug fix 04/05/06, RD/AG) */		\
	    type	dummy;	/* properly construct one element */	\
									\
	    if (!(buffer = (type*) malloc(sizeof(type)*size)))		\
	    {								\
		perror("c_typed_queue");				\
		abort();						\
	    }								\
	    for(i=0; i<size; i++)	/* for bitvectors, we need to */\
	    {				/* "copy" the vptr over	*/	\
		memcpy(&buffer[i], &dummy, sizeof(type));		\
	    }								\
	}								\
    }									\
									\
    void cleanup(void)							\
    {									\
	if (! n)							\
	{								\
	    free(buffer);						\
	    buffer = 0;							\
	}								\
    }									\
									\
    void receive(type *d)						\
    {									\
	Task *t;							\
	while(! n)							\
	{								\
	    wr++;							\
            t=os.pre_wait();						\
	    wait r;							\
	    os.post_wait(t);						\
	    wr--;							\
	}								\
									\
	if (n <= p)							\
	{								\
	    *d = buffer[p - n];						\
	}								\
	else								\
	{								\
	    *d = buffer[p + size - n];					\
	}								\
	n--;								\
									\
	if (ws)								\
	{								\
	    notify s;							\
	}								\
									\
	cleanup();							\
    }									\
									\
    void send(type d)							\
    {	                                                                \
	Task *t;							\
	while(n >= size)						\
	{								\
	    ws++;                                                       \
	    t=os.pre_wait();						\
	    wait s;						        \
	    os.post_wait(t);						\
	    ws--;							\
	}								\
									\
	setup();							\
									\
	buffer[p] = d;							\
	p++;								\
	if (p >= size)							\
	{								\
	    p = 0;							\
	}								\
	n++;								\
									\
	if (wr)								\
	{								\
	    notify r;							\
	}								\
    }									\
};


/**
 * Combined definition of a typed queue and its 
 * interfaces (sender, receiver and transceiver).
 *
 *@param typename   user defined name for queue type
 *@param type       SpecC basic or composite type
 */
#define DEFINE_IC_TYPED_QUEUE(typename, type, os)	                       \
                                                                       \
DEFINE_I_TYPED_TRANCEIVER(typename, type)                              \
DEFINE_I_TYPED_SENDER(typename, type)                                  \
DEFINE_I_TYPED_RECEIVER(typename, type)                                \
DEFINE_C_TYPED_QUEUE(typename, type, os)  



#endif /* C_TYPED_QUEUE_SH */


// EOF c_typed_queue.sh

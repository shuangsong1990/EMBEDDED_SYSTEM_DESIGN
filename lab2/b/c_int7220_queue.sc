//#include <c_typed_queue.sh>	/* make the template available */
//import "c_typed_queue.sh";

import "os";

#ifndef I_TYPED_SENDER_SH
#define I_TYPED_SENDER_SH
#define DEFINE_I_TYPED_SENDER(typename, type)				\
									\
interface i_ ## typename ## _sender					\
{									\
    note _SCE_STANDARD_LIB = { "i_sender", #typename, #type };		\
									\
    void send(type d);							\
};
#endif /* I_TYPED_SENDER_SH */




#ifndef I_TYPED_RECEIVER_SH
#define I_TYPED_RECEIVER_SH
#define DEFINE_I_TYPED_RECEIVER(typename, type)				\
									\
interface i_ ## typename ## _receiver					\
{									\
    note _SCE_STANDARD_LIB = { "i_receiver", #typename, #type };	\
									\
    void receive(type *d);						\
};
#endif /* I_TYPED_RECEIVER_SH */



#ifndef I_TYPED_TRANCEIVER_SH
#define I_TYPED_TRANCEIVER_SH
#define DEFINE_I_TYPED_TRANCEIVER(typename, type)			\
									\
interface i_ ## typename ## _tranceiver					\
{									\
    note _SCE_STANDARD_LIB = { "i_tranceiver", #typename, #type };	\
									\
    void send(type d);							\
    void receive(type *d);						\
};
#endif /* I_TYPED_TRANCEIVER_SH */




#ifndef C_TYPED_QUEUE_SH
#define C_TYPED_QUEUE_SH

//#include <stdio.h>	// avoid platform-dependent contents
extern void perror(const char*);

//#include <stdlib.h>	// avoid platform-dependent contents
extern void abort(void);
extern void *malloc(unsigned int);
extern void free(void*);
extern void *memcpy(void*, const void*, unsigned int);


//#include <i_typed_sender.sh>
//#include <i_typed_receiver.sh>
//#include <i_typed_tranceiver.sh>

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



typedef int  int7220[7220];

DEFINE_I_TYPED_TRANCEIVER(int7220, int7220)
DEFINE_I_TYPED_SENDER(int7220, int7220)
DEFINE_I_TYPED_RECEIVER(int7220, int7220)
DEFINE_C_TYPED_QUEUE(int7220, int7220, OSAPI)


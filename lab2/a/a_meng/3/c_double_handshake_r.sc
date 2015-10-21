// c_double_handshake.sc: type-less 2-way handshake between sender and receiver
// (rendezvous for data transfer between a sender and a receiver)
//
// author: Rainer Doemer
//
// modifications: (most recent first)
//
// 10/14/05 RD	avoid including platform dependent header files (bug #39)
// 08/10/04 RD	made receiver wait for its own 'ack' such that both
//		receiver and sender exit in the same synch. cycle
//		(otherwise a tranceiver may receive its own 'ack'
//		when calling send immediately after receive)
// 04/25/03 RD	added support of interface i_typed_tranceiver
// 04/22/03 RD	added 'const' qualifier to sender interface
// 10/04/02 RD	added rule about safety of exceptions
// 02/18/02 RD	applied naming convention, integrated with distribution
// 02/07/02 RD	clarified properties, fixed code
// 02/06/02 RD  separated interfaces for send and receive
// 01/31/02 RD	brush up, moved into separate file "double_handshake.sc"
// 12/27/01 RD	initial version
//
//
// interface rules:
//
// - see files i_sender.sc, i_receiver.sc, i_tranceiver.sc
//
// channel rules:
//
// - this double-handshake channel can be seen as a queue (see c_queue.sc)
//   with size 0; because no data can be stored in the channel, the
//   sender and receiver must meet in a rendezvous to exchange the data
// - exactly one receiver and one sender thread may use the same channel
//   instance at the same time; if used by more than one sender or receiver,
//   the behavior of the channel is undefined
// - the same channel instance may be used multiple times in order to
//   transfer multiple data packets from the sender to the receiver
// - using the tranceiver interface, the channel may be used bidirectionally
// - the sender calls send() to send a packet of data to the receiver
// - the receiver calls receive() to receive a packet of data from the sender
// - data packets may vary in size; valid sizes are in the range
//   between 0 and 2**32-1 inclusively
// - if different packet sizes are used with the same channel, the user
//   has to ensure that the data size of the sender always matches
//   the data size expected by the receiver; it is an error if the sizes
//   in a transaction don't match
// - the channel operates in rendezvous fashion; a call to send() will
//   suspend the sender until the receiver calls receive(), and vice versa;
//   when both are ready, data is transferred from the sender to the receiver
//   and both can resume their execution
// - calling send() or receive() may suspend the calling thread indefinitely
// - this channel is only safe with respect to exceptions, if any exceptions
//   are guaranteed to occur only for all communicating threads simultaneously;
//   the behavior is undefined, if any exceptions occur only for a subset
//   of the communicating threads
// - no restrictions exist for use of 'waitfor'
// - no restrictions exist for use of 'wait', 'notify', 'notifyone'


//#include <string.h>	// avoid platform-dependent contents
extern void *memcpy(void*, const void*, unsigned int);

//#include <stdlib.h>	// avoid platform-dependent contents
extern void abort(void);

import "i_sender";
import "i_receiver";
import "i_tranceiver";
import "os";


channel my_c_double_handshake_r (OSAPI rtos) implements i_sender, i_receiver, i_tranceiver
{
    note _SCE_STANDARD_LIB = { "my_c_double_handshake_r" };
  
    event         req,
                  ack;
    bool          v = false,
                  w = false;
    const void    *tmpd;
    unsigned long tmpl;

    //int position = 0;
    struct Task t;

    void receive(void *d, unsigned long l)
    {
	if (!v)
	{
	    w = true;
	    t = rtos.pre_wait();
	    wait req;
	    rtos.post_wait(t);
	    w = false;
	}
	if (l != tmpl)
	{
	    abort();
	}
	memcpy(d, tmpd, l);
	v = false;
	notify ack;
	t = rtos.pre_wait();
	wait ack;
	rtos.post_wait(t);
    }

    void send(const void *d, unsigned long l)
    {
	tmpd = d;
	tmpl = l;
	v = true;
	if (w)
	{
	    notify req;
	}
	wait ack;
    }
};


// EOF c_double_handshake.sc

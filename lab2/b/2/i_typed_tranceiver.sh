// i_typed_tranceiver.sh: template (macro) for a typed tranceiver interface
//
// author: Rainer Doemer
//
// modifications: (most recent first)
//
// 04/25/03 RD	changed receiver interface to support array types
// 04/22/03 RD	added comment about non-support of array types
// 02/13/02 RD	applied naming convention, integrated with distribution
// 02/08/02 RD	converted into macro version
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
// - a connected thread acts as a transceiver (sender and receiver)
// - a call to send() sends out a packet of data to a connected channel
// - a call to receive() receives a packet of data from a connected channel
// - data packets must be of the specified type
// - calling send() or receive() may suspend the calling thread indefinitely


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


// EOF i_typed_tranceiver.sh

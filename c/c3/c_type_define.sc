#include <c_typed_queue.sh>

DEFINE_I_TYPED_SENDER(r, int)
DEFINE_I_TYPED_RECEIVER(r, int)
DEFINE_C_TYPED_QUEUE(r, int)

DEFINE_I_TYPED_SENDER(mid, unsigned char)
DEFINE_I_TYPED_RECEIVER(mid, unsigned char)
DEFINE_C_TYPED_QUEUE(mid, unsigned char)

DEFINE_I_TYPED_SENDER(in, unsigned char)
DEFINE_I_TYPED_RECEIVER(in, unsigned char)
DEFINE_C_TYPED_QUEUE(in, unsigned char)

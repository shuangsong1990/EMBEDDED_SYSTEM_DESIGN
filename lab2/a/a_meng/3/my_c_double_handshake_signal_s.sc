
import "i_send";
import "i_receive";
import "os";


channel my_c_double_handshake_signal_s (OSAPI rtos) implements i_send, i_receive
{

    event         req,
                  ack;
    bool          v = false,
                  w = false;

    int position = 0;

    void receive(void)
    {
        if (!v)
        {
            w = true;
            wait req;
            w = false;
        }
        v = false;
        notify ack;
        wait ack;
    }

    void send(void)
    {
        v = true;
        if (w)
        {
            notify req;
        }
	position = rtos.pre_wait();
        wait ack;
	rtos.post_wait(position);
    }
};

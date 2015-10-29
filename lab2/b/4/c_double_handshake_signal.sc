
import "i_send";
import "i_receive";


channel c_double_handshake_signal implements i_send, i_receive
{

    event         req,
                  ack;
    bool          v = false,
                  w = false;

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
        wait ack;
    }
};




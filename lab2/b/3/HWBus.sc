//////////////////////////////////////////////////////////////////////
// File:   	HWBus.sc
//////////////////////////////////////////////////////////////////////

import "i_send";
import "i_receive";

import "c_handshake";
// Simple hardware bus

#define DATA_WIDTH	32u
#define ADDR_WIDTH	16u

#if DATA_WIDTH == 32u
# define DATA_BYTES 4u
#elif DATA_WIDTH == 16u
# define DATA_BYTES 2u
#elif DATA_WIDTH == 8u
# define DATA_BYTES 1u
#else
# error "Invalid data width"
#endif


/* ----- Physical layer, bus protocol ----- */

// Protocol primitives
interface IMasterHardwareBusProtocol
{
  void masterWrite(unsigned bit[ADDR_WIDTH-1:0] a, unsigned bit[DATA_WIDTH-1:0] d);
  void masterRead (unsigned bit[ADDR_WIDTH-1:0] a, unsigned bit[DATA_WIDTH-1:0] *d);
};

interface ISlaveHardwareBusProtocol
{
  void slaveWrite(unsigned bit[ADDR_WIDTH-1:0] a, unsigned bit[DATA_WIDTH-1:0] d);
  void slaveRead (unsigned bit[ADDR_WIDTH-1:0] a, unsigned bit[DATA_WIDTH-1:0] *d);
};

// Master protocol implementation
channel TLMHardwareBus() implements IMasterHardwareBusProtocol, ISlaveHardwareBusProtocol 
{
	signal unsigned bit[ADDR_WIDTH-1:0] A;
	signal unsigned bit[DATA_WIDTH-1:0] D;
	event ack, rdy;

  void masterWrite(unsigned bit[ADDR_WIDTH-1:0] a, unsigned bit[DATA_WIDTH-1:0] d)
  {

	A = a;
	D = d;
	notify rdy;
	wait ack;	
	waitfor(34000);
  }

  void masterRead (unsigned bit[ADDR_WIDTH-1:0] a, unsigned bit[DATA_WIDTH-1:0] *d)
  {
	A = a;
	notify rdy;
	wait ack;
	*d = D;
	waitfor(39000);
	
  }


  void slaveRead (unsigned bit[ADDR_WIDTH-1:0] a, unsigned bit[DATA_WIDTH-1:0] *d){
//	do{
//		wait rdy;
//	}while(a != A);

	t1: wait rdy;
	if (a != A)
		goto t1;

	*d = D;
	notify ack;
	
  }

  void slaveWrite(unsigned bit[ADDR_WIDTH-1:0] a, unsigned bit[DATA_WIDTH-1:0] d){
	do{
		wait rdy;
	}while(a != A);

	D = d;
	notify ack;
  }


};

// Slave protocol implementation
/*
channel SlaveHardwareBus(in  signal unsigned bit[ADDR_WIDTH-1:0] A,
                             signal unsigned bit[DATA_WIDTH-1:0] D,
                         in  signal unsigned bit[1]    ready,
                         out signal unsigned bit[1]    ack)
  implements ISlaveHardwareBusProtocol
{
  void slaveWrite(unsigned bit[ADDR_WIDTH-1:0] a, unsigned bit[DATA_WIDTH-1:0] d)
  {
    do {
      t1: while(!ready) wait(ready);
      t2: if(a != A) {
            waitfor(1000); // avoid hanging from t2 to t1
            goto t1;
          }
          else {
            D = d;
            waitfor(12000);
          }
      t3: ack = 1;
          while(ready) wait(ready);
      t4: waitfor(7000);
      t5: ack = 0;
    }
    timing {
      range(t2; t3; 10000; 20000);
      range(t4; t5; 5000; 15000);
    }
  }

  void slaveRead (unsigned bit[ADDR_WIDTH-1:0] a, unsigned bit[DATA_WIDTH-1:0] *d)
  {
    do {
      t1: while(!ready) wait(ready);
      t2: if(a != A) {
            waitfor(1000);  // avoid hanging from t2 to t1
	    goto t1;
	  }
          else {
            *d = D;
            waitfor(12000);
          }
      t3: ack = 1;
          while(ready) wait(ready);
      t4: waitfor(7000);
      t5: ack = 0;
    }
    timing {
      range(t2; t3; 10000; 20000);
      range(t4; t5; 5000; 15000);
    }
  }
};
*/


/* -----  Physical layer, interrupt handling ----- */

channel MasterHardwareSyncDetect(i_receive intr)
  implements i_receive
{
  void receive(void)
  {
	intr.receive();
  }
};

channel SlaveHardwareSyncGenerate(i_send intr)
  implements i_send
{
  void send(void)
  {
	intr.send();
  }
};


/* -----  Media access layer ----- */

interface IMasterHardwareBusLinkAccess
{
  void MasterRead(int addr, void *data, unsigned long len);
  void MasterWrite(int addr, const void* data, unsigned long len);
};
  
interface ISlaveHardwareBusLinkAccess
{
  void SlaveRead(int addr, void *data, unsigned long len);
  void SlaveWrite(int addr, const void* data, unsigned long len);
};

channel MasterHardwareBusLinkAccess(IMasterHardwareBusProtocol protocol)
  implements IMasterHardwareBusLinkAccess
{
  void MasterWrite(int addr, const void* data, unsigned long len)
  {    
    unsigned long i;
    unsigned char *p;
    unsigned bit[DATA_WIDTH-1:0] word = 0;
   
    for(p = (unsigned char*)data, i = 0; i < len; i++, p++)
    {
      word = (word<<8) + *p;
      
      if(!((i+1)%DATA_BYTES)) {
	protocol.masterWrite(addr, word);
	word = 0;
      }
    }
    
    if(i%DATA_BYTES) {
      word <<= 8 * (DATA_BYTES - (i%DATA_BYTES));
      protocol.masterWrite(addr, word);
    }    
  }
  
  void MasterRead(int addr, void* data, unsigned long len)
  {
    unsigned long i;
    unsigned char* p;
    unsigned bit[DATA_WIDTH-1:0] word;
   
    for(p = (unsigned char*)data, i = 0; i < len; i++, p++)
    {
      if(!(i%DATA_BYTES)) {
	protocol.masterRead(addr, &word);
      }

      *p = word[DATA_WIDTH-1:DATA_WIDTH-8];
      word = word << 8;      
    }
  }
};

channel SlaveHardwareBusLinkAccess(ISlaveHardwareBusProtocol protocol)
  implements ISlaveHardwareBusLinkAccess
{
  void SlaveWrite(int addr, const void* data, unsigned long len)
  {    
    unsigned long i;
    unsigned char *p;
    unsigned bit[DATA_WIDTH-1:0] word = 0;
   
    for(p = (unsigned char*)data, i = 0; i < len; i++, p++)
    {
      word = (word<<8) + *p;
      
      if(!((i+1)%DATA_BYTES)) {
	protocol.slaveWrite(addr, word);
	word = 0;
      }
    }
    
    if(i%DATA_BYTES) {
      word <<= 8 * (DATA_BYTES - (i%DATA_BYTES));
      protocol.slaveWrite(addr, word);
    }    
  }
  
  void SlaveRead(int addr, void* data, unsigned long len)
  {
    unsigned long i;
    unsigned char* p;
    unsigned bit[DATA_WIDTH-1:0] word;
   
    for(p = (unsigned char*)data, i = 0; i < len; i++, p++)
    {
      if(!(i%DATA_BYTES)) {
	protocol.slaveRead(addr, &word);
      }

      *p = word[DATA_WIDTH-1:DATA_WIDTH-8];
      word = word << 8;      
    }
  }
};


/* -----  Bus instantiation example ----- */

// Bus protocol interfaces
interface IMasterHardwareBus
{
  void MasterRead(unsigned bit[ADDR_WIDTH-1:0] addr, void *data, unsigned long len);
  void MasterWrite(unsigned bit[ADDR_WIDTH-1:0] addr, const void* data, unsigned long len);
  
  void MasterSyncReceive0();
  void MasterSyncReceive1();
};
  
interface ISlaveHardwareBus
{
  void SlaveRead(unsigned bit[ADDR_WIDTH-1:0] addr, void *data, unsigned long len);
  void SlaveWrite(unsigned bit[ADDR_WIDTH-1:0] addr, const void* data, unsigned long len);
  
  void SlaveSyncSend0();
  void SlaveSyncSend1();
};


// Bus protocol channel
channel HardwareBus()
  implements IMasterHardwareBus, ISlaveHardwareBus
{
  // wires
  //signal unsigned bit[ADDR_WIDTH-1:0] A;
  //signal unsigned bit[DATA_WIDTH-1:0] D;
  //signal unsigned bit[1]    ready = 0;
  //signal unsigned bit[1]    ack = 0;


  // interrupts
  c_handshake	intr0, intr1;

  MasterHardwareSyncDetect  MasterSync0(intr0);
  SlaveHardwareSyncGenerate SlaveSync0(intr0);

  MasterHardwareSyncDetect  MasterSync1(intr1);
  SlaveHardwareSyncGenerate SlaveSync1(intr1);
  
//  MasterHardwareBus Master(A, D, ready, ack);
//  SlaveHardwareBus  Slave(A, D, ready, ack);

  //MasterHardwareBus Master(A, D);
  //SlaveHardwareBus  Slave(A, D);
  TLMHardwareBus TLM;  


  MasterHardwareBusLinkAccess MasterLink(TLM);
  SlaveHardwareBusLinkAccess SlaveLink(TLM);

  
  void MasterRead(unsigned bit[ADDR_WIDTH-1:0] addr, void *data, unsigned long len) {
    MasterLink.MasterRead(addr, data, len);
  }
  
  void MasterWrite(unsigned bit[ADDR_WIDTH-1:0] addr, const void *data, unsigned long len) {
    MasterLink.MasterWrite(addr, data, len);
  }
  
  void SlaveRead(unsigned bit[ADDR_WIDTH-1:0] addr, void *data, unsigned long len) {
    SlaveLink.SlaveRead(addr, data, len);
  }
  
  void SlaveWrite(unsigned bit[ADDR_WIDTH-1:0] addr, const void *data, unsigned long len) {
    SlaveLink.SlaveWrite(addr, data, len);
  }

  void MasterSyncReceive0() {
    MasterSync0.receive();
  }
  void MasterSyncReceive1() {
    MasterSync1.receive();
  }
  
  void SlaveSyncSend0() {
    SlaveSync0.send();
  }
  
  void SlaveSyncSend1() {
    SlaveSync1.send();
  }
};

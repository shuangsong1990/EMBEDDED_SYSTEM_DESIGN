#include "susan.sh"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

import "c_handshake";

struct Task{
	unsigned int id;
};




interface OSAPI
{
	struct Task task_create(char* a);
	void push_t(struct Task t);
	struct Task pop_t(void);
	void time_wait(unsigned int t);
};


channel OS implements OSAPI{

        struct Task current;
	struct Task rdyq[10];

	c_handshake e0, e1, e2, e3, e4, e5, e6, e7, e8, e9;

        unsigned int head = 0;
        unsigned int tail = 0;

	unsigned int task_pool = 1;	

	struct Task task_create(char* a){
		if (a != ""){
			struct Task new_id;
			new_id.id = task_pool;
			task_pool = task_pool + 1;
	//		printf("name is %s and id is %d\n", a, new_id.id);
			return new_id;	
		}else{
			printf("Wrong task name");
		}
	}	
	

	void push_t(struct Task t){
		if( head == tail){
			printf("queue is empty\n");
			exit;
		}else{
			rdyq[tail] = t;
			if(tail == 9){
				tail = 0;
			}else{
				tail = tail + 1;
			}
		}		
	}
	struct Task pop_t(void){
		struct Task t; 
		if(  head == tail){
			printf("queue is full\n");
			exit;
		}else{
			t = rdyq[head];
			if(head == 9){
				head = 0;
			}else{
				head = head + 1;
			}
			return t;
		}
	}

	void task_activate(struct Task t){
		unsigned int position;
		unsigned int i;
		push_t(t);
		for (i = 0; i < 10; i++){
			if(t.id == rdyq[i].id){
				position = i;
			}
		}
		switch(position){
			case 0:
				e0.receive();	
			case 1:
				e1.receive();
			case 2:
				e2.receive();
			case 3:
				e3.receive();
			case 4:
				e4.receive();
			case 5:
				e5.receive();
			case 6:
				e6.receive();
			case 7:
				e7.receive();
			case 8:
				e8.receive();
			case 9:
				e9.receive();
		}
	}
	
	void time_wait(unsigned int t){
		waitfor(t);
	}
	
	struct Task par_start(void){
		struct Task ret_t = current;
		current = dispatch();
		return ret_t;	
	}

	void par_end(struct Task end_t){
		
	}
};


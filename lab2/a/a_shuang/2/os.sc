#include "susan.sh"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

import "c_handshake";

struct Task{
	unsigned int id;
	unsigned int valid;
};




interface OSAPI
{
	struct Task task_create(char* a);
	void push_t(struct Task t);
	struct Task pop_t(void);
	void time_wait(unsigned int t);
	void yield(void);
	struct Task par_start(void);
	void dispatch(void);
	void task_activate(struct Task t);
	void init(struct Task t);
	void task_terminate(void);
};


channel OS implements OSAPI{

        struct Task current;
	struct Task rdyq[10];

	c_handshake e0, e1, e2, e3, e4, e5, e6, e7, e8, e9;

        unsigned int head = 0;
        unsigned int tail = 0;

	unsigned int task_pool = 1;	

	unsigned int start = 0;



	struct Task task_create(char* a){
		if (a != ""){
			struct Task new_id;
			new_id.id = task_pool;
			new_id.valid = 1;
			task_pool = task_pool + 1;
	//		printf("name is %s and id is %d\n", a, new_id.id);
			return new_id;	
		}else{
			printf("Wrong task name");
		}
	}	
	

	void push_t(struct Task t){
		if( (head == tail) && (start == 1) ){
			printf("queue is full\n");
		}else{
			printf( "push task id : %d\n", t.id);
			rdyq[tail] = t;
			if(tail == 9){
				tail = 0;
			}else{
				tail = tail + 1;
			}
			start = 1;
		}		
	}
	struct Task pop_t(void){
		struct Task t; 
		if( ( head == tail) ){
			printf("queue is empty\n");
		}else{
			t = rdyq[head];
			rdyq[head].valid = 0;
			printf( "pop task id : %d\n", t.id);
			if(head == 9){
				head = 0;
			}else{
				head = head + 1;
			}

			return t;
		}
	}

	void dispatch(void){
		unsigned int position;
		position = head;
		current = pop_t();
		//printf ("current.id = %d\n", current.id);
		printf ("position = %d\n", position);
		if(current.id != 0) ////may be wrong
		{
				
			switch(position){
				case 0:
					e0.send();
					break;	
				case 1:
					e1.send();	
					break;	
				case 2:
					e2.send();	
					break;	
				case 3:
					e3.send();	
					break;	
				case 4:
					e4.send();	
					break;	
				case 5:
					e5.send();	
					break;	
				case 6:
					e6.send();	
					break;	
				case 7:
					e7.send();	
					break;	
				case 8:
					e8.send();	
					break;	
				case 9:
					e9.send();	
					break;	
			}
		} 
	}
	void task_activate(struct Task t){
		unsigned int position;
		unsigned int i;
		i = head;
		while ((i >= head )||(i <= tail)){
			if(rdyq[i].id == t.id){
				position = i;
				break;
			}else{
				if( i == 9 ){
					i = 0;
				}
				else{
					i = i + 1;
				}
			}
		}

		//printf("activate head %d tail %d position %d \n", head, tail ,position);		

		/*
		for (i = 0; i < 10; i++){
			if(t.id == rdyq[i].id){
				position = i;
			}
		}
		*/
		switch(position){
			case 0:
				e0.receive();	
					break;	
			case 1:
				e1.receive();
					break;	
			case 2:
				e2.receive();
					break;	
			case 3:
				e3.receive();
					break;	
			case 4:
				e4.receive();
					break;	
			case 5:
				e5.receive();
					break;	
			case 6:
				e6.receive();
					break;	
			case 7:
				e7.receive();
					break;	
			case 8:
				e8.receive();
					break;	
			case 9:
				e9.receive();
					break;	
		}
	}

	void task_terminate(void){
	/*	if(head == tail ) {
			printf("head == tail head %d tail %d\n", head, tail);	
			//pop_t();
			
			printf("current is %d\n", current.id);
			head = head + 1;
		}else{
			printf("terminate head %d tail %d\n", head, tail);
			dispatch();
		}
	*/
		if( (head + 1 == tail ) || (head == 9 && tail == 0)){
			unsigned int i ;
			for ( i=0; i < 10; i++){
				if( rdyq[i].valid == 1)
					printf("valid --------------- i %d id %d valid %d\n", i, rdyq[i].id, rdyq[i].valid);
			}
			printf("TERMINATION head %d tail %d \n", head, tail);
			dispatch();
			printf("head %d tail %d \n", head, tail);
			head = 0;
			tail = 0; /// try
			start = 0;	
		}	
	}
	
	void yield(void){
		unsigned int position;
		unsigned int i;
		struct Task t;
		t = current;
		position = head;
		push_t(t);
		dispatch();

		printf("WRONG POSITION IS %d \n", position);		
	
		i = head;
	
		if ( head != tail ){

			while ((i >= head )||(i <= tail)){
				if(rdyq[i].id == t.id){
					position = i;
					break;
				}else{
					if( i == 9 ){
						i = 0;
					}
					else{
						i = i + 1;
					}
				}
			}
		}	
	
		printf("head is %d ; tail is %d ;  position is %d\n", head, tail, position);

		switch(position){
			case 0:
				printf("e0 wait here\n");
				e0.receive();	
					break;	
			case 1:
				printf("e1 wait here\n");
				e1.receive();
					break;	
			case 2:
				printf("e2 wait here\n");
				e2.receive();
					break;	
			case 3:
				printf("e3 wait here\n");
				e3.receive();
					break;	
			case 4:
				printf("e4 wait here\n");
				e4.receive();
					break;	
			case 5:
				printf("e5 wait here\n");
				e5.receive();
					break;	
			case 6:
				printf("e6 wait here\n");
				e6.receive();
					break;	
			case 7:
				printf("e7 wait here\n");
				e7.receive();
					break;	
			case 8:
				printf("e8 wait here\n");
				e8.receive();
					break;	
			case 9:
				printf("e9 wait here\n");
				e9.receive();
				printf("e9 done here \n\n");
					break;	
		}
			
	}
	void time_wait(unsigned int t){
		waitfor(t);
		printf("time to wait %d\n", t);
		yield();
	}

	
	struct Task par_start(void){
		struct Task ret_t ;
		unsigned int i ;
		ret_t = current;
		dispatch();
		return ret_t;	
	}


	void init(struct Task t){
		current = t;
		start = 0;	
	}



};


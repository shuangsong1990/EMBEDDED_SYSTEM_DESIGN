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
	struct Task pre_wait();
	void post_wait(struct Task t);
};

channel OS implements OSAPI{

        struct Task current;
	struct Task rdyq[20];

	c_handshake e0, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15, e16, e17, e18, e19;

	unsigned int head = 0;
	unsigned int tail = 0;

	unsigned int task_pool = 1;

	unsigned int start = 0;

	struct Task task_create(char* a){

		if (a != ""){
			struct Task new_id;
			new_id.id = task_pool;
			task_pool = task_pool + 1;
			printf("name is %s and id is %d\n", a, new_id.id);
			return new_id;
		}
		else{
			printf("Wrong task name");
		}
	} 

	void push_t(struct Task t){
		if( (head == tail) && (start == 1)){
			printf("queue is full\n");
		}
		else{
			printf( "push task id : %d\n", t.id);
			rdyq[tail] = t;
			if(tail == 19){
				tail = 0;
			}else{
				tail = tail + 1;
			}
			start = 1;
		}
	}
	struct Task pop_t(void){
		struct Task t; 
		if( ( head == tail) ){ // seems to be problematic
			printf("queue is empty\n");
		}
		else{
			t = rdyq[head];
			printf( "pop task id : %d\n", t.id);
			if(head == 19){
				head = 0;
			}else{
				head = head + 1;
			}
	
			if (head == tail){
				start = 0;
			}

			return t;
		}
	}
	void dispatch(void){
		unsigned int position;
		position = head;
		current = pop_t();
		printf ("current.id = %d\n", current.id);
		if(current.id != 0) ////may be wrong
		{
				
			switch(position){
				case 0:
					e0.send();
					printf("e0 send\n");
					break; 
				case 1:
					e1.send();	
					printf("e1 send\n");
					break;
				case 2:
					e2.send();	
					printf("e2 send\n");
					break;
				case 3:
					e3.send();
					printf("e3 send\n");
					break; 
				case 4:
					e4.send();	
					printf("e4 send\n");
					break;
				case 5:
					e5.send();	
					printf("e5 send\n");
					break;
				case 6:
					e6.send();
					printf("e6 send\n");
					break; 
				case 7:
					e7.send();	
					printf("e7 send\n");
					break;
				case 8:
					e8.send();	
					printf("e8 send\n");
					break;
				case 9:
					e9.send();
					printf("e9 send\n");
					break; 
				case 10:
					e10.send();	
					printf("e10 send\n");
					break;
				case 11:
					e11.send();	
					printf("e11 send\n");
					break;
				case 12:
					e12.send();
					printf("e12 send\n");
					break; 
				case 13:
					e13.send();	
					break;
				case 14:
					e14.send();	
					break;
				case 15:
					e15.send();
					break; 
				case 16:
					e16.send();	
					break;
				case 17:
					e17.send();	
					break;
				case 18:
					e18.send();
					break; 
				case 19:
					e19.send();	
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
			}
			else{
			    if (i == 19)
				i = 0;
			    else
				i = i + 1;
			}
		}
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
			case 10:
				e10.receive();
				break;
			case 11:
				e11.receive();
				break;
			case 12:
				e12.receive();	
				break;
			case 13:
				e13.receive();
				break;
			case 14:
				e14.receive();
				break;
			case 15:
				e15.receive();	
				break;
			case 16:
				e16.receive();
				break;
			case 17:
				e17.receive();
				break;
			case 18:
				e18.receive();
				break;
			case 19:
				e19.receive();
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
//			if( (head + 1 <= tail ) || (head == 19 && tail == 0)){
			if ((head + 1 <= tail) || (head + 1 > tail && head + 1 <= tail + 20)){
				unsigned int i ;
				for ( i=0; i < 20; i++){
					if( rdyq[i].valid == 1)
						printf("valid --------------- i %d id %d valid %d\n", i, rdyq[i].id, rdyq[i].valid);
				}
				printf("TERMINATION head %d tail %d \n", head, tail);
				printf("current id is %d\n", current.id);
				dispatch();
				printf("head %d tail %d \n", head, tail);
				head = 0;
				tail = 0; /// try
				start = 0;	
			}	
			else{
				start = 0;
				printf("TERMINATION head %d tail %d\n!!!", head, tail);
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
	
		i = head;
	
		if ( head != tail ){

			while ((i >= head )||(i <= tail)){
				if(rdyq[i].id == t.id){
					position = i;
					break;
				}
				else{
					if( i == 19 ){
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
				break;
			case 10:
				printf("e10 wait here\n");
				e10.receive();
				break;
			case 11:
				printf("e11 wait here\n");
				e11.receive();
				break;
			case 12:
				printf("e12 wait here\n");
				e12.receive();	
				break;
			case 13:
				printf("e13 wait here\n");
				e13.receive();
				break;
			case 14:
				printf("e14 wait here\n");
				e14.receive();
				break;
			case 15:
				printf("e15 wait here\n");
				e15.receive();
				break;
			case 16:
				printf("e16 wait here\n");
				e16.receive();	
				break;
			case 17:
				printf("e17 wait here\n");
				e17.receive();
				break;
			case 18:
				printf("e18 wait here\n");
				e18.receive();
				break;
			case 19:
				printf("e19 wait here\n");
				e19.receive();
				break;
		}	
	}

	struct Task pre_wait(){
		struct Task t;
		t = current;
		//push_t(t);
		dispatch();
		return t;
	/*	
		i = head;
	
		if ( head != tail ){

			while ((i >= head )||(i <= tail)){
				if(rdyq[i].id == t.id){
					position = i;
					break;
				}
				else{
					if( i == 19 ){
						i = 0;
					}
					else{
						i = i + 1;
					}
				}
			}
		}
		printf("pre_wait position %d \n", position);	
		return position;
		*/


	}

	void post_wait(struct Task t){

		int position;

		printf("start post wait\n");
		printf("id of task is %d\n", t.id);
		printf("is start equals to %d\n", start);
		push_t(t);
		if( current.id != t.id){
			dispatch();
		}
		if( tail == 0){
			position = 19;
		}else{
			position = tail - 1;
		}
		switch(position){
			case 0:
				printf("post_wait : e0 wait here\n");
				e0.receive();	
				break;
			case 1:
				printf("post_wait :e1 wait here\n");
				e1.receive();
				break;
			case 2:
				printf("post_wait :e2 wait here\n");
				e2.receive();
				break;
			case 3:
				printf("post_wait :e3 wait here\n");
				e3.receive();	
				break;
			case 4:
				printf("post_wait :e4 wait here\n");
				e4.receive();
				break;
			case 5:
				printf("post_wait :e5 wait here\n");
				e5.receive();
				break;
			case 6:
				printf("post_wait :e6 wait here\n");
				e6.receive();	
				break;
			case 7:
				printf("post_wait :e7 wait here\n");
				e7.receive();
				break;
			case 8:
				printf("post_wait :e8 wait here\n");
				e8.receive();
				break;
			case 9:
				printf("post_wait :e9 wait here\n");
				e9.receive();	
				break;
			case 10:
				printf("post_wait :e10 wait here\n");
				e10.receive();
				break;
			case 11:
				printf("post_wait :e11 wait here\n");
				e11.receive();
				break;
			case 12:
				printf("post_wait :e12 wait here\n");
				e12.receive();	
				break;
			case 13:
				printf("post_wait :e13 wait here\n");
				e13.receive();
				break;
			case 14:
				printf("post_wait :e14 wait here\n");
				e14.receive();
				break;
			case 15:
				printf("post_wait :e15 wait here\n");
				e15.receive();	
				break;
			case 16:
				printf("post_wait :e16 wait here\n");
				e16.receive();
				break;
			case 17:
				printf("post_wait :e17 wait here\n");
				e17.receive();
				break;
			case 18:
				printf("e18 wait here\n");
				e18.receive();
				break;
			case 19:
				printf("e19 wait here\n");
				e19.receive();
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
		ret_t = current;
		dispatch();
		return ret_t;	
	}

	void init(struct Task t){
		current = t;
		start = 0;
	}
};

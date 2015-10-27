#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <sim.sh>
#include "susan.sh"

#define QUEUE_SIZE 100
typedef int Task;

interface OSAPI
{
	//OS management
	void init();
	//void start(int sched_alg);
	//void interrupt_return(void);

	//Task management
	Task task_create(int taskID);
	void task_terminate();
	void task_activate(Task t);
	void print();

	Task par_start();	//for the threads fork
	void par_end(Task t);	//for the threads fork

	//Event handling
	Task pre_wait();
	void post_wait(Task t);

	//Delay modeling
	void time_wait(sim_time nsec);

};


channel OS implements OSAPI
{
	//Task is the self-defined DS
	Task current = -1;
	int firstIndex = 0;	//the place that is going to get next
	int endIndex = 0;	//the place that is going to put next
	int i,j;
	//Task retID = 0;
	//os_queue is the DS based on queue
	//os_queue rdyq, waitq;
	Task  rdyq[100];
	Task  waitq[100];

	event event00, event02,  event03, event04, event05; 
	event event06, event07,  event08, event09, event10; 
	event event11, event12,  event13, event14, event15; 
	event event16, event17,  event18, event19, event20; 
	event event21, event22,  event23, event24, event25; 
	event event26, event27,  event28, event29, event01; 

	void init() {

	}

	void print()
	{
		int Total_Num;
		Total_Num=(endIndex-firstIndex)%30;
		for(i=0;i<(Total_Num);i++){
			j=rdyq[(firstIndex+i)%30];
			printf("No.%d task is %d.\n",(i+1),j);
		}
		printf("end of queue.\n");
	}

	//enqueue--rdyq
	void insert(Task task)
	{
		if(endIndex==((firstIndex-1+QUEUE_SIZE)%QUEUE_SIZE)){
			printf("fifo full during insert");
		}
		rdyq[endIndex] = task;
		endIndex = (endIndex+1)%QUEUE_SIZE;
	}
	//dequeue--rdyq
	Task remove()
	{
		Task task;
		if(firstIndex == endIndex){//empty
			printf("fifo empty during remove");	
		}
		task = rdyq[firstIndex];
		//index range from 0 to 99
		firstIndex = (firstIndex+1)%QUEUE_SIZE;
		return task;
	}

	os_notify(Task task)
	{
		switch(task)
		{
			case 0: notify event00;
			case 1: notify event01;
			case 2: notify event02;
			case 3: notify event03;
			case 4: notify event04;
			case 5: notify event05;
			case 6: notify event06;
			case 7: notify event07;
			case 8: notify event08;
			case 9: notify event09;
			case 10: notify event10;
			case 11: notify event11;
			case 12: notify event12;
			case 13: notify event13;
			case 14: notify event14;
			case 15: notify event15;
			case 16: notify event16;
			case 17: notify event17;
			case 18: notify event18;
			case 19: notify event19;
			case 20: notify event20;
			case 21: notify event21;
			case 22: notify event22;
			case 23: notify event23;
			case 24: notify event24;
			case 25: notify event25;
			case 26: notify event26;
			case 27: notify event27;
			case 28: notify event28;
			case 29: notify event29;
			default:break;
		}
	}

	os_wait(Task task)
	{
		switch(task)
		{
			case 0: wait event00;
			case 1: wait event01;
			case 2: wait event02;
			case 3: wait event03;
			case 4: wait event04;
			case 5: wait event05;
			case 6: wait event06;
			case 7: wait event07;
			case 8: wait event08;
			case 9: wait event09;
			case 10: wait event10;
			case 11: wait event11;
			case 12: wait event12;
			case 13: wait event13;
			case 14: wait event14;
			case 15: wait event15;
			case 16: wait event16;
			case 17: wait event17;
			case 18: wait event18;
			case 19: wait event19;
			case 20: wait event20;
			case 21: wait event21;
			case 22: wait event22;
			case 23: wait event23;
			case 24: wait event24;
			case 25: wait event25;
			case 26: wait event26;
			case 27: wait event27;
			case 28: wait event28;
			case 29: wait event29;
			default:break;
		}
	}


	//Task management
	Task task_create(int taskID)
	{
		Task task;
 		task= taskID;	//give an index of a task
		return task;
	}


	void dispatch(void)
	{
		current = remove();//os_queue----function--pop
		os_notify(current);
	}
	
	//Task management
	void task_terminate()
	{//dispatch next in the queue
		dispatch();
	}

	//Task management
	void task_activate(Task t)
	{//add to rdyq
		insert(t);
//		print();
		//write events
		os_wait(t);
	}

	//Task management--suspends the calling task and waits for the child tasks to finish
	Task par_start()
	{//for the dynamic task forking and joining
		Task retID;
		retID = current;
		dispatch();
		return retID;
	}	
	
	//Task management--resumes the calling task's execution
	void par_end(Task retID)
	{//for the dynamic task forking and joining
		insert(retID);
		os_wait(retID);
	}	
	
	//stop a, put it into queue and execute b, 
	void yield()
	{
		Task task;
		task = current;
		insert(task);
		dispatch();
		os_wait(task);
	}

	//model the time delay
	void time_wait(sim_time t)
	{
		waitfor(t);
		yield();
	}


	//enqueue--waitq
	void w_insert(Task task)
	{
		if(endIndex==((firstIndex-1+QUEUE_SIZE)%QUEUE_SIZE)){
			printf("fifo full during insert");
		}
		waitq[endIndex] = task;
		endIndex = (endIndex+1)%QUEUE_SIZE;
	}
	//dequeue--waitq
	Task w_remove()
	{
		Task task;
		if(firstIndex == endIndex){//empty
			printf("fifo empty during remove");	
		}
		task = waitq[firstIndex];
		//index range from 0 to 99
		firstIndex = (firstIndex+1)%QUEUE_SIZE;
		return task;
	}

	//event management
	Task pre_wait()
	{
		Task task;
		task = current;
		insert(task);
		dispatch();
		return task;
	}

	//event management
	void post_wait(Task t)
	{
		os_wait(t);
	}


};





/**********************************************************************
 *  File Name:   os1.sc
 *  Author:      lingxiao.jia
 *  Mail:        lingxiao.jia@utexas.edu
 *  Create Time: 2015 Oct 17 03:46:04 PM
 **********************************************************************/

#include "susan.sh"
import "c_handshake";

#define NUMTHREADS  15

typedef struct Task {
    struct Task *next;  // linked-list pointer
    struct Task *prev;  // point to previous task
    char name[20];
    int32_t id;
} Task;

interface OSAPI
{
    int getNumCreated();
    void print();

    /* OS management */
    void init();
    void start();

    /* Task management */
    Task* task_create(char *name);
    void task_terminate();
    void task_activate(Task *t);

    Task* par_start();
    void par_end(Task *t);

    /* Event handling */
    Task* pre_wait();
    void post_wait(Task *t);

    /* Time modeling */
    void time_wait(sim_time nsec);
};

channel OS implements OSAPI {
    int NumCreated = 0;
    c_handshake e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15;
    Task tasks[NUMTHREADS];
    Task *RunPt = 0;
    Task *NextPt = 0;

    /* helper function */
    int getNumCreated() {
        return NumCreated;
    }

    void print() {
        Task *temp;
        if (RunPt == 0){ printf("Empty.\n"); return;}
        temp = RunPt->next;
        printf("WaitQ: ");
        while (temp != RunPt) {
            printf("%d %s ", temp->id, temp->name);
            temp = temp->next;
        }
        printf("RunPt: %d %s \n", temp->id, temp->name);
        printf("Next:%d Run:%d\n",NextPt->id,RunPt->id);
        printf("end of queue.\n");
    }

    void Remove(Task **listPt){
        if (*listPt == 0) return;
        if ((*listPt)->next == (*listPt)) {
            *listPt = 0;
            return;
        }
        (*listPt)->prev->next = (*listPt)->next;
        (*listPt)->next->prev = (*listPt)->prev;
        *listPt = (*listPt)->prev;
    }

    void Insert(Task *listPt, Task *currPt){
        currPt->prev = listPt->prev;
        currPt->prev->next = currPt;
        currPt->next = listPt;
        listPt->prev = currPt;
    }

    void os_wait(int id) {
        switch (id) {
            case 1: e1.receive(); break;
            case 2: e2.receive(); break;
            case 3: e3.receive(); break;
            case 4: e4.receive(); break;
            case 5: e5.receive(); break;
            case 6: e6.receive(); break;
            case 7: e7.receive(); break;
            case 8: e8.receive(); break;
            case 9: e9.receive(); break;
            case 10: e10.receive(); break;
            case 11: e11.receive(); break;
            case 12: e12.receive(); break;
            case 13: e13.receive(); break;
            case 14: e14.receive(); break;
            case 15: e15.receive(); break;
            default: break;
        }
    }

    void os_notify(int id) {
        switch (id) {
            case 1: e1.send(); break;
            case 2: e2.send(); break;
            case 3: e3.send(); break;
            case 4: e4.send(); break;
            case 5: e5.send(); break;
            case 6: e6.send(); break;
            case 7: e7.send(); break;
            case 8: e8.send(); break;
            case 9: e9.send(); break;
            case 10: e10.send(); break;
            case 11: e11.send(); break;
            case 12: e12.send(); break;
            case 13: e13.send(); break;
            case 14: e14.send(); break;
            case 15: e15.send(); break;
            default: break;
        }
    }

    void dispatch(void) {
        RunPt = RunPt->next;
        os_notify(RunPt->id);
    }

    void yield() {
        Task *currPt;
        currPt = RunPt;
        dispatch();
        os_wait(currPt->id);
    }

    /* OS management */
    void init() {

    }

    void start() {

    }

    /* Task management */
    Task* task_create(char *name) {
        int idx = 0;
        Task *currPt;
        if (NumCreated == NUMTHREADS) {     // Meet maximum number of threads
            return 0;
        }
        while (tasks[idx].id != 0) {
            idx++;
        }
        NumCreated++;
        currPt = &tasks[idx];
        currPt->id = idx+1;
        strcpy(currPt->name, name);
        if (RunPt == 0) {
            RunPt = currPt;
            RunPt->prev = RunPt->next = RunPt;
        } else {
            Insert(RunPt, currPt);
        }
        NextPt = RunPt->next;
        return currPt;
    }

    void task_terminate() {
        Task *currPt;
        currPt = RunPt;
        Remove(&RunPt);
        if (currPt) {
            currPt->id = 0;
            NumCreated--;
        }
        if (RunPt) dispatch();
    }

    void task_activate(Task *currPt) {
        os_wait(currPt->id);
    }

    Task* par_start() {
        Task *currPt;
        currPt = RunPt;
        Remove(&RunPt);
        if (currPt) {
            NumCreated--;
        }
        if (RunPt) dispatch();
        return currPt;
    }

    void par_end(Task *currPt) {
        if (RunPt == 0) {
            RunPt = currPt;
            RunPt->prev = RunPt->next = RunPt;
        } else {
            Insert(RunPt, currPt);
            os_wait(currPt->id);
        }
        NumCreated++;
    }

    /* Event handling */
    Task* pre_wait() {
        Task *currPt;
        currPt = RunPt;
        Remove(&RunPt);
        if (currPt) {
            NumCreated--;
        }
        if (RunPt) dispatch();
        return currPt;
    }

    void post_wait(Task *currPt) {
        if (RunPt == 0) {
            RunPt = currPt;
            RunPt->prev = RunPt->next = RunPt;
        } else {
            Insert(RunPt, currPt);
            os_wait(currPt->id);
        }
        NumCreated++;
    }

    /* Time modeling */
    void time_wait(sim_time t) {
        waitfor(t);
        yield();
    }
};



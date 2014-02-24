package cs455.scaling.threadpool;

import java.util.LinkedList;

import cs455.scaling.task.Task;

/**
 * A WorkerThread, when run, waits for tasks to be available on the provided
 * queue. When a task is available, it removes the task from the queue and runs
 * it.
 * 
 * @author Kira Lindburg
 * @date Feb 23, 2014
 */
public class WorkerThread extends Thread {
    private LinkedList<Task> taskQueue;

    public WorkerThread(LinkedList<Task> taskQueue) {
        this.taskQueue = taskQueue;
    }
    
    @Override
    public void run(){
        
    }
}

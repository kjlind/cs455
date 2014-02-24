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

    /**
     * Creates a new worker thread which will handle tasks from the provided
     * queue.
     */
    public WorkerThread(LinkedList<Task> taskQueue) {
        this.taskQueue = taskQueue;
    }

    /**
     * Waits for tasks to be available on the queue. When a task is available,
     * dequeues it, runs it, and returns to waiting.
     */
    @Override
    public void run() {
        while (!interrupted()) {
            Task nextTask;
            synchronized (taskQueue) {
                while (taskQueue.isEmpty()) {
                    try {
                        taskQueue.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                nextTask = taskQueue.removeFirst();
            }
            nextTask.run();
        }
    }
}

package cs455.scaling.threadpool;

import java.util.LinkedList;

import cs455.scaling.task.Task;

/**
 * ThreadpoolManager handles a thread pool with some specified number of worker
 * threads. It maintains a queue of tasks which are to be completed; a method is
 * provided to add additional tasks to this queue. Whenever a new task is added,
 * the thread pool manager notifies waiting worker threads.
 * 
 * @author Kira Lindburg
 * @date Feb 23, 2014
 */
public class ThreadpoolManager {
    private static final int DEFAULT_NUM_THREADS = 10;

    private WorkerThread[] pool;
    private LinkedList<Task> taskQueue;

    /**
     * Creates a new threadpool manager with the specified number of threads.
     * 
     * @param numThreads the number of threads which should be maintained in the
     * pool; if less than or equal to 0, a default value of 10 will be used
     * instead
     */
    public ThreadpoolManager(int numThreads) {
        taskQueue = new LinkedList<Task>();

        if (numThreads <= 0) {
            numThreads = DEFAULT_NUM_THREADS;
        }
        pool = new WorkerThread[numThreads];
        for (int i = 0; i < numThreads; ++i) {
            pool[i] = new WorkerThread(taskQueue);
        }
    }

    /**
     * Starts all threads in the thread pool, if this threadpool manager has not
     * yet been initialized. If the threadpool manager has previously been
     * initialized (via a call to this method), does nothing.
     */
    public void initialize() {
        synchronized (pool) {
            if (pool[0].getState() == Thread.State.NEW) {
                startThreads();
            }
        }
    }

    /**
     * Starts all threads in the pool.
     */
    private void startThreads() {
        for (WorkerThread nextThread : pool) {
            nextThread.start();
        }
    }

    /**
     * Adds the provided task to the waiting queue of tasks to be performed;
     * this task will be executed eventually when a worker thread is available.
     * 
     * @param task the task to add to the queue
     */
    public void addTask(Task task) {
        synchronized (taskQueue) {
            taskQueue.addLast(task);
            taskQueue.notify();
        }
    }
}

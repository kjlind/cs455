package cs455.scaling.task;

/**
 * A Task represents some piece of work which needs to be carried out. A task is
 * a runnable object and thus must include a run() method; this method should
 * encapsulate all calculations to carry out the task. A task must additionally
 * have a getType() method which uniquely identifies the type of task.
 * 
 * @author Kira Lindburg
 * @date Feb 23, 2014
 */
public interface Task extends Runnable {
    @Override
    public void run();

    public String getType();
}

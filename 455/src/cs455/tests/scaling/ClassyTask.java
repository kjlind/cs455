package cs455.tests.scaling;

import cs455.scaling.task.Task;

/**
 * Testing junk task for threadpool manager.
 *
 * @author Kira Lindburg
 * @date Feb 23, 2014
 */
public class ClassyTask implements Task{

    /**
     * Prints a string.
     */
    @Override
    public void run() {
        System.out.println("I am a very classy task.");
    }

    @Override
    public String getType() {
        return "Classy";
    }

}

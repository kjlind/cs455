package cs455.tests.scaling;

import cs455.scaling.task.Task;

/**
 * Test class for the thread pool manager; calls thread.sleep and waits for a
 * while.
 * 
 * @author Kira Lindburg
 * @date Feb 23, 2014
 */
public class TwiddleThumbsTask implements Task {

    /**
     * Sleeps for a while.
     */
    @Override
    public void run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public String getType() {
        return "Twiddle Thumbs";
    }

}

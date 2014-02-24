package cs455.tests.scaling;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import cs455.scaling.threadpool.ThreadpoolManager;

public class ThreadpoolManagerTest {
    private ThreadpoolManager mangey;
    private int numThreads = 10;

    @Before
    public void setUp() throws Exception {
        mangey = new ThreadpoolManager(numThreads);
    }

    @Test
    public void test() {
        try {
            System.out.println("Running ThreadpoolManagerTest");

            // add some tasks
            for (int i = 0; i < 20; ++i) {
                mangey.addTask(new ClassyTask());
            }

            // start the thread pool
            mangey.initialize();

            // start again just to make sure nothing is broken
            mangey.initialize();

            // add a couple more tasks
            mangey.addTask(new ClassyTask());
            mangey.addTask(new ClassyTask());
            for (int i = 0; i < 5; ++i) {
                mangey.addTask(new TwiddleThumbsTask());
            }
            mangey.addTask(new ClassyTask());

            // wait for a little while to ensure nothing exits?
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}

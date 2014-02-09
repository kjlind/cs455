package cs455.tests.overlay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import cs455.overlay.util.LinkInfo;
import cs455.overlay.util.NodeInfo;

public class LinkInfoTest {
    private LinkInfo link;

    private NodeInfo nodeA;
    private NodeInfo nodeB;
    private int linkWeight;

    @Before
    public void setUp() throws Exception {
        nodeA = new NodeInfo("host", 10);
        nodeB = new NodeInfo("veggie", 36);
        linkWeight = 10;

        link = new LinkInfo(nodeA, nodeB, linkWeight);
    }

    @Test
    public void testMarshalling() {
        // valid
        try {
            // marshal
            byte[] bytes = link.getBytes();

            // unmarshal
            LinkInfo link2 = new LinkInfo(bytes);
            assertEquals(nodeA, link2.getNodeA());
            assertEquals(nodeB, link2.getNodeB());
            assertEquals(linkWeight, link2.getLinkWeight());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Threw an exception but it ought to have been just fineQ");
        }
    }

}

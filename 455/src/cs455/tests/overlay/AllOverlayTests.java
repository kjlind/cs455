package cs455.tests.overlay;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ RegisterRequestTest.class, MessageFactoryTest.class,
        DeregisterRequestTest.class, ConnectionInformationTest.class,
        RegisterResponseTest.class, NodeInfoTest.class,
        MessagingNodesListTest.class, LinkInfoTest.class })
public class AllOverlayTests {

}

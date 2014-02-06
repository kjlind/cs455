package cs455.tests.overlay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegisterResponse;

public class RegisterResponseTest {
    private RegisterResponse reginald;

    private boolean success = false;
    private String info = "Something very very very very bad happened :D";

    @Before
    public void setUp() throws Exception {
        reginald = new RegisterResponse(success, info);
    }

    @Test
    public void testGetType() {
        assertEquals(Protocol.REGISTER_RESPONSE, reginald.getType());
    }

    @Test
    public void testMarshalling() {
        // valid
        try {
            // marshal the message
            byte[] marsha = reginald.getBytes();

            // unmarshal the message
            RegisterResponse regena = new RegisterResponse(marsha);
            assertEquals(success, regena.getSuccess());
            assertEquals(info, regena.getInfo());
            // assertEquals(regena.getAssignedID(), assignedID);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Threw an I/O exception with what should be valid marshalling");
        }
    }
}

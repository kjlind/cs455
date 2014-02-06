package cs455.overlay.tcp;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * A Sender sends data to some recipient via a Socket. The socket must be
 * provided to the sender upon creation, and it should already be connected with
 * the desired recipient.
 * 
 * @author Kira Lindburg
 * @date Jan 24, 2014
 */
public class Sender {
    private Socket socket;
    private String name;

    /**
     * Creates a new sender which will transmit data to whatever recipient with
     * which the provided socket is connected.
     */
    public Sender(Socket socket) {
        this.socket = socket;

        /*
         * set an initial (presumably unique unless something has broken) name
         * for this sender so that it may be hashed properly
         */
        String initialName = getReceiverHostAddress() + ":" + getReceiverPort();
        // System.out.println("Sender ctor, initial name: " + initialName);

        setName(initialName);
    }

    /**
     * @return the host name of the node to which this sender is connected
     */
    public String getReceiverHostAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    /**
     * @return string representation of the address to which the socket in this
     * sender is bound
     */
    public String getLocalHostAddress() {
        return socket.getLocalAddress().getHostAddress();
    }

    /**
     * @return the port number of the receiver to which this sender is connected
     */
    public int getReceiverPort() {
        return socket.getPort();
    }

    /**
     * @return the local port number to which the socket in this sender is bound
     */
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    /**
     * @return a string which may be used to identify this sender (its name)
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this sender to the provided string.
     */
    public void setName(String newName) {
        name = newName;
    }

    /**
     * Sends the contents of the given byte array to the receiver with which
     * this sender is connected.
     * 
     * @throws IOException if an I/O error occurs while attempting to send the
     * data
     */
    public void sendBytes(byte[] data) throws IOException {
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(
            socket.getOutputStream()));
        int dataSize = data.length;
        dout.writeInt(dataSize);
        dout.write(data);
        dout.flush();
    }
}

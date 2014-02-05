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

    /**
     * Creates a new sender which will transmit data to whatever recipient with
     * which the provided socket is connected.
     */
    public Sender(Socket socket) {
        this.socket = socket;
    }

    /**
     * @return the host name of the node to which this sender is connected
     */
    public String getReceiverHostName() {
        return socket.getInetAddress().getHostName();
    }
    
    /**
     * @return the port number of the receiver to which this sender is connected
     */
    public int getReceiverPort(){
        return socket.getPort();
    }

    /**
     * Sends the contents of the given byte array to the reciever with which
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

package cs455.overlay.tcp;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import cs455.overlay.nodes.Node;

/**
 * A receiver has a socket over which it listens for incoming messages. The
 * socket should already be connected before it is passed to the receiver. Once
 * run, it will listen for incoming messages at its given socket indefinitely. A
 * Receiver is also associated with a particular Node; whenever it receives a
 * message, it passes it to the node to handle.
 * 
 * @author Kira Lindburg
 * @date Jan 24, 2014
 */
public class ReceiverThread extends Thread {
    private Socket socket;
    private Node targetedNode;

    public ReceiverThread(Socket socket, Node targetedNode) {
        this.socket = socket;
        this.targetedNode = targetedNode;
    }

    /**
     * Tries to read some data from the socket. Assumes that the data size, in
     * bytes, was published first, followed by the actual data.
     * 
     * @return the data which was read
     * @throws IOException if an I/O error occurs while trying to read bytes
     * from the socket
     */
    private byte[] receiveBytes() throws IOException {
        DataInputStream din = new DataInputStream(new BufferedInputStream(
                socket.getInputStream()));

        int dataSize = din.readInt();
        byte[] data = new byte[dataSize];
        din.readFully(data);

        return data;
    }

    /**
     * @return the IPAddress of the sender to which this receiver is connected
     */
    public String getSenderIPAddress() {
        return socket.getInetAddress().getHostName();
    }

    @Override
    public void run() {
        // TODO: change to while(socket.isConnected())?
        while (true) {
            try {
                byte[] data = receiveBytes();
                targetedNode.handleMessage(data);
            } catch (IOException e) {
                System.err.println("I/O error while trying to read data: ");
                e.printStackTrace();
            }
        }
    }
}

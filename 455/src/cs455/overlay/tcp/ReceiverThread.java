package cs455.overlay.tcp;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

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
    private DataInputStream din;

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
        int dataSize = din.readInt();
        byte[] data = new byte[dataSize];
        din.readFully(data);

        return data;
    }

    /**
     * @return the HostName of the sender to which this receiver is connected
     */
    public String getSenderHostName() {
        return socket.getInetAddress().getHostName();
    }

    @Override
    public void run() {
        try {
            din = new DataInputStream(new BufferedInputStream(
                socket.getInputStream()));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // TODO: change to while(socket.isConnected())?
        while (true) {
            try {
                byte[] data = receiveBytes();
                // TODO: pass the HostName of the sender along with the
                // message? (so that registry esp. can know who the message was
                // from and react accordingly)
                targetedNode.handleMessage(data);
            } catch (SocketException e) {
                System.err.println("Socket exception while trying to read"
                    + " data:");
                e.printStackTrace();
                return;
            } catch (EOFException e) {
                System.err.println("EOF exception while trying to read data: ");
                e.printStackTrace();
                return;
            } catch (IOException e) {
                System.err.println("I/O error while trying to read data: ");
                e.printStackTrace();
            }
        }
    }
}

package cs455.overlay.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cs455.overlay.nodes.Node;

/**
 * The Server listens for incoming connections over a ServerSocket. It accepts
 * any incoming connections and sends them off to a Receiver/Sender pair to
 * handle.
 * 
 * @author Kira Lindburg
 * @date Jan 24, 2014
 */
public class ServerThread extends Thread {

    private ServerSocket serverSocket;
    private Node targetedNode;

    /**
     * Creates a new server which will listen for connections on the specified
     * port.
     * 
     * @throws IOException if an I/O error occurs while setting up the server
     * socket
     * @throws IllegalArgumentException if the port supplied is not in the valid
     * range (0 -65535)
     */
    public ServerThread(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new ReceiverThread(socket, targetedNode).run();
                targetedNode.addSender(new Sender(socket));
            } catch (IOException e) {
                System.err.println("I/O error while waiting for/trying to"
                        + " accept an incoming connection:");
                e.printStackTrace();
            }
        }
    }

}

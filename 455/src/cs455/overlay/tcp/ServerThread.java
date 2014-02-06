package cs455.overlay.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cs455.overlay.nodes.Node;

/**
 * The Server listens for incoming connections over a ServerSocket. It accepts
 * any incoming connections and sends them off to a Receiver/Sender pair to
 * handle. It has an associated 'owner' node; all senders it creates will be
 * given this node.
 * 
 * @author Kira Lindburg
 * @date Jan 24, 2014
 */
public class ServerThread extends Thread {
    private static final boolean DEBUG = false;

    private ServerSocket serverSocket;
    private Node targetedNode;

    /**
     * Creates a new server associated with the specified node which will listen
     * for connections on the specified port.
     * 
     * @throws IOException if an I/O error occurs while setting up the server
     * socket
     * @throws IllegalArgumentException if the port supplied is not in the valid
     * range (0 - 65535)
     */
    public ServerThread(int port, Node targetedNode) throws IOException {
        serverSocket = new ServerSocket(port);
        this.targetedNode = targetedNode;

        if (DEBUG) {
            System.out.println("Server: Setting up a server");
        }
    }

    /**
     * @return the port on which this server thread is currently listening for
     * connections
     */
    public int getPort() {
        return serverSocket.getLocalPort();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (DEBUG) {
                    System.out.println("Server: waiting for connections");
                }

                Socket socket = serverSocket.accept();
                new ReceiverThread(socket, targetedNode).start();
                targetedNode.addSender(new Sender(socket));

                if (DEBUG) {
                    System.out.println("Server: Successfully accepted incoming"
                        + " connection");
                }
            } catch (IOException e) {
                System.err.println("I/O error while waiting for/trying to"
                    + " accept an incoming connection:");
                e.printStackTrace();
            }
        }
    }
}

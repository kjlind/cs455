package cs455.overlay.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
                // TODO: handle connections
            } catch (IOException e) {
                System.err.println("I/O error while waiting for/trying to"
                        + " accept an incoming connection:");
                e.printStackTrace();
            }
        }
    }

}

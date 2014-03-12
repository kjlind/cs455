package cs455.scaling.client;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Client connects to a Server at some port and hostname. Upon successful
 * connection, it begins sending payloads to the server at specified intervals.
 * Each payload contains a random 8 KB. The client tracks the payloads sent via
 * a list of the SHA-1 hashes for each array. It listens for replies from the
 * server consisting of hashes; when it receives a hash currently stored in the
 * list, it removes the hash from the list.
 * 
 * The server hostname, server port, and interval for sending packets must be
 * specified as command line arguments.
 * 
 * @author Kira Lindburg
 * @date Feb 23, 2014
 */
public class Client {
    private static final int PACKET_SIZE = 8192;

    private SocketChannel channel;
    private int messageRate;

    private ResponseListener listener;

    /**
     * Creates a default client which is not yet connected to a server. When
     * started, the client will send packets to a server at the specified rate.
     * 
     * @param messageRate desired packet sending rate, in packets/s
     * @throws IOException if an IO error occurs when opening the socket channel
     */
    public Client(int messageRate) throws IOException {
        channel = SocketChannel.open();
        listener = new ResponseListener(channel);
        this.messageRate = messageRate;
    }

    /**
     * Attempts to connect to a host at the specified IP address and port
     * number. Additionally, starts the response listener. This method should be
     * called before attempting to start the client.
     * 
     * @param hostIP the IP address of the server
     * @param hostPort the port number of the server
     * @throws IOException if the connection attempt fails due to an IO error
     */
    public void connect(String hostIP, int hostPort) throws IOException {
        System.out.println("Connecting to server.");
        channel.connect(new InetSocketAddress(hostIP, hostPort));
        new Thread(listener).start();
    }

    /**
     * Causes the client to start sending messages to the server. The client
     * will continue sending messages to the server at the rate specified upon
     * its creation until the thread is interrupted. This method should be
     * called only after a successful call to connect().
     */
    public void start() {
        System.out.println("Sending messages to server at a rate of "
            + messageRate + " per second.");
        while (!Thread.interrupted()) {
            try {
                sendMessage();
            } catch (IOException e) {
                // assume server disconnect and give up
                return;
            }

            try {
                Thread.sleep(1000 / messageRate);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /**
     * Sends a message to the server via its socket channel. The message will
     * consist of 8KB of random data. Additionally, the SHA-1 checksum for the
     * data will be computed and stored in this client's pending hashes list.
     * 
     * @throws IOException if an IO error occurs when sending the data
     */
    private void sendMessage() throws IOException {
        // generate data
        byte[] data = new byte[PACKET_SIZE];
        ThreadLocalRandom.current().nextBytes(data);

        // compute and store hash sum
        MessageDigest dig = null;
        try {
            dig = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
        }
        byte[] hashbrowns = dig.digest(data);
        listener.addHash(hashbrowns);

        // put data into buffer for sending
        ByteBuffer buff = ByteBuffer.allocate(PACKET_SIZE);
        buff.put(data);
        buff.flip();

        // write the data
        while (buff.hasRemaining()) {
            channel.write(buff);
        }

        // print a message
        BigInteger hashInt = new BigInteger(1, hashbrowns);
        String hashStr = hashInt.toString(16);
        System.out.println("Sent " + hashStr);
    }

    public static void main(String[] args) {
        // handle command line arguments
        if (args.length != 3) {
            usage();
        }

        String hostIP = args[0];
        int hostPort = 0;
        try {
            hostPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("hostPort must be an int; " + args[1]
                + " cannot be parsed to an int");
            System.exit(1);
        }
        int messageRate = 0;
        try {
            messageRate = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("messageRate must be an int; " + args[2]
                + " cannot be parsed to an int");
            System.exit(1);
        }

        // create and initialize client
        Client client = null;
        try {
            client = new Client(messageRate);
            client.connect(hostIP, hostPort);
        } catch (IOException e) {
            System.err.println("IO error when trying to initialize Client: ");
            e.printStackTrace();
            System.err.println("Exiting.");
            System.exit(1);
        }

        // send messages
        client.start();
    }

    /**
     * Prints a usage message and exits.
     */
    private static void usage() {
        System.err.println("Usage: java cs455.scaling.client.Client serverIP"
            + " serverPort messageRate");
        System.err.println("       java cs455.scaling.client.Client carrot 5000 5");
        System.exit(1);
    }
}

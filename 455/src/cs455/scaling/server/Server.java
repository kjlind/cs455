package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

import cs455.scaling.task.ReadTask;
import cs455.scaling.task.WriteTask;
import cs455.scaling.threadpool.ThreadpoolManager;

/**
 * Server manages connections with a number of Clients at once. It uses java NIO
 * to accept incoming connections, receive incoming payloads from the clients,
 * and send replies consisting of the SHA-1 hash of these payloads. It utilizes
 * a thread pool via a ThreadPoolManager in order to accomplish all of these
 * tasks.
 * 
 * The port number on which to listen for connections and the number of threads
 * in the pool must be specified as command line arguments.
 * 
 * @author Kira Lindburg
 * @date Feb 23, 2014
 */
public class Server {
    private static final int PACKET_SIZE = 8192;

    private int port;
    Selector selector;
    ThreadpoolManager threadpool;

    /**
     * Creates a new server which will listen for incoming connections with
     * clients on the specified port number.
     */
    public Server(int port, int numThreads) {
        this.port = port;
        threadpool = new ThreadpoolManager(numThreads);
    }

    /**
     * Sets up this server's selector, opens a server socket channel, and
     * registers the channel with the selector so that it is ready to begin
     * listening for connections from clients. This method should be called
     * before starting the server.
     * 
     * @throws IOException
     */
    public void initialize() throws IOException {
        // set up selector
        selector = Selector.open();

        // set up server socket channel
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        // set up threadpool
        threadpool.initialize();
    }

    /**
     * Causes the server to start listening for incoming connections and
     * messages from clients.
     */
    public void start() {
        while (!Thread.interrupted()) {
            // see if anything is ready
            int selectedKeys = 0;
            try {
                selectedKeys = selector.select();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // if nothing was selected, go back and wait again
            if (selectedKeys == 0) {
                continue;
            }

            // handle ready events
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey nextKey = keys.next();
                keys.remove();

                // make sure we're good to go with a valid key
                if (!nextKey.isValid()) {
                    continue;
                }

                // handle the key
                if (nextKey.isAcceptable()) {
                    try {
                        accept(nextKey);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (nextKey.isReadable()) {
                    read(nextKey);
                }

                if (nextKey.isWritable()) {
                    write(nextKey);
                }
            }
        }
    }

    /**
     * Accepts an incoming connection on the provided key's channel. Sets up a
     * channel for the new connection, registers it, and adds a new pending
     * write list for this channel to the hash table of channels.
     * 
     * @param key the key which is ready for accepting a connection
     * @throws IOException if an IO error occurs when trying to accept the
     * connection
     */
    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ,
            new ArrayList<byte[]>());
        System.out.println("Accepted connection from "
            + channel.getRemoteAddress());
    }

    /**
     * Reads in some data from the channel associated with this key. Reads 8KB
     * of data if no errors occur, calculates the checksum, and adds the
     * checksum to the list attached to the key (the list of pending data to
     * write to this channel).
     * 
     * @param key the key which is ready for reading some data
     */
    private void read(SelectionKey key) {
        threadpool.addTask(new ReadTask(key, PACKET_SIZE));
    }

    /**
     * Writes any pending data in the key's attached list to the associated
     * channel.
     * 
     * @param key the key which is ready for writing some data
     */
    private void write(SelectionKey key) {
        threadpool.addTask(new WriteTask(key));
    }

    public static void main(String[] args) {
        // handle command line arguments
        if (args.length != 2) {
            usage();
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("port must be an int; " + args[0]
                + " cannot be parsed to an int");
            System.exit(1);
        }

        int numThreads = 0;
        try {
            numThreads = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("port must be an int; " + args[1]
                + " cannot be parsed to an int");
            System.exit(1);
        }

        // set up server
        Server server = new Server(port, numThreads);
        try {
            server.initialize();
        } catch (IOException e) {
            System.err.println("IO error initializing server: ");
            e.printStackTrace();
            System.err.println("Exiting.");
            System.exit(1);
        }

        // handle communications with clients
        server.start();
    }

    /**
     * Prints a usage message and exits.
     */
    private static void usage() {
        System.err.println("Usage: cs455.scaling.server.Server port"
            + " threadPoolSize");
        System.err.println("       cs455.scaling.server.Server 5000 10");
        System.exit(1);
    }
}

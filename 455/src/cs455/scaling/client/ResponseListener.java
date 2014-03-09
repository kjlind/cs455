package cs455.scaling.client;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * ReponseListener listens for responses in the form of sha1 hash sums from the
 * server. When it receives a hash, it removes it from the pending hashes list.
 * 
 * @author Kira Lindburg
 * @date Mar 9, 2014
 */
public class ResponseListener implements Runnable {
    private static final int CHECKSUM_SIZE = 20;

    private SocketChannel channel;
    private LinkedList<byte[]> pendingHashes;

    public ResponseListener(SocketChannel channel) {
        this.channel = channel;
        pendingHashes = new LinkedList<byte[]>();
    }

    /**
     * Adds the provided hash to the pending hashes list for this reponse
     * listener.
     */
    public void addHash(byte[] hash) {
        synchronized (pendingHashes) {
            BigInteger hashInt = new BigInteger(1, hash);
            String hashStr = hashInt.toString(16);
            System.out.println("added " + hashStr);
            pendingHashes.add(hash);
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            // read the data
            ByteBuffer buff = ByteBuffer.allocate(CHECKSUM_SIZE);
            int read = 0;
            while (buff.hasRemaining() && read != -1) {
                try {
                    read = channel.read(buff);
                } catch (IOException e) {
                    System.err.println("IO error while reading");
                    e.printStackTrace();
                }
            }
            // TODO: handle disconnects and IO errors
            buff.flip();
            byte[] data = new byte[CHECKSUM_SIZE];
            buff.get(data);
            BigInteger hashInt = new BigInteger(1, data);
            String hashStr = hashInt.toString(16);
            System.out.println("Got " + hashStr);

            // find the hash in the pending hashes and remove it
            synchronized (pendingHashes) {
                Iterator<byte[]> iter = pendingHashes.iterator();
                byte[] nextHash = null;
                while (!Arrays.equals(data, nextHash) && iter.hasNext()) {
                    nextHash = iter.next();
                }
                if (!Arrays.equals(data, nextHash)) {
                    System.out.println("Got a hash which wasn't in the list!");
                } else {
                    System.out.println("-------before----------");
                    for (byte[] hahsh : pendingHashes) {
                        String hag = new BigInteger(1, hahsh).toString(16);
                        System.out.println(hag);
                    }
                    System.out.println("-----------------");

                    System.out.println("Received and removed " + hashStr);
                    pendingHashes.remove(nextHash);

                    System.out.println("-------after----------");
                    for (byte[] hahsh : pendingHashes) {
                        String hag = new BigInteger(1, hahsh).toString(16);
                        System.out.println(hag);
                    }
                    System.out.println("-----------------");

                }
            }
        }
    }
}

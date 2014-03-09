package cs455.scaling.client;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
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
    private LinkedList<BigInteger> pendingHashes;

    public ResponseListener(SocketChannel channel) {
        this.channel = channel;
        pendingHashes = new LinkedList<BigInteger>();
    }

    /**
     * Adds the provided hash to the pending hashes list for this reponse
     * listener.
     */
    public void addHash(byte[] hash) {
        BigInteger hashInt = new BigInteger(1, hash);
        synchronized (pendingHashes) {
            pendingHashes.add(hashInt);
        }
        String hashStr = hashInt.toString(16);
        System.out.println("added " + hashStr);
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
            boolean yay;
            synchronized (pendingHashes) {
                yay = pendingHashes.remove(hashInt);
            }
            if (yay) {
                System.out.println("Found and removed " + hashStr);
            } else {
                System.out.println("Hash not found in list!");
            }
        }
    }
}

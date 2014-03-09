package cs455.scaling.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * A ReadTask reads the specified number of bytes from the provided key,
 * calculates the checksum, adds the checksum to the pending write list attached
 * to the key, and sets the selection op to write to prepare the key for writing
 * out the data.
 * 
 * @author Kira Lindburg
 * @date Mar 9, 2014
 */
public class ReadTask implements Task {
    private SelectionKey key;
    private int packetSize;

    public ReadTask(SelectionKey key, int packetSize) {
        this.key = key;
        this.packetSize = packetSize;
    }

    @Override
    public void run() {
        // read the data
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buff = ByteBuffer.allocate(packetSize);
        int read = 0;
        while (buff.hasRemaining() && read != -1) {
            try {
                read = channel.read(buff);
            } catch (IOException e) {
                System.err.println("IO error while reading");
                e.printStackTrace();
                return;
            }
        }
        // TODO: handle disconnects and IO errors
        buff.flip();
        byte[] data = new byte[packetSize];
        buff.get(data);
        try {
            System.out.println("Received data " + data + " from "
                + channel.getRemoteAddress());
        } catch (IOException e1) {
        }

        // calculate and store checksum
        MessageDigest dig = null;
        try {
            dig = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
        }
        byte[] hashbrowns = dig.digest(data);
        @SuppressWarnings("unchecked")
        List<byte[]> pendingWrites = (List<byte[]>) key.attachment();
        synchronized (pendingWrites) {
            pendingWrites.add(hashbrowns);
        }

        key.interestOps(SelectionKey.OP_WRITE);
    }

    @Override
    public String getType() {
        return "read task";
    }
}

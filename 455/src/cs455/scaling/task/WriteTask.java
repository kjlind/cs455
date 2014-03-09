package cs455.scaling.task;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

public class WriteTask implements Task {
    private SelectionKey key;

    public WriteTask(SelectionKey key) {
        this.key = key;
    }

    @Override
    public void run() {
        SocketChannel channel = (SocketChannel) key.channel();
        @SuppressWarnings("unchecked")
        List<byte[]> pendingWrites = (List<byte[]>) key.attachment();
        synchronized (pendingWrites) {
            for (byte[] nextData : pendingWrites) {
                ByteBuffer buff = ByteBuffer.wrap(nextData);
                try {
                    channel.write(buff);

                    BigInteger hashInt = new BigInteger(1, nextData);
                    String hashStr = hashInt.toString(16);
                    System.out.println("Wrote hash " + hashStr + " to "
                        + channel.getRemoteAddress());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            pendingWrites.clear();
        }
        key.interestOps(SelectionKey.OP_READ);
    }

    @Override
    public String getType() {
        return "write task";
    }
}

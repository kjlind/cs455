package cs455.scaling.task;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
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
        byte[] data;
        synchronized (pendingWrites) {
            Iterator<byte[]> iter = pendingWrites.iterator();
            if (!iter.hasNext()) {
                // this is so very much a hack
                // this is here because there are a million useless tasks being
                // created and I need to solve that issue
                return;
            }

            data = iter.next();
            iter.remove();
        }
        ByteBuffer buff = ByteBuffer.wrap(data);
        try {
            channel.write(buff);

            BigInteger hashInt = new BigInteger(1, data);
            String hashStr = hashInt.toString(16);
            // System.out.println("Wrote hash " + hashStr + " to "
            // + channel.getRemoteAddress());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // synchronized (pendingWrites) {
        // for (byte[] nextData : pendingWrites) {
        // ByteBuffer buff = ByteBuffer.wrap(nextData);
        // try {
        // channel.write(buff);
        //
        // BigInteger hashInt = new BigInteger(1, nextData);
        // String hashStr = hashInt.toString(16);
        // System.out.println("Wrote hash " + hashStr + " to "
        // + channel.getRemoteAddress());
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }
        // pendingWrites.clear();
        // }
        key.interestOps(SelectionKey.OP_READ);
    }

    @Override
    public String getType() {
        return "write task";
    }
}

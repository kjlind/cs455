package cs455.scaling.task;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

import cs455.scaling.server.ChannelStatus;

public class WriteTask implements Task {
    private SelectionKey key;

    public WriteTask(SelectionKey key) {
        this.key = key;
        ChannelStatus status = (ChannelStatus) key.attachment();
        status.setWriting(true);
    }

    @Override
    public void run() {
        ChannelStatus status = (ChannelStatus) key.attachment();
        List<byte[]> pendingWrites = status.getAndClearPendingWrites();

        SocketChannel channel = (SocketChannel) key.channel();
        for (byte[] nextData : pendingWrites) {
            ByteBuffer buff = ByteBuffer.wrap(nextData);
            try {
                channel.write(buff);

                // print a very exciting message
                BigInteger hashInt = new BigInteger(1, nextData);
                String hashStr = hashInt.toString(16);
                String clientName = channel.socket().getInetAddress()
                    .getHostName();
                int clientPort = channel.socket().getPort();
                System.out.println("Sent " + hashStr + " to " + clientName
                    + ":" + clientPort);
            } catch (IOException e) {
            }
        }

        // @SuppressWarnings("unchecked")
        // List<byte[]> pendingWrites = (List<byte[]>) key.attachment();
        // byte[] data;
        // synchronized (pendingWrites) {
        // Iterator<byte[]> iter = pendingWrites.iterator();
        // if (!iter.hasNext()) {
        // // this is so very much a hack
        // // this is here because there are a million useless tasks being
        // // created and I need to solve that issue
        // return;
        // }
        //
        // data = iter.next();
        // iter.remove();
        // }
        // ByteBuffer buff = ByteBuffer.wrap(data);
        // try {
        // channel.write(buff);
        //
        // BigInteger hashInt = new BigInteger(1, data);
        // String hashStr = hashInt.toString(16);
        // // System.out.println("Wrote hash " + hashStr + " to "
        // // + channel.getRemoteAddress());
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

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
        status.setWriting(false);
    }

    @Override
    public String getType() {
        return "write task";
    }
}

package cs455.scaling.server;

import java.util.ArrayList;
import java.util.List;

/**
 * ChannelStatus stores information about the current status of a channel,
 * including the list of information which is pending being written, whether a
 * thread is currently (or pending) reading from it, and whether a thread is
 * currently (or pending) writing to it. This class is completely thread safe
 * (er, presumably).
 * 
 * @author Kira Lindburg
 * @date Mar 10, 2014
 */
public class ChannelStatus {
    private List<byte[]> pendingWrites;
    private volatile boolean reading;
    private volatile boolean writing;

    /**
     * Creates a new ChannelStatus with empty pending writes list, reading set
     * to false, and writing set to false.
     */
    public ChannelStatus() {
        pendingWrites = new ArrayList<byte[]>();
        reading = false;
        writing = false;
    }

    public boolean reading() {
        return reading;
    }

    public void setReading(boolean reading) {
        this.reading = reading;
    }

    public boolean writing() {
        return writing;
    }

    public void setWriting(boolean writing) {
        this.writing = writing;
    }

    /**
     * Adds the given array to the pending writes list.
     * 
     * @param pending an array of bytes which should be written to the
     * associated channel eventually
     */
    public void addPendingWrite(byte[] pending) {
        synchronized (pendingWrites) {
            pendingWrites.add(pending);
        }
    }

    /**
     * Makes a copy of the current pending writes and clears the list; the
     * contents of the copy should be written out to the associated channel.
     * 
     * @return a copy of the current pending writes
     */
    public List<byte[]> getAndClearPendingWrites() {
        List<byte[]> copy = new ArrayList<byte[]>();
        synchronized (pendingWrites) {
            for (byte[] nextData : pendingWrites) {
                copy.add(nextData);
            }
            pendingWrites.clear();
        }
        return copy;
    }
}

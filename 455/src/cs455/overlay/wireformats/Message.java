package cs455.overlay.wireformats;

import java.io.IOException;

/**
 * A Message represents some sort of communication between two nodes. Messages
 * may have differing fields and need to be acted upon in varying ways; the
 * types of messages are defined in the static class Protocol. Different types
 * of messages may store their fields in any way they wish; however, they must
 * provide both an integer identifier which indicates the type of message, and a
 * method to convert their fields to a byte array which may easily be sent.
 * 
 * @author Kira Lindburg
 * @date Jan 24, 2014
 */
public interface Message {
    /**
     * Every type of Message must have a unique integer which identifies its
     * type; nodes should use this type identifier to decide how to respond to
     * the message (in particular for casting it to the right type for field
     * retrieval).
     * 
     * @return a unique integer identifying this message's type (should be one
     * of the values defined in the static Protocol class)
     */
    public int getType();

    /**
     * Marshals the fields of the message and stores them in a single byte array
     * so that they are ready to be sent via an output stream. Messages should
     * also provide a method (preferably a constructor) which takes a byte array
     * and ummarshals the bytes.
     * 
     * @return a byte array containing the fields of the message
     * @throws IOException if an I/O error occurs while attempting to marshal
     * the bytes
     */
    public byte[] getBytes() throws IOException;
}

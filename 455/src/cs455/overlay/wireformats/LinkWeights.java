package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cs455.overlay.util.LinkInfo;

/**
 * A LinkWeights message is sent from the Registry to all registered nodes. It
 * contains a LinkInfo object for each link currently found in the overlay.
 * 
 * @author Kira Lindburg
 * @date Feb 9, 2014
 */
public class LinkWeights implements Message {
    private static final int TYPE = Protocol.LINK_WEIGHTS;

    private LinkInfo[] links;

    /**
     * Creates a new LinkWeights message with the specified array of link info
     * objects.
     */
    public LinkWeights(LinkInfo[] links) {
        this.links = links;
    }

    /**
     * Creates a new LinkWeights object by demarshalling the provided bytes into
     * the correct fields.
     * 
     * @throws IOException if an I/O error occurs
     */
    public LinkWeights(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(bais));

        // type field - discard
        din.readInt();

        // number of links field
        int numberOfLinks = din.readInt();

        // list of messaging nodes field(s)
        links = new LinkInfo[numberOfLinks];
        for (int i = 0; i < numberOfLinks; ++i) {
            int linkLength = din.readInt();
            byte[] linkBytes = new byte[linkLength];
            din.readFully(linkBytes);
            links[i] = new LinkInfo(linkBytes);
        }
    }

    public LinkInfo[] getLinks() {
        return links;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(
            baos));

        // type field
        dout.writeInt(TYPE);

        // number of links field
        dout.writeInt(links.length);

        // list of links field(s)
        for (LinkInfo link : links) {
            byte[] linkBytes = link.getBytes();
            int linkLength = linkBytes.length;
            dout.writeInt(linkLength);
            dout.write(linkBytes);
        }

        dout.flush();
        marshalledBytes = baos.toByteArray();

        baos.close();
        dout.close();

        return marshalledBytes;
    }

    @Override
    public String toString() {
        String string = "Link Weights";
        for (LinkInfo link : links) {
            string += "\n" + link;
        }
        return string;
    }
}

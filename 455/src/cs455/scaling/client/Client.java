package cs455.scaling.client;

/**
 * Client connects to a Server at some port and hostname. Upon successful
 * connection, it begins sending payloads to the server at specified intervals.
 * Each payload contains a random 8 KB. The client tracks the payloads sent via
 * a list of the sha-1 hashes for each array. It listens for replies from the
 * server consisting of hashes; when it receives a hash currently stored in the
 * list, it removes the hash from the list.
 * 
 * The server hostname, server port, and interval for sending packets must be
 * specified as command line arguments.
 * 
 * @author Kira Lindburg
 * @date Feb 23, 2014
 */
public class Client {

}

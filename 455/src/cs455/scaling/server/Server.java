package cs455.scaling.server;

/**
 * Server manages connections with a number of Clients at once. It uses java NIO
 * to accept incoming connections, receive incoming payloads from the clients,
 * and send replies consisting of the SHA-1 hash of these payloads. It utilizes
 * a thread pool via a ThreadPoolManager in order to accomplish all of these
 * tasks.
 * 
 * The portnumber on which to listen for connections and the number of threads
 * in the pool must be specified as command line arguments.
 * 
 * @author Kira Lindburg
 * @date Feb 23, 2014
 */
public class Server {

}

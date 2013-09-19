
import java.io.*;
import java.net.*;
import java.util.*;
 
public class UDPClient {
	
	// Class Variables
	private static DatagramSocket socket; // UDP socket used by this client
	private static InetAddress ip;
	private static DatagramPacket sendMessage;
    private static DatagramPacket recieveMessage;
    private static Date date;
    private static int port;
	
    // Main Function of Class
	public static void main(String[] args) throws Exception {
		// Local Variables...
		String output="nil";
		boolean recieved = true;
		boolean sending = true;
		long time = 0;
		long count = 1;

		// 1. Initialise our UDP Socket
		initClient(args);

		// 2. Loop sending and recieveing messages
		while (sending) {
			while (count % 10 != 0) {
				// 1. Get Time
				time = System.currentTimeMillis();

				// 2. Update our message -
				if (recieved) {
					output = "PING " + count++ + date.toString() + "\r\n";
					sendMessage = new DatagramPacket(output.getBytes(),
							output.length(), ip, port);
				}

				// 2. Send the output via our socket
				socket.send(sendMessage);

				// 3. Wait for response
				try {
					System.out.println("ping to" + ip.toString()+ " Message: "+ output);
					// a. Wait for specified period of time until message is
					// recieved
					socket.receive(recieveMessage);
					time = System.currentTimeMillis() - time;
					System.out.println("rtt : " + time + "ms"); // print our rtt
					Thread.currentThread().sleep(1000 - time); // sleep for the
																// required
																// time....
					recieved = true;
				} catch (java.net.SocketTimeoutException e) {
					// We have a timeout - simply r
					recieved = false;
				}

				if (recieved) {
					printData(recieveMessage);
					System.out.println();
				} else {
					System.out.println("Packet Lost");
				}
			}
			// Check to continue sending
			System.out.print("Continue Sending (yes or no):");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			count++;
			if (in.readLine().equals("no")) {
				sending = false;
			}
		}
		System.out.println("GoodBye!");
	}
    /*
     * Function: Initialise our main variable sna dthe client socket
     */
    private static void initClient(String args[]) throws SocketException, UnknownHostException{
    	recieveMessage= new DatagramPacket(new byte[1024],1024);
        date = new Date();
        port = Integer.parseInt(args[0]);
        socket = new DatagramSocket(port+1);
        socket.setSoTimeout(1000);
        ip=InetAddress.getByName("127.0.0.1");
    }
    
    /*
     * Function : prints the Datagram packet nicely...
     */
    private static void printData(DatagramPacket request) throws Exception
       {
          // Obtain references to the packet's array of bytes.
          byte[] buf = request.getData();
 
          // Wrap the bytes in a byte array input stream,
          // so that you can read the data as a stream of bytes.
          ByteArrayInputStream bais = new ByteArrayInputStream(buf);
 
          // Wrap the byte array output stream in an input stream reader,
          // so you can read the data as a stream of characters.
          InputStreamReader isr = new InputStreamReader(bais);
 
          // Wrap the input stream reader in a bufferred reader,
          // so you can read the character data a line at a time.
          // (A line is a sequence of chars terminated by any combination of \r and \n.)
          BufferedReader br = new BufferedReader(isr);
 
          // The message data is contained in a single line, so read this line.
          String line = br.readLine();
 
          // Print host address and data received from it.
          System.out.println(
             "Received from " +
             request.getAddress().getHostAddress() +
             ": " +
             new String(line) );
       }
}

// Old Code
//ArrayList<DatagramPacket> buffer = new ArrayList<DatagramPacket>(10);
//for (Integer i = 0; i < 10; i++) {
//    output = "PING " + i + " " + date.toString() + "\r\n";
////    buffer.add(new DatagramPacket(output.getBytes(), output.length(),ip,port));
////}
//
////ListIterator<DatagramPacket> bufferIt = buffer.listIterator();
////while (bufferIt.hasNext())
//
//try {
//    System.out.println("ping to" + ip.toString() + ", seq = " + bufferIt.nextIndex() + ", rtt ="+time);
//    socket.receive(recieveMessage);
//    time=System.currentTimeMillis()-time;
//    Thread.currentThread().sleep(1000-time); // sleep for the required time....
//    recieved=true;
//} catch (java.net.SocketTimeoutException e) {
//    // We have a timeout - simply reset the iterator
//    recieved=false;
//    if (bufferIt.hasPrevious()) {
//        bufferIt.previous();
//    } else {
//        bufferIt = buffer.listIterator();
//    }
//}
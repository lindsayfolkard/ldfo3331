import java.io.*;
import java.net.*;
import java.util.*;

public class reliableUDTClient {

	// Class Variables
	private DatagramSocket socket; // UDP socket used by this client
	private DatagramPacket sendMessage;
    private DatagramPacket recieveMessage;
    private Date date;
    private int port;
    private long minRtt=0;
    private long maxRtt=0;
    private long avRtt=0;
    private Timer timer; // Timer used to do precise message timing
    private boolean standardMode=true;// true - just use simple delay, false-send message every second
	private byte id=0;
	private Random rand;
    
    // Constructor
    reliableUDTClient(int port) throws SocketException, UnknownHostException{
    	this.port=port;
    	initClient();
    }
    
    //Function: send a single message using UDP to destIp, destPort
    // Blocks until message is recieved or time out...
	public boolean sendMessage(String message, InetAddress destIP, int destPort)
			throws Exception {
		// Local Variables...
		String output = "nil";
		boolean recieved = false;
		boolean sending = true;
		long time = 0;
		long count = 0;

		// 1. Append Message ID
		id = (byte) rand.nextInt(255);
		message="  " + message;// Create some space at start message for id...
		byte[] send =message.getBytes();
		send[0]=id; // append id...

		// 2. Create the message
		sendMessage = new DatagramPacket(send, send.length,
				destIP, destPort);

		// 2. Send the output via our socket
		while (count < 20 && !recieved) {
			time = System.currentTimeMillis();
			socket.send(sendMessage);

			// 3. Try fro 0.5 second
			// ExecutorService service =
			for (int i = 0; i < 10 && !recieved; i++) {
				try {
					// a. Wait for 50 ms otherwise throw exception....
					socket.receive(recieveMessage);
					time = System.currentTimeMillis() - time;
					updateRtt(time);
					printData(recieveMessage);
					recieved = checkMessage(recieveMessage);
					if (recieved) {
						System.out.println("Message Sent Successfully rtt: "
								+ time + "avRtt : " + avRtt + "maxRtt: "
								+ maxRtt + "minRtt: " + minRtt); // print our
																	// rtt
						printData(recieveMessage);
					}
				} catch (java.net.SocketTimeoutException e) {
					// We have a timeout - simply r
					recieved = false;
				}
			}
		}
		socket.close();
		if (count == 20) { // we were unable to send the message effectively
			System.out.println("Packet Lost");
			return false;
		}
		return true;
	}

	/*
	 * Function: Initialise our main variable sna dthe client socket
	 */
    private void initClient() throws SocketException, UnknownHostException{
    	recieveMessage= new DatagramPacket(new byte[1024],1024);
    	rand= new Random();
        date = new Date();
        socket = new DatagramSocket(port+2);
        socket.setSoTimeout(50);
        timer= new Timer();
        //enablePeriodic();// comment out this function to just use std method
    }
    /*
     * Function to update our rtt
     */
    private void updateRtt(long rtt){
    	if (avRtt==0){
    		avRtt=rtt;minRtt=rtt;maxRtt=rtt;
    	}
    	else {
    		avRtt=(long)(0.8 * rtt + 0.2*avRtt);
    		if (rtt > maxRtt){
    			maxRtt=rtt;
    		}
    		else if (rtt < minRtt){
    			minRtt=rtt;
    		}
    	}
    }
    
    private boolean checkMessage(DatagramPacket response){
    	byte[] buf = response.getData();
    	System.out.println(buf[0]);
    	if (buf.length > 1 && buf[0]==id){
    		// we have recieved the message back...
			return true;
    	}
    	return false;
    }
    /*
     * Function: enable periodic sending of message every precise interval...
     */
    private void enablePeriodic(){
    	standardMode=false; // reset our standard mode ....
    	int period=1000;
    	timer.scheduleAtFixedRate(new TimerTask() {
    		@Override
    			public void run() {
    				try {
						socket.send(sendMessage);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("Unable to send message");
						e.printStackTrace();
					}
    			}}
    			, 3000, period);
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

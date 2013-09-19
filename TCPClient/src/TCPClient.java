
// Java TCP Client Example
import java.io.*;
import java.net.*;

class TCPClient {
	
	//Variables:
	static BufferedReader inFromUser;
	static Socket clientSocket;
	static DataOutputStream outToServer;
	static BufferedReader inFromServer;
	
	
	// Main Function : Perform the TCP init,
	public static void	main(String argv[]) throws Exception{
		String sentence="nil";
		String modifiedSentence;
		
		if (argv.length <2){
			System.out.println("usage ./this ipAddress portNumber");
			return;
		}
		// 1. Initialise our TCP Connection
		initTCP("localhost",2456);

		////////////////////////////////////////////////////////////////////////////
		//////////////////// Main Working Loop ////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////
		while (!sentence.equals("quit")) {
			// 1. Read in line from Local Terminal
			System.out.print("Message (enter):");
			sentence = inFromUser.readLine();

			// 2. Write Sentence to TCP socket
			outToServer.writeBytes(sentence + '\n');

			// 3. Get Response from TCP Socket
			modifiedSentence = inFromServer.readLine();

			// 4. Print Response and close Socket
			System.out.println("FROM SERVER: " + modifiedSentence);
			clientSocket.close();
		}
		clientSocket.close();
	}
	
	/*
	 * Function: initialise a TCP Connection
	 */
	private static boolean initTCP(String host, int port) throws UnknownHostException, IOException {
		
		// a. Input Stream: create input stream to read bytes
		inFromUser = new BufferedReader(new InputStreamReader(System.in));

		// b. TCP Socket: create a TCP Socket instance
		clientSocket = new Socket(host, port);
		
		// c. Output Stream: create output stream object to attach to socket
		outToServer = new DataOutputStream(clientSocket.getOutputStream());

		// d. Socket Input Stream: create an input stream into the TCP socket
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		return false;
	}
}

import java.io.*;
import java.net.*;


public class TCPServer {

	// Variables:
	static ServerSocket welcomeSocket;// Socket
	static InetAddress mine;
	static int port;
	
	 public static void main(String argv []) throws Exception{ 
		 welcomeSocket=new ServerSocket(Integer.parseInt(argv[0])); // Initialise our welcome socket
		 System.out.println("Server Socket On...");
		 // Main Operation Loop
		 while (true) {
			 // 1. New Connection: Create a new TCP Socket to handle the connection
			 Socket connectionSocket = welcomeSocket.accept(); // Wait for incoming connection !!!
			 
			 // 2. Input Stream: Create an input stream attached to the socket
			 BufferedReader inFromClient= new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			 
			 //3.  Output Stream : create an output stream and attach to socket
			 DataOutputStream outToClient = new DataOutputStream (connectionSocket.getOutputStream());
		 
			 //4. Read , process and send : read from Socket and process the input and send response
			 processMessage(inFromClient,outToClient);
		 }
	 }
	 
	 private static boolean processMessage( BufferedReader inFromClient, DataOutputStream outToClient){
		 String clientSentence= "Null"; // default value in case we are unable to read anything
		 String modifiedSentence;
		 
		 //1. Read a line of input from the socket
		 try {
			clientSentence=inFromClient.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 //2. Process the line of input
		 modifiedSentence=clientSentence.toUpperCase() + '\n';
		 System.out.println("Message Recieved: " + clientSentence + " Time: " + System.currentTimeMillis() );
		 
		 //3. Send the Response message - write output line to socket
		 try {
			outToClient.writeBytes(modifiedSentence);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return true;
	 }
}

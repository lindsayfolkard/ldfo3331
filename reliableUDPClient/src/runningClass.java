import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class runningClass {
	public static void main(String args[]) throws Exception{
		reliableUDTClient myClient=new reliableUDTClient(4536);
		myClient.sendMessage("Hello", InetAddress.getByName("localhost"),2658 );
	}
}

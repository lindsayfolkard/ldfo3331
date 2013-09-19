/**  
 * UDPClient.java -- Simple UDP client  
 *  
 * $Id: UDPClient.java,v 1.2 2003/10/14 14:25:30 kangasha Exp $  
 *  
 */   
   
import java.net.InetAddress;   
import java.net.SocketException;   
import java.net.SocketTimeoutException;   
import java.net.UnknownHostException;   
import java.util.Date;   
   
public class UDPClient extends UDPPinger implements Runnable {   
    /** Host to ping */   
    String remoteHost;   
   
    /** Port number of remote host */   
    int remotePort;   
   
    /** How many pings to send */   
    static final int NUM_PINGS = 10;   
   
    /** How many reply pings have we received */   
    int numReplies = 0;   
   
    /** Array for holding replies and RTTs */   
    static boolean[] replies = new boolean[NUM_PINGS];   
   
    static long[] rtt = new long[NUM_PINGS];   
   
    /*  
     * Send our own pings at least once per second. If no replies received  
     * within 5 seconds, assume ping was lost.  
     */   
    /** 1 second timeout for waiting replies */   
    static final int TIMEOUT = 1000;   
   
    /** 5 second timeout for collecting pings at the end */   
    static final int REPLY_TIMEOUT = 5000;   
   
    /** Create UDPClient object. */   
    public UDPClient(String host, int port) {   
        remoteHost = host;   
        remotePort = port;   
    }   
   
    /**  
     * Main Function.  
     */   
    public static void main(String args[]) {   
        String host = "127.0.0.1";
	int port = 8000;   
   
        System.out.println("Contacting host " + host + " at port " + port);   
   
        UDPClient client = new UDPClient(host, port);   
        client.run();      
    }   
       
    /** Main code for pinger client thread. */   
    public void run() {   
        /* Create socket. We do not care which local port we use. */   
        createSocket();   
        try {   
            socket.setSoTimeout(TIMEOUT);   
        } catch (SocketException e) {   
            System.out.println("Error setting timeout TIMEOUT: " + e);   
        }   
   
        for (int i = 0; i < NUM_PINGS; i++) {   
            /*  
             * Message we want to send. Add space at the end for easy parsing of  
             * replies.  
             */   
            Date now = new Date();   
            String message = "PING " + i + " " + now.getTime() + " ";   
            replies[i] = false;   
            rtt[i] = 1000000;   
            PingMessage ping = null;   
   
            /* Send ping to recipient */   
            try {   
                ping = new PingMessage(InetAddress.getByName(remoteHost),   
                        remotePort, message);   
            } catch (UnknownHostException e) {   
                System.out.println("Cannot find host: " + e);   
            }   
            sendPing(ping);   
   
            /* Read reply */   
            try {   
                PingMessage reply = receivePing();   
                handleReply(reply.getContents());      
            } catch (SocketTimeoutException e) {   
                /*  
                 * Reply did not arrive. Do nothing for now. Figure out lost  
                 * pings later.  
                 */   
            }   
        }   
        /*  
         * We sent all our pings. Now check if there are still missing replies.  
         * Wait for a reply, if nothing comes, then assume it was lost. If a  
         * reply arrives, keep looking until nothing comes within a reasonable  
         * timeout.  
         */   
        try {   
            socket.setSoTimeout(REPLY_TIMEOUT);   
        } catch (SocketException e) {   
            System.out.println("Error setting timeout REPLY_TIMEOUT: " + e);   
        }   
        while (numReplies < NUM_PINGS) {   
            try {   
                PingMessage reply = receivePing();   
                handleReply(reply.getContents());   
            } catch (SocketTimeoutException e) {   
                /* Nothing coming our way apparently. Exit loop. */   
                numReplies = NUM_PINGS;   
            }   
        }   
        /* Print statistics */   
        for (int i = 0; i < NUM_PINGS; i++) {   
            System.out.println("PING " + i + ": " + replies[i] + " RTT: "   
                    + ((rtt[i] > 0) ? Long.toString(rtt[i]):"< 1")+ " ms");   
        }   
   
    }   
   
    /**  
     * Handle the incoming ping message. For now, just count it as having been  
     * correctly received.  
     */   
    private void handleReply(String reply) {   
        String[] tmp = reply.split(" ");   
        int pingNumber = Integer.parseInt(tmp[1]);   
        long then = Long.parseLong(tmp[2]);   
        replies[pingNumber] = true;   
        /* Calculate RTT and store it in the rtt-array. */   
        Date now = new Date();   
        rtt[pingNumber] = now.getTime() - then;    
        numReplies++;   
    }   
}  

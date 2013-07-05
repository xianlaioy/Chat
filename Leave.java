package dchat;

import java.net.InetAddress;

public class Leave extends PMsg {
	
	/* LEAVE\n
	 * PEER <nick>\n
	 * SEQUENCE <number>\0
	 */
	
	public Leave(String peer, InetAddress address, int sequence) {
		super(peer, address, sequence);
		// TODO Auto-generated constructor stub
	}
	
	public String getInfo() {
		return "LEAVE" + "\nPEER " + super.getPeer() + "\nSEQUENCE " + super.getSequence() + '\0'; 
	}
}

package dchat;

import java.net.InetAddress;

public class Join extends PMsg {
	
	/* JOIN\n
	 * PEER <nick>\n
	 * SEQUENCE <number>\0
	 */
	
	public Join(String peer, InetAddress address, int sequence) {
		super(peer, address, sequence);
		// TODO Auto-generated constructor stub
	}
	
	public String getInfo() {
		return "JOIN" + "\nPEER " + super.getPeer() + "\nSEQUENCE " + super.getSequence() + '\0'; 
	}
}

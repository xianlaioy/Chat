package dchat;

import java.net.InetAddress;

public class Ack extends PMsg {
	
	/* ACK <number>\n
	 * PEER <nick>\0
	 */

	public Ack(String peer, InetAddress address, int sequence) {
		super(peer, address, sequence);
		// TODO Auto-generated constructor stub
	}
	
	public String getInfo() {
		return "ACK " + super.getSequence() + "\nPEER " + super.getPeer() + '\0'; 
	}
}

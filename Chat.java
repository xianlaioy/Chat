package dchat;

import java.net.InetAddress;

public class Chat extends PMsg {
	private String message;
	private String type;
	
	/* CHAT <message>\n
	 * TYPE <type>\n
	 * PEER <nick>\n
	 * SEQUENCE <number>\0
	 */

	public Chat(String peer, InetAddress address, int sequence, String message, String type) {
		super(peer, address, sequence);
		// TODO Auto-generated constructor stub

		this.message = message;
		this.type = type;
	}
	
	public String getInfo() {
		return "CHAT " + message + "\nTYPE " + type + "\nPEER " + super.getPeer() + "\nSEQUENCE " + super.getSequence() + '\0';
	}
}

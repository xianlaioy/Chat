package dchat;

import java.net.InetAddress;

public class Share extends PMsg {
	private String filename;
	private String type;
	
	/* SHARE <filename>\n
	 * TYPE <type>\n
	 * PEER <nick>\n
	 * SEQUENCE <number>\0
	 */
	
	public Share(String peer, InetAddress address, int sequence, String filename, String type) {
		super(peer, address, sequence);
		// TODO Auto-generated constructor stub

		this.filename = filename;
		this.type = type;
	}
	
	public String getInfo() {
		return "SHARE " + filename + "\nTYPE " + type + "\nPEER " + super.getPeer() + "\nSEQUENCE " + super.getSequence() + '\0'; 
	}
}

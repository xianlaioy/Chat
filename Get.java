package dchat;

import java.net.InetAddress;

public class Get extends PMsg {
	private String filename;
	private int port;
	
	/* GET <filename>\n
	 * PORT <port>\n
	 * PEER <nick>\n
	 * SEQUENCE <number>\0
	 */
	
	public Get(String peer, InetAddress address, int sequence, String filename, int port) {
		super(peer, address, sequence);
		// TODO Auto-generated constructor stub

		this.filename = filename;
		this.port = port;
	}
	
	public String getInfo() {
		return "GET " + filename + "\nPORT " + port + "\nPEER " + super.getPeer() + "\nSEQUENCE " + super.getSequence() + '\0'; 
	}
}

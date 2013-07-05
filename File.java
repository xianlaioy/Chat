package dchat;

import java.net.InetAddress;

public class File {
	private String nick;
	private String filename;
	private InetAddress address;
	
	public File(String nick, InetAddress address, String filename) {
		this.nick = nick;
		this.filename = filename;
		this.address = address;
	}
	
	public String getNick() {
		return nick;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public String print() {
		return (":\t" + nick + "\t- " + filename + "\n");
	}
}

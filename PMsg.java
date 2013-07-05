package dchat;

import java.net.InetAddress;
import java.util.*;

public class PMsg {
	private String peer;
	private int sequence;
	private InetAddress address; // package destiny address 
	private Calendar lastSend;
	private int nretries = 0;
	
	public PMsg(String peer, InetAddress address, int sequence) {
		this.peer = peer;
		this.address = address;
		this.sequence = sequence;
	}
	
	public String getPeer() {
		return peer;
	}
	
	public int getSequence() {
		return sequence;
	}
	
	public String getInfo() {
		return "";
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	
	public void setLastSend() {
		lastSend = new GregorianCalendar();
	}
	
	public boolean canSendAgain() {
		GregorianCalendar now = new GregorianCalendar();
		
		if (now.getTimeInMillis() - lastSend.getTimeInMillis() >= 1000) {
			return true;
		}
		else
			return false;
	}

	public void incrementRetries() {
		this.nretries++;
	}

	public boolean retries() {
		return nretries < 5;
	}
}

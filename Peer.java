package dchat;

import java.net.InetAddress;
import java.util.*;

public class Peer {
	private String peer;
	private InetAddress address;
	private Calendar calendar;
	private int sequence;
	
	public Peer(String peer, int sequence, InetAddress address) {
		this.peer = peer;
		this.sequence = sequence;
		this.address = address;
		calendar = new GregorianCalendar();
	}
	
	// to avoid duplicated protocol messages
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public int getSequence() {
		return sequence;
	}
	
	public String getPeer() {
		return peer;
	}
	
	// to refresh the associated time when a message is received from the respective peer
	public void updateTime() {
		calendar = new GregorianCalendar();
	}
	
	public boolean isActive() {
		GregorianCalendar now = new GregorianCalendar();
		
		if (now.getTimeInMillis() - calendar.getTimeInMillis() < 5*60*1000)
			return true;
		else return false;
	}
	
	public String print() {
		String date = calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR);
		String time = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE);
		
		return (peer + "\t" + address.toString() + "\t" + "Last seen: " + date + " " + time + "\n");
	}
}

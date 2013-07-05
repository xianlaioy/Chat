package dchat;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class ChatServerClient {
	
	private String nick = "AlunoFR-Isa";
	private int port;
	private int sequence;
	private boolean leavePM;
	private InetAddress publicAddress = null;
	private ChatWindow chat;
	private LinkedList<Peer> users = new LinkedList<Peer>();
	private LinkedList<File> me = new LinkedList<File>();
	private LinkedList<File> others = new LinkedList<File>();
	private DatagramSocket serverSocket = null;
	private ArrayList<PMsg> arrayMsgs = new ArrayList<PMsg>();
	private FileServer fs = null;
	private FileClient fc = null;
	
	/****************************** CHAT SERVER CLIENT ******************************/
	public ChatServerClient(String inick, int port, ChatWindow chat) {
		
		nick = inick;
		this.port = port;
		this.chat = chat;
		
		try {
			
			publicAddress = InetAddress.getByName("255.255.255.255");
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		
		sequence = (int) (Math.random() * 100.0);
		
		try {
			
			serverSocket = new DatagramSocket(port);
			
			// timeout of 1 second
			 serverSocket.setSoTimeout(1000);
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/****************************** GET SEQUENCE ******************************/
	public int getSequence() {
		
		return sequence;
	}
	
	/****************************** HANDLE TEXT ******************************/
	public void handleText(String []text) {
		
		String exit = "";
		
		/* Commands:
		 * - /say <nick> <message>
		 * - /peers
		 * - /share <filename> <nick>
		 * - /unshare <filename> <nick>
		 * - /list-shares
		 * - /get <filename> <nick>
		 * - /quit
		 */
		
		if (text[0].charAt(0) != '/') {  // CHAT PUBLIC
			
			for(int i = 0; i < text.length; i++) {
				exit += text[i] + " ";
			}
            sendMessage(exit, "public", "");
			chat.writeText(nick + " -> public: " + exit + "\n");
			return;
		}
		
		if (text[0].equals("/say")) {  // CHAT PRIVATE

			if (text.length > 2) {
				for(int i = 2; i < text.length; i++) {
					exit += text[i] + " ";
				}
	            sendMessage(exit, "private", text[1]);
				chat.writeText(nick + " -> " + text[1] + ": " + exit + "\n");
			}
		}
		
		else if (text[0].equals("/peers")) {  // LIST PEERS
			
			printPeers();
		}
		
		else if (text[0].equals("/share")) {  // SHARE PRIVATE/PUBLIC 
			
			if (users.size() == 0) {
				
				chat.writeText("User " + text[2] + " not found\n");
				return;
			}
		 
			if ((text.length == 3 && users.size() > 0) || (text.length == 2)) {
				sendShare(text);
			}	
		}
		
		else if (text[0].equals("/unshare")) {  // UNSHARE
			
			if (me.size() == 0) {
				
				chat.writeText("You are not sharing " + text[1] + "\n");
				return;
			}
		
			if ((text.length == 3 || text.length == 2) && (me.size() > 0)) {
				sendUnshare(text);
			}
		}
		
		else if (text[0].equals("/get")) {  // GET
			
			if (others.size() == 0) {
				
				chat.writeText("Nobody is sharing " + text[1] + "\n");
				return;
			}
			
			if ((text.length == 3)  && (others.size() > 0)) {
				sendGet(text);
			}	
		}
		
		else if (text[0].equals("/list-shares")) {  // LIST SHARES
			
			printShares();
		}
		
		else if (text[0].equals("/quit")) {  // QUIT
			Leave eu = new Leave(nick, publicAddress, sequence);
			client(eu, "public");
			System.exit(0);
		}
	
		else chat.writeText("Invalid command\n");  // ERROR
	}

	/****************************** SEND MESSAGE ******************************/
	private void sendMessage(String message, String type, String name) {  // CHAT : SEND
		
		// Chat(String peer, String address, int sequence, String message, String type)
		
		if (type.equals("private") && users.size() > 0) {
			
			for (int i = 0; i < users.size(); i++) {
				
				if (name.equals(users.get(i).getPeer())) {
					
					Chat msg = new Chat(nick, users.get(i).getAddress(), sequence, message, type);
					client(msg, type);
					return;
				}	
			}
		}
		if (type.equals("public")) {
			
			Chat msg = new Chat(nick, publicAddress, sequence, message, type);
			client(msg, type);
		}
	}
	
	/* A user entry should be removed from the active user list after 5 minutes 
	 * without receiving any message from him or after a LEAVE message sent by him.
	 */
	
	/****************************** PRINT PEERS ******************************/
	private void printPeers() {  // LIST PEERS
		
		if (users.size() != 0) {
			
			chat.writeText("Active peers:\n");
			
			for (int i = 0; i < users.size(); i++) {
				if (users.get(i).isActive()) {
					chat.writeText(users.get(i).print());
					return;
				}
				else {
					users.remove(i);
					return;
				}
			}
		}
		else {
			chat.writeText("You are alone. Wanna coffee?\n");
		}
	}
	
	/****************************** SEND SHARE ******************************/
	private void sendShare(String[] text) {  // SHARE : SEND
		// TODO Auto-generated method stub
		
		// Share(String peer, String address, int sequence, String filename, String type) 
		
		InetAddress address = null;
		
		if (text.length == 3) {
			
			for (int i = 0; i < users.size(); i++) {
				if (text[2].equals(users.get(i).getPeer())) {
					
					address = users.get(i).getAddress();
					Share msg = new Share(nick, address, sequence, text[1], "private");
					client(msg, "private");
					
					// File(String nick, String address, String filename)
					
					File newF = new File(text[2], address, text[1]);
					me.add(newF);
					return;
				}	
			}
		}
		else {
			
			address = publicAddress;
			Share msg = new Share(nick, address, sequence, text[1], "public");
			client(msg, "public");
			
			// File(String nick, String address, String filename)
			
			File newF = new File("public", address, text[1]);
			me.add(newF);
		}
	}

	/****************************** SEND UNSHARE ******************************/
	private void sendUnshare(String[] text) {  // UNSHARE
		// TODO Auto-generated method stub
		
		if (text.length == 3) {
			
			for (int i = 0; i < me.size(); i++) {
				if (text[2].equals(me.get(i).getNick())) {
					if (text[1].equals(me.get(i).getFilename())) {
						me.remove(i);
						return;
					}
					else {
						chat.writeText("File " + text[1] + " not found\n");
						return;
					}		
				}
			}
		}
		else {
				
			for (int i = 0; i < me.size(); i++) {
				if (text[1].equals(me.get(i).getFilename())) {
					me.remove(i);
					return;
				}
			}
		}
	}

	/****************************** SEND GET ******************************/
	private void sendGet(String[] text) {  // GET : SEND
		// TODO Auto-generated method stub
		
		fs = new FileServer(text[1]);  // create a server

		int fsPort = fs.getPort();  // get port
		
		fs.start();  // server start

		// send a GET protocol message with the port
		
		for (int i = 0; i < others.size(); i++) {
			if (text[2].equals(others.get(i).getNick())) {
				
				// Get(String peer, String address, int sequence, String filename, int port)
				
				Get msg = new Get(nick, others.get(i).getAddress(), sequence, text[1], fsPort);
				client(msg, "get");
				return;
			}
		}
	}
	
	/****************************** PRINT SHARES ******************************/
	private void printShares() {
		
		// shared to others by the local user
		chat.writeText("----------------------------LOCAL---------------------------\n"); 
		if (me.size() != 0) {
			for (int i = 0; i < me.size(); i++) {
				chat.writeText((i + 1) + me.get(i).print()); 	
			}
		}
		else {
			chat.writeText("You are a jealous person! You are not sharing anything!\n");
		}
		
		// shared by other users 
		chat.writeText("---------------------------REMOTE---------------------------\n");
		if (others.size() != 0) {
			for (int i = 0; i < others.size(); i++) {
				chat.writeText((i + 1) + others.get(i).print());
			}
		}
		else {
			chat.writeText("Nobody likes you! Nobody wants to share with you! \n");
		}
		
		chat.writeText("------------------------------------------------------------\n");
	}

	/****************************** CLIENT ******************************/
	public void client(PMsg pacote, String type) {
		
		InetAddress IPAddress = null;
		byte []sendData;
		
		try {
			
			IPAddress = pacote.getAddress();
			
		} catch (Exception e) {
			
			// TODO Auto-generated catch block
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		
	    sendData = pacote.getInfo().getBytes();  
	     
	    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
	     
	    try {
	    	
			serverSocket.send(sendPacket);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}   
		
		sequence++;
	    
	    /* ACK message should be used on all GET protocol messages and on CHAT 
   	     * and SHARE messages of type private.
   	     */
		
	    // add sendPacket to list of pending packets
	    if (type.equals("private") || type.equals("get")) {
	    	
	    	pacote.setLastSend();
	    	arrayMsgs.add(pacote);
	    }
	
	 }
	 
	/****************************** SERVER ******************************/
	 public void server() { 
		 	
	     while (true) {

	    	 byte[] receiveData = new byte[2048]; 
	    	 DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	    	 String []pacote = null;
	    	 String []command = null;
	    	 String []arr = new String[1];
	    	 String peer = "";
	    	 int posUser = -1;
	    	 int seqPck = 0;
	    	 leavePM = false;
	    	 
	    	 InetAddress add = null;
	    	 
	    	 try {	 
	    		 try {
	    			 
	    			 // process list of pending messages
	    			 processMessageList();
	    			 
	    			 serverSocket.receive(receivePacket);
	    			 
	    			 String[] packetStr = new String(receivePacket.getData()).split("\0");

	    			 String sentence = new String(receivePacket.getData());
	    			 
	/*** print ***/  System.out.println(sentence);
	    			 
	    			 if (packetStr.length < 1)
	    				 continue;
	    			 
	    			 pacote = packetStr[0].split("\n");	
	    			 
	    			 command = pacote[0].split(" ");
	    			 
	    			 if (command.length > 1)
	    				 arr = pacote[1].split(" ");
	    			 
	    			 // ACK : RECEIVED
	    			 if ((command[0].equals("ACK")) && (command.length == 2) && (pacote.length == 2) && (arr.length == 2)) {
	    				 
	    				 // ACK : arr[1] == peer's nick
	    				 
	    				 processAck(Integer.parseInt(command[1]), arr[1]);
	    			 } 
	    			 	 
	    			 if (pacote.length < 3)
		    			 continue;
	    			 
	    			 peer = pacote[pacote.length - 2].split(" ")[1];
	    			 
	    			 if (peer.equals(nick))
	    				 continue;
	    
	    			 // LEAVE : RECEIVED
	    			 posUser = processActivePeers(command, peer, posUser);
	    			 
	    			 if (leavePM) 
	    				 continue;
	    			 
	    			 String[] seqStr = pacote[pacote.length - 1].split(" ");
	    	    	 
	    	    	 if (seqStr.length == 2) {
	    	    		 seqPck = Integer.parseInt(seqStr[1]);
	    	    	 }
	    	    	 else {
	    	    		 System.out.println("ERROR: SEQUENCE " + pacote[pacote.length - 1]);
	    	    		 continue;
	    	    	 }
	    	    	 
	    	    	 add = receivePacket.getAddress();
	    			 
	    	    	 if (posUser >= 0) {
	    	    		 
	    	    		 // to avoid duplicated messages 
	    	    		 if (seqPck <= users.get(posUser).getSequence()) {
		    				 sendAck(receivePacket, seqPck);
		    				 continue;
		    			 }
		    			 else {
		    				 
		    				 // to update the peer's sequence number
		    				 users.get(posUser).setSequence(seqPck);
		    			 }
	    	    	 }
	    	    	 else {

	    	    		 // JOIN : RECEIVED
	    	    		 Peer newUser = new Peer(peer, seqPck, add);
	    	    		 users.add(newUser);
	    	    		 posUser = users.indexOf(newUser);
	    	    		 chat.writeText("JOIN : " + peer + "\n");
	    	    	 }
	    			
	    		 } catch (SocketTimeoutException e) {
	    			 continue;
	    		 } 
	    		 
	    	 } catch (Exception e) {
	    		// TODO Auto-generated catch block
	 			System.out.println("ERROR: " + e.getMessage());
	 			e.printStackTrace(); 
	    	 }

	    	 /* ACK message should be used on all GET protocol messages and on CHAT 
	    	  * and SHARE messages of type private.
	    	  */
	    	 
	    	 if (command.length < 2)
	    		 continue;
	    	 
	    	 if (command[0].equals("CHAT")) {  // CHAT : RECEIVED
	    		 
	    		 // arr[1] == type
	    		 
	    		 handleChat(command, arr[1], peer);  
	    		 
	    		 if (arr[1].equals("private")) {
	    			 try {
	    				 
	    				 sendAck(receivePacket, seqPck);
	    				 
	    			 } catch (Exception e) {
	    				 // TODO Auto-generated catch block
	    				 System.out.println("ERROR: " + e.getMessage());
	    				 e.printStackTrace();
	    			 }
		    	 }
	    	 }
	    	 else if (command[0].equals("SHARE")) {  // SHARE : RECEIVED
		    		 
	    		 // arr[1] == type
	    		 
	    		 handleShare(command[1], add, arr[1], peer);
		    		 
	    		 if (arr[1].equals("private")) {
	    			 sendAck(receivePacket, seqPck);
	    			 chat.writeText("SHARE : " + peer + " shared " + command[1] + " with you\n");
	    		 }
	    		 else
	    			 chat.writeText("SHARE : " + peer + " shared " + command[1] + "\n");
	    		 
	    	 }
	    	 else if (command[0].equals("GET")) {  // GET : RECEIVED
		    		
	    		 // arr[1] == port
	    		 
	    		 handleGet(command[1], peer, add, Integer.parseInt(arr[1]));
	    		 sendAck(receivePacket, seqPck);
		     }
	     }
	 }

	 /****************************** PROCESS ACTIVE PEERS ******************************/
	 private int processActivePeers(String[] command, String peer, int posUser) {
		 if (users.size() > 0) {
			 for (int i = 0; i < users.size(); i++) {
				 if (users.get(i).getPeer().equals(peer)) {
					 if (command[0].equals("LEAVE")) {
						chat.writeText("LEAVE : " + peer + "\n");
						users.remove(i);
						leavePM = true;
						break;
					 }
					 users.get(i).updateTime();
					 posUser = i;
					 break;
				 }
			 }
		}
		return posUser;
	}

	/****************************** PROCESS MESSAGE LIST ******************************/
	private void processMessageList() {
		
		 byte[] sendData;
		 InetAddress IPAddress = null;
		
		 for (int i = 0; i < arrayMsgs.size(); i++) {
			 
			 // returns false if the package has already been sent 5 times
			 if (!arrayMsgs.get(i).retries()) {
				 arrayMsgs.remove(i);
				 break;
			 }
			 else {
				 
				 // returns true if a second has passed since the last sent package 
				 if (arrayMsgs.get(i).canSendAgain()) {
					 sendData = arrayMsgs.get(i).getInfo().getBytes();
					 
					IPAddress = arrayMsgs.get(i).getAddress();
					
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					 
					try {
						
						 serverSocket.send(sendPacket);
					
					} catch (IOException e) {
						 // TODO Auto-generated catch block
						 System.out.println("ERROR: " + e.getMessage());
						 e.printStackTrace();
					}   
					
					arrayMsgs.get(i).setLastSend();
					arrayMsgs.get(i).incrementRetries();
				}
				 
			 }
		 }
	 }
	
	/****************************** PROCESS ACK ******************************/
	 private void processAck(int num, String nickPeer) {  // ACK : RECEIVED
		 // TODO Auto-generated method stub
		 
		 if (arrayMsgs.size() > 0) {
			 for (int i = 0; i < arrayMsgs.size(); i++) {
				 if (arrayMsgs.get(i).getSequence() == num) {
					 arrayMsgs.remove(i);
					 return;
				 }
			 }
		 }
		 if (users.size() > 0) {
			 for (int i = 0; i < users.size(); i++) {
				 if (users.get(i).getPeer().equals(nickPeer)) {
					 users.get(i).updateTime();
					 return;
				 }
			 }
		 }
	 }
	 
	 /****************************** SEND ACK ******************************/
	 private void sendAck(DatagramPacket receivePacket, int seq) {  // ACK : SEND
		 
		 InetAddress address = receivePacket.getAddress(); 
    	 int iport = receivePacket.getPort();
    	 
    	 Ack ack = new Ack(nick, address, seq);
    	 
    	 byte[] sendData = ack.getInfo().getBytes();
    	 
		 DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, iport);
		 
	     try {
	    	 
	    	 serverSocket.send(sendPacket);
	    	 
		 } catch (IOException e) {
			 // TODO Auto-generated catch block
			 System.out.println("ERROR: " + e.getMessage());
		 	 e.printStackTrace(); 
		 } 			 
	}

	/****************************** HANDLE CHAT ******************************/
	private void handleChat(String []message, String type, String peer) {  // CHAT : RECEIVED
		// TODO Auto-generated method stub
		
		String msg = "";

		for (int i = 1; i < message.length; i++) {
			
			msg += " " + message[i];
		}

		if (type.equals("private")) {
                    
			chat.writeText(peer + " -> " + nick + ":" + msg + "\n");
		}

		else {
			chat.writeText(peer + " -> public:" + msg + "\n");
		}
	}

	/****************************** HANDLE SHARE ******************************/
	private void handleShare(String filename, InetAddress address, String type, String peer) {  // SHARE : RECEIVED
		// TODO Auto-generated method stub
		
		// File(String nick, String address, String filename)
		
		File newFile = new File(peer, address, filename);
		others.add(newFile);
	}

	/****************************** HANDLE GET ******************************/
	private void handleGet(String filename, String peer, InetAddress ip, int portR) {  // GET : RECEIVED
		// TODO Auto-generated method stub

		for (int i = 0; i < me.size(); i++) {
			if (peer.equals(me.get(i).getNick()) || me.get(i).getNick().equals("public")) {
				if (filename.equals(me.get(i).getFilename())) {
					
					fc = new FileClient(filename, ip, portR);  // create a client with filename, IP address and port
					fc.start();  // start
					return;
				}
			}
		}
	}
}

package dchat;

import java.net.InetAddress;

//import java.net.*;

public class DChat implements ChatWindowClient {
	private ChatWindow chat;
	private ChatServerClient sc;
	String nick = "ChatUser00";
	int port = 9900;
	
	//Handler for text provided to the input area
	public void textAvailable(String text)
	{
		//TODO:Process user input
		
		//This will write all text to the Text Output
		//chat.writeText(text);
		text = text.trim();
		
		if(text.length() == 0)
			return;
		
		String []splitMsg = text.split(" ");

		try {
			sc.handleText(splitMsg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}

	//Handler for the Window Close Event
	public void windowClosed() {
		///TODO:Should inform all peers
		System.exit(0);
	}

	//Handler for the Window Open Event
	public void windowOpened() {
		//TODO: Should inform all peers
		chat.writeText("Starting Console Application\n");
		chat.writeText("Welcome " + nick + "\n");
	}
	
	//Application main method
	public void run(String[] args)
	{
		try {
			
			if(args.length >= 1) 
				nick = args[0];
			
			if(args.length >= 2)
				port = Integer.parseInt(args[1]);
			
			chat = new ChatWindow(new String(), this);
			chat.setNick(nick);
			sc = new ChatServerClient(nick, port, chat);
			Join eu = new Join(nick, InetAddress.getByName("255.255.255.255"), sc.getSequence());
			sc.client(eu, "public");
			sc.server();
			
		} catch(Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) 
	{
		DChat dchat = new DChat();
		dchat.run(args);
	}
}
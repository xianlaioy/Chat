package dchat;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileServer extends Thread {  // GET : SEND
	
	private ServerSocket sock = null;
	private String filename;
	
	public FileServer(String filename)
	{
            this.filename = filename;

            try {
            	
                sock = new ServerSocket();
                sock.bind(null);  // associates socket with local address
                 
            } catch (IOException ex) {
            	
            	Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
	
	public int getPort()
	{
		return sock.getLocalPort();
	}
	
	public void run()
	{
		Socket clientSocket;
		DataInputStream bufRead = null;
		DataOutputStream bufWriter = null;
		int nbytes = 0;
		byte[] cbuf = new byte[2048];
		FileOutputStream fileOut = null;
		
		try {
			
			fileOut = new FileOutputStream(filename);
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
		
			clientSocket = sock.accept();  // waits for new connections
			bufRead = new DataInputStream(clientSocket.getInputStream());
			bufWriter = new DataOutputStream(fileOut);
         	     
            while ((nbytes = bufRead.read(cbuf)) != -1) {
            	
            	bufWriter.write(cbuf, 0, nbytes);
            }
            
			bufWriter.flush();
            
		} catch (IOException ex) {
            	  
			Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
        }
		
		System.out.println(bufWriter.toString());
		
		try {
			
			sock.close();
			bufWriter.close();
			bufRead.close();
			fileOut.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package dchat;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;

public class FileClient extends Thread {  // GET : RECEIVED

	private Socket sock = null;
	private String filename;
	private int port;
	private InetAddress ip;

	public FileClient(String filename, InetAddress ip, int port)
	{
		this.filename = filename;
		this.ip = ip;
		this.port = port;
		sock = new Socket();
	}

	public void run()
	{		
		DataInputStream bufRead = null;
		DataOutputStream bufWriter = null;
   
        FileInputStream fileIn = null;
        
        SocketAddress endpoint;
        
        byte[] cbuf = new byte[2048];
		int nbytes = 0;
		
		File newFile = new File(filename);
        
		if (!newFile.exists() && !newFile.canRead() && !newFile.isFile()) {
			return;
		}
			
		try {
			
			fileIn = new FileInputStream(newFile);
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			
			endpoint = new InetSocketAddress(ip, port);
			sock.connect(endpoint);  // connect to a remote address
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {
        	
        	bufRead = new DataInputStream(fileIn);
        	bufWriter = new DataOutputStream(sock.getOutputStream());

            while ((nbytes = bufRead.read(cbuf)) != -1) { 
            	  
            	bufWriter.write(cbuf, 0, nbytes);
            }
            
			bufWriter.flush();
                    
        } catch (IOException ex) {
        	
        	Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
        
        	sock.close();
			bufWriter.close();
			bufRead.close();
			fileIn.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
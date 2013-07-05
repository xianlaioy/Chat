package dchat;

public interface ChatWindowClient {
	//This method is called when text is available
	public void textAvailable(String arg) throws Exception;
	
	//This method is called when the window is closed
	public void windowClosed();
	
	//This method is called when the window is started
	public void windowOpened();
}

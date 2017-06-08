// IMConnectable.java
// An interface implemented by IMServer and IMClient. 
// Devon Bautista

import java.io.*;
import java.net.*;

public interface IMConnectable
{
	ObjectOutputStream output = null;
	ObjectInputStream input = null;
	Socket connection = null;
	IMWindow win = null;

	public void initializeStreams() throws IOException;
	public void doWhileChatting() throws IOException;
	public void closeConnection() throws IOException;
	public void sendMessage(String msg) throws IOException;
	public void run();
	public void start();
	public void stop();
	public Socket getConnection();
	public ServerSocket getServer();	// Only needs to be implemented by IMServer
}	// End of interface IMRunnable
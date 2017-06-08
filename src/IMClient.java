// IMClient.java
// The client side of SimpleIM.
// Devon Bautista

import java.io.*;
import java.net.*;

/**
 * Represents an IM client object.
 **/
public class IMClient implements IMConnectable, Runnable
{
	// Attributes
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private volatile Socket connection;
	private IMWindow win;
	private String IPAddress;
	private int port;
	private Thread thread = null;

	public IMClient(String IPAddress, int port, IMWindow win)
	{
		this.win = win;					// Store IMWindow object
		this.port = port;				// Store port server is hosted on
		this.IPAddress = IPAddress;		// Store IP address of server to connect to

		this.start();
	}	// End of constructor

	public void run()
	{
		try
		{
			this.connectToServer();			// Connect with a client
			this.initializeStreams();		// Set up IO streams for server and client
			this.win.setConnected(true);	// Set connection to true
			this.win.ableToSend(true);		// Enable "Send" button
			this.win.printInformationMessage("Connection set. Start chatting!");
			this.doWhileChatting();			// Do while connection is established
		}	// End of try
		catch (EOFException e)
		{
			// When connection has terminated, let user know.
			this.win.printInformationMessage("Connection terminated.");
		}	// End of catch
		catch (IOException e)
		{
			try
			{
				this.win.printInformationMessage("No such server found.");
				this.connection.close();
			}	// End of try
			catch (IOException ex)
			{
				this.win.printInformationMessage("Unable to close connection.");
			}	// End of catch
			catch (NullPointerException ex)
			{
				// Socket has been closed
			}	// End of catch
		}	// End of catch
		finally
		{
			try
			{
				this.closeConnection();
				this.win.setSession(null);
				this.win.ableToSend(false);
				this.win.printInformationMessage("Connection closed.");
			}	// End of try
			catch (IOException e)
			{
				this.win.printInformationMessage("Unable to close connection.");
			}	// End of catch
			catch (NullPointerException e)
			{
				this.win.printInformationMessage("Connection cancelled.");
			}	// End of catch
		}	// End of finally
	}	// End of method run

	public void start()
	{
		if (thread == null)
		{
			thread = new Thread(this);
			thread.setDaemon(false);
			thread.start();
		}	// End of if
	}	// End of method start

	public void stop()
	{
		if (thread != null)
		{
			thread.interrupt();
			thread = null;
		}	// End of if
	}	// End of method stop

	/**
	 * Connects to server, then displays connection information.
	 * @throws IOException if there is a problem creating a new Socket.
	 **/
	public void connectToServer() throws IOException
	{
		this.win.printInformationMessage("Waiting for connection...");							// Print status to IMWindow object
		this.connection = new Socket(InetAddress.getByName(this.IPAddress), this.port);			// Connect to server, then proceed
		this.win.printInformationMessage("Now connected to " + this.connection.getInetAddress()
			+ " on port " + this.port + ".");
	}	// End of method connectToServer

	/**
	 * Gets stream to send and receive messages.
	 * @throws IOException if unable to get Socket's
	 * IO streams.
	 **/
	public void initializeStreams() throws IOException
	{
		this.output = new ObjectOutputStream(this.connection.getOutputStream());	// Get output stream of socket
		this.output.flush();														// Send any residual data to server
		this.input = new ObjectInputStream(this.connection.getInputStream());		// Get input stream of socket
		this.win.printInformationMessage("IO streams initialized.");				// Print status to IMWindow object
	}	// End of method initializeStreams

	/**
	 * Executes actions during chat session.
	 **/
	public void doWhileChatting()
	{
		String message = "";

		do
		{
			try
			{
				// Read incoming message from socket
				message = (String) this.input.readObject();

				if (!message.equals("-=CLOSE=-"))
					// Print incoming message in IMWindow object
					this.win.printIncomingMessage(message);
				else
					break;
			}	// End of try
			catch (ClassNotFoundException e)
			{
				this.win.printInformationMessage("Unable to parse incoming data.");
				break;
			}	// End of catch
			catch (IOException e)
			{
				// End of connection
				break;
			}	// End of catch
		} while (true);
	}	// End of method doWhileChatting

	/**
	 * Deinitializes streams and closes socket connection.
	 * @throws IOException if there is a problem closing the
	 * IO streams.
	 **/
	public void closeConnection() throws IOException
	{
		this.win.printInformationMessage("Closing connection...");		// Print status to IMWindow object
		
		this.connection.close();		// Close socket connection
		this.output.close();			// Close output stream
		this.input.close();				// Close input stream
		this.win.setConnected(false);	// Set connected to false
	} // End of method closeConnection

	/**
	 * Sends a message to the server.
	 * @param msg The String to send to the server.
	 * @throws IOException if there is a problem writing
	 * to the Socket's output stream.
	 **/
	public void sendMessage(String msg) throws IOException
	{
		this.output.writeObject(msg);				// Send msg to socket for output
		this.output.flush();						// Clear any residual data from output stream
		if (!msg.equals("-=CLOSE=-"))
			this.win.printOutgoingMessage(msg);	// Print incoming message in IMWindow object
		else
		{
			this.stop();						// Stop connection thread
		}	// End of else
	}	// End of method sendMessage

	/**
	 * Requires no implementation
	 * since this class represents a client.
	 * @return null
	 **/
	public ServerSocket getServer() {return null;}

	/**
	 * Returns the Socket object of this IMConnectable
	 * instance.
	 * @return connection The Socket object of this IMConnectable
	 * instance.
	 **/
	public Socket getConnection()
	{
		return this.connection;
	}	// End of method getConnection
}	// End of class Client
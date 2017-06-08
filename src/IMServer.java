// IMServer.java
// The server side of SimpleIM.
// Devon Bautista

import java.io.*;
import java.net.*;

/**
 * Represents an IM server object.
 **/
public class IMServer implements IMConnectable, Runnable
{
	// Attributes
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private volatile ServerSocket server;
	private Socket connection;
	private int port;
	private IMWindow win;
	private Thread thread = null;

	/**
	 * Sets up server and starts running it.
	 * @param port The port number to run the server off of.
	 * @param win The IMWindow object to run the server off of.
	 * @throws IOException if
	 **/
	public IMServer(int port, IMWindow win)
	{
		this.win = win;		// Store IMWindow object
		this.port = port;	// Store port
		this.start();
	}	// End of constructor

	public void run()
	{
		try
		{
			// Initialize server
			this.server = new ServerSocket(this.port);

			try
			{
				this.waitForConnection();		// Connect with a client
				this.initializeStreams();		// Set up IO streams for server and client
				this.win.setConnected(true);	// Set connection to true
				this.win.ableToSend(true);		// Enable "Send" button
				this.win.printInformationMessage("Connection set. Start chatting!");
				this.doWhileChatting();			// Do while connection is established
			}	// End of try
			catch (EOFException e)
			{
				// When connection has terminated, let user know.
				win.printInformationMessage("Connection terminated.");
			}	// End of catch
			catch (NullPointerException e)
			{
				this.win.printInformationMessage("Connection cancelled.");
			}	// End of catch
			finally
			{
				if (this.connection != null)
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
				}	// End of if
			}	// End of finally
		}	// End of try
		catch (IOException e)
		{
			this.win.printInformationMessage("This server is already running.");
			this.win.printInformationMessage("Waiting for connection...");
		}	// End of catch
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
	 * Waits for connection to a client, then displays connection information.
	 * @throws IOException if server ServerSocket's accept() is interrupted.
	 **/
	public void waitForConnection() throws IOException
	{
		try
		{
			this.win.printInformationMessage("Waiting for connection...");	// Print status to IMWindow object
			this.connection = server.accept();								// Wait for a client connection, then proceed
			this.win.printInformationMessage("Now connected to " + this.connection.getInetAddress()
				+ " on port " + this.port + ".");							// Print status to IMWindow object
		}
		catch (SocketException e)
		{
			// Socket has been closed. Do nothing.
		}	// End of catch
	}	// End of method waitForConnection

	/**
	 * Gets stream to send and receive messages.
	 * @throws IOException if there is a problem getting the
	 * Socket's IO streams.
	 **/
	public void initializeStreams() throws IOException
	{
		this.output = new ObjectOutputStream(this.connection.getOutputStream());	// Get output stream of socket
		this.output.flush();														// Send any residual data to client
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
	 * @throws IOException if there is a problem closing the IO streams.
	 * @throws SocketException when Socket is closed.
	 **/
	public void closeConnection() throws IOException, SocketException
	{
		this.win.printInformationMessage("Closing connection...");		// Print status to IMWindow object
		this.server.close();			// Close server
		this.connection.close();		// Close socket connection
		this.output.close();			// Close output stream
		this.input.close();				// Close input stream
		this.win.setConnected(false);	// Set connected to false
	}	// End of method closeConnection

	/**
	 * Sends a message to the client.
	 * @param msg The String to send to the client.
	 * @throws IOException if unable to write to output stream.
	 **/
	public void sendMessage(String msg) throws IOException
	{
		this.output.writeObject(msg);			// Send msg to socket for output
		this.output.flush();					// Clear any residual data from output stream
		if (!msg.equals("-=CLOSE=-"))
			this.win.printOutgoingMessage(msg);	// Print incoming message in IMWindow object
		else
		{
			this.stop();						// Stop connection thread
		}	// End of else
	}	// End of method sendMessage

	/**
	 * Getter method for ServerSocket object.
	 * @return server The ServerSocket object of the IMServer instance.
	 **/
	public ServerSocket getServer()
	{
		return this.server;
	}	// End of method getServer

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
}	// End of class IMServer
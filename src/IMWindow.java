// IMWindow.java
// The main window of the IM application.
// Devon Bautista

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import java.io.*;

/**
 * Represents an instance of the main window. All
 * widgets and event handlers are contained within this class.
 * @author Devon Bautista
 **/
class IMWindow extends JFrame
{
	// Global widgets and objects
	private StyledDocument doc;
	private SimpleAttributeSet styIncoming = new SimpleAttributeSet();
	private SimpleAttributeSet styOutgoing = new SimpleAttributeSet();
	private SimpleAttributeSet styBody = new SimpleAttributeSet();
	private SimpleAttributeSet styInfo = new SimpleAttributeSet();
	private JTextPane txtMessages;
	private JTextArea txtMessageEntry;
	private JButton btnSend;
	private boolean connected = false;
	private IMConnectable session = null;
	private SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
	private ImageIcon ico = new ImageIcon("SimpleIM.png");

	// Menu bar and menu items
	private JMenuBar menuBar;
	private JMenu mnuOptions;
	private JMenuItem itmConnect, itmDisconnect;

	// Constants
	private int FRAME_HEIGHT = 575;
	private int FRAME_WIDTH = 600;
	private Dimension MINIMUM_FRAME_SIZE = new Dimension(400, 300);
	private Dimension PREFERRED_CHAT_SIZE = new Dimension(100, 100);
	private Dimension PREFERRED_ENTRY_SIZE = new Dimension(500, 50);

	/**
	 * Sets up JFrame and creates widgets.
	 **/
	public IMWindow()
	{
		// Window specifics
		this.setTitle("SimpleIM - Instant Messenger");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(FRAME_HEIGHT, FRAME_WIDTH);
		this.setMinimumSize(MINIMUM_FRAME_SIZE);
		this.setIconImage(ico.getImage());
		this.setVisible(true);

		// Create menu bar and widgets
		this.createMenuBar();
		this.createWidgets();

		// Disable sending until connection is made
		this.ableToSend(false);

		// Set styles of chat field
		this.doc = txtMessages.getStyledDocument();
		StyleConstants.setForeground(styIncoming, Color.GREEN);
		StyleConstants.setBold(styIncoming, true);
		StyleConstants.setAlignment(styIncoming, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(styOutgoing, Color.BLUE);
		StyleConstants.setBold(styOutgoing, true);
		StyleConstants.setAlignment(styOutgoing, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(styBody, Color.BLACK);
		StyleConstants.setForeground(styInfo, Color.GRAY);
		StyleConstants.setItalic(styInfo, true);
		StyleConstants.setAlignment(styInfo, StyleConstants.ALIGN_CENTER);

		// Refresh window
		this.revalidate();		
	}	// End of constructor

	/**
	 * Creates menu bar on main window.
	 **/
	private void createMenuBar()
	{
		// Create menu bar
		this.menuBar = new JMenuBar();

		//*******************************************************
		//* "Options" menu
		//*******************************************************
		this.mnuOptions = new JMenu("Options");								// Create menu bar
		this.mnuOptions.setMnemonic(KeyEvent.VK_O);							// Set activation key "O"
		this.menuBar.add(this.mnuOptions);									// Add menu to menu bar

		// "Connect..." menu item
		this.itmConnect = new JMenuItem("Connect...", KeyEvent.VK_C);		// Create menu item and set activation key "C"
		this.itmConnect.addActionListener(new ConnectMenuListener());		// Add event handler to menu item
		this.mnuOptions.add(this.itmConnect);								// Add menu item to menu bar

		// "Terminate Connection" menu item
		this.itmDisconnect = new JMenuItem("Terminate Connection");			// Create menu item
		this.itmDisconnect.addActionListener(new DisconnectMenuListener());	// Add event handler to menu item
		this.mnuOptions.add(this.itmDisconnect);							// Add menu item to menu bar
		this.mnuOptions.addSeparator();										// Add horizontal line after menu item

		// Add menu bar to main window
		this.setJMenuBar(this.menuBar);
	}	// End of method createMenuBar

	/**
	 * Creates all widgets on main window.
	 **/
	private void createWidgets()
	{
		// Panel for txtMessages
		JPanel panMessages = new JPanel();												// Create new panel
		panMessages.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));			// Add padding around text area
		panMessages.setLayout(new BoxLayout(panMessages, BoxLayout.PAGE_AXIS));			// Set panel to vertical BoxLayout
		this.txtMessages = new JTextPane();												// Create messages text area
		JScrollPane scrlMessages = new JScrollPane(this.txtMessages);					// Create scrollbar for messages text area
		this.txtMessages.setEditable(false);											// Make messages text area uneditable
		this.txtMessages.setPreferredSize(PREFERRED_CHAT_SIZE);							// Set preferred size of text area
		panMessages.add(scrlMessages);													// Add txtMessages to panel

		// Panel for message entry and send button
		JPanel panMessageEntry = new JPanel();											// Create new panel
		panMessageEntry.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));		// Create 10-pixel invisible border around panel
		panMessageEntry.setLayout(new BoxLayout(panMessageEntry, BoxLayout.LINE_AXIS));	// Set panel to horizontal BoxLayout
		this.txtMessageEntry = new JTextArea();											// Create message input text area
		JScrollPane scrlMessageEntry = new JScrollPane(txtMessageEntry);				// Create scrollbar for message input
		scrlMessageEntry.setPreferredSize(PREFERRED_ENTRY_SIZE);						// Set preferred size of message entry field
		this.txtMessageEntry.setWrapStyleWord(true);									// Enable word wrap
		this.txtMessageEntry.setLineWrap(true);											// Enable wrapping at word boundaries
		this.btnSend = new JButton("Send");												// Create "Send" button
		this.btnSend.addActionListener(new SendButtonListener());						// Add event handler to "Send" button
		this.btnSend.setAlignmentY(Component.TOP_ALIGNMENT);							// "Send" button stays toward top of panel
		scrlMessageEntry.setAlignmentY(Component.TOP_ALIGNMENT);						// Message entry stays toward top of panel
		panMessageEntry.add(scrlMessageEntry);											// Add input text area to panel
		panMessageEntry.add(Box.createRigidArea(new Dimension(10, 0)));					// Add 10-px space between entry field and "Send" button
		panMessageEntry.add(this.btnSend);												// Add "Send" button

		// Add panels to main window
		this.add(panMessages, BorderLayout.CENTER);										// Add messages text area to body
		this.add(panMessageEntry, BorderLayout.SOUTH);									// Add message entry panel to bottom
	}	// End of method createWidgets

	/**
	 * Sets whether a user can send messages.
	 * @param canSend True lets user send message, false
	 * prohibits the user to type.
	 **/
	public void ableToSend(final boolean canSend)
	{
		this.btnSend.setEnabled(canSend);
	}	// End of method ableToType

	/**
	 * Returns connection status.
	 * @return connected True if connected, false if not.
	 **/
	public boolean isConnected()
	{
		return connected;
	}	// End of method isConnected

	/**
	 * Sets connection status.
	 * @param setConnection True to set status as connected, false
	 * to set status as disconnected.
	 **/
	public void setConnected(final boolean setConnection)
	{
		this.connected = setConnection;
	}	// End of method setConnected

	/**
	 * Returns IMConnectable session object.
	 **/
	public IMConnectable getSession()
	{
		return this.session;
	}	// End of method getSession

	/**
	 * Sets session to a IMConnectable object.
	 **/
	public void setSession(IMConnectable newSession)
	{
		this.session = newSession;
	}	// End of method setSession

	/**
	 * Prints incoming message to the chat area with formatting.
	 * @param msg The String to be printed with formatting.
	 **/
	public void printIncomingMessage(String msg)
	{
		SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run()
				{
					try
					{
						Calendar cal = Calendar.getInstance();
						String INCOMING_PREFIX = "[" + fmt.format(cal.getTime()) + "]<< ";
						doc.setParagraphAttributes(doc.getLength(), msg.length() + INCOMING_PREFIX.length(), styIncoming, true);
						doc.insertString(doc.getLength(), INCOMING_PREFIX, styIncoming);
						doc.insertString(doc.getLength(), msg.trim() + "\n", styBody);	
					}	// End of try
					catch (Exception e)
					{
						e.printStackTrace();
					}	// End of catch	
				}	// End of method run
			});	
	}	// End of method printIncomingMessage

	/**
	 * Prints outgoing message to the chat area with formatting.
	 * @param msg The String to be printed with formatting.
	 **/
	public void printOutgoingMessage(String msg)
	{
		SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run()
				{
					try
					{
						Calendar cal = Calendar.getInstance();
						String OUTGOING_PREFIX = "[" + fmt.format(cal.getTime()) + "]>> ";
						doc.setParagraphAttributes(doc.getLength(), msg.length() + OUTGOING_PREFIX.length(), styOutgoing, true);
						doc.insertString(doc.getLength(), OUTGOING_PREFIX, styOutgoing);
						doc.insertString(doc.getLength(), msg.trim() + "\n", styBody);
					}	// End of try
					catch (Exception e)
					{
						e.printStackTrace();
					}	// End of catch
				}	// End of method run
			});
	}	// End of method printOutgoingMessage

	/**
	 * Prints information message to the chat area with formatting.
	 * @param msg The String to be printed with formatting.
	 **/
	public void printInformationMessage(String msg)
	{
		SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run()
				{
					try
					{
						doc.setParagraphAttributes(doc.getLength(), msg.length(), styInfo, true);
						doc.insertString(doc.getLength(), msg.trim() + "\n", styInfo);
					}	// End of try
					catch (Exception e)
					{
						e.printStackTrace();
					}	// End of catch
				}	// End of method run
			});
	}	// End of method printInformationMessage

	//*******************************************************
	//* Event Handlers
	//*******************************************************
	/**
	 * Listens for when the "Send" button is clicked.
	 **/
	private class SendButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			String outGoingMessage = txtMessageEntry.getText();
			if (!outGoingMessage.equals(""))
			{
				try
				{
					session.sendMessage(outGoingMessage);
					txtMessageEntry.setText("");
				}	// End of try
				catch (IOException e)
				{
					// sendMessage() method will print error statement
				}
			}	// End of if
		}	// End of method actionPerformed
	}	// End of class SendButtonListener

	/**
	 * Listens for when "Connect..." is clicked from the
	 * menu bar.
	 **/
	private class ConnectMenuListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			// Open a connection window
			new ConnectionWindow(IMWindow.this);
		}	// End of method actionPerformed
	}	// End of class ConnectMenuListener

	/**
	 * Listens for when "Terminate Connection" is clicked
	 * from the menu bar.
	 **/
	private class DisconnectMenuListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if (IMWindow.this.isConnected() == true)
			{
				try
				{
					ableToSend(false);
					IMWindow.this.getSession().sendMessage("-=CLOSE=-");
				}	// End of try
				catch (IOException e)
				{
					// closeConnection() handles exception
				}	// End of catch
			}	// End of if
			else
			{
				try
				{
					// ******************** NEEDS FIXING *********************************
					// Currently doesn't disconnect immediately; waits for timeout.
					// *******************************************************************
					if (IMWindow.this.getSession() instanceof IMServer)
						IMWindow.this.getSession().getServer().close();
					else if (IMWindow.this.getSession() instanceof IMClient)
						IMWindow.this.getSession().stop();

				}	// End of try
				catch (IOException e) {}
				catch (NullPointerException e) {}
			}	// End of else

			IMWindow.this.setSession(null);
		}	// End of method actionPerformed
	}	// End of class ConnectMenuListener
}	// End of class IMWindow
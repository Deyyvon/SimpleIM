// ConnectionWindow.java
// GUI to configure connection.
// Devon Bautista

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Represents an instance of the connection
 * configuration window.
 **/
public class ConnectionWindow extends JFrame
{
	// Global widgets
	JRadioButton rdoServer;
	JRadioButton rdoClient;
	JButton btnOkay;
	JButton btnCancel;
	JTextField txtIPOfServer;
	JTextField txtPortOfServer;
	JTextField txtPortToHost;
	JPanel panConfig;
	JPanel panServerConfig;
	JPanel panClientConfig;
	IMWindow win;

	// Constants
	private int FRAME_HEIGHT = 400;
	private int FRAME_WIDTH = 200;

	/**
	 * Sets up JFrame and constructs widgets.
	 * @param parent The IMWindow this window is bound to.
	 **/
	public ConnectionWindow(IMWindow parent)
	{
		// Set this window's parent
		this.win = parent;

		// Window specifics
		this.setTitle("New Connection");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(FRAME_HEIGHT, FRAME_WIDTH);
		this.setVisible(true);
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);

		// Create widgets
		this.createWidgets();

		// Refresh window
		this.revalidate();
	}	// End of constructor

	/**
	 * Creates all widgets on connection window.
	 **/
	private void createWidgets()
	{
		// "This application is the:" titled panel
		JPanel panID = new JPanel();							// Create section panel
		panID.setBorder(BorderFactory.createTitledBorder(
				"This application is the:"));					// Set title around section panel
		rdoClient = new JRadioButton("Client");					// Create client radiobutton
		rdoServer = new JRadioButton("Server");					// Create server radiobutton
		rdoClient.addActionListener(new RadioButtonListener());	// Add event handler to client radiobutton
		rdoServer.addActionListener(new RadioButtonListener());	// Add event handler to server radiobutton
		ButtonGroup group = new ButtonGroup();					// Create button group
		group.add(rdoClient);									// Add client radiobutton to group
		group.add(rdoServer);									// Add server radiobutton to group
		rdoServer.setSelected(true);							// Set server radiobutton as selected
		panID.add(rdoServer);									// Add server radiobutton to section panel
		panID.add(rdoClient);									// Add client radiobutton to section panel

		// Client/Server configuration panel
		this.panConfig = new JPanel(new CardLayout());						// Create section panel that displays selected panel

		this.panServerConfig = new JPanel();								// Create server configuration panel
		this.txtPortToHost = new JTextField(15);							// Instantiate server port field
		JPanel panPortToHost = new JPanel(new GridLayout(1, 2));			// Create sub panel for text field
		panPortToHost.add(new JLabel("Port to Host:"));						// Add label to server configuration panel
		panPortToHost.add(txtPortToHost);									// Add text field to sub panel
		this.panServerConfig.add(panPortToHost);							// Add port field to server configuration panel

		this.panClientConfig = new JPanel();								// Create client configuration panel
		this.txtIPOfServer = new JTextField(15);							// Instantiate IP of server field
		this.txtPortOfServer = new JTextField(15);							// Instantiate port of server field
		JPanel panIPOfServer = new JPanel(new GridLayout(1, 2));			// Create a sub panel for row
		JPanel panPortOfServer = new JPanel(new GridLayout(1, 2));			// Create a sub panel for row
		panIPOfServer.add(new JLabel("IP of Server:"));						// Add label to client configuration panel
		panIPOfServer.add(txtIPOfServer);									// Add IP field to client configuration panel
		panPortOfServer.add(new JLabel("Port of Server:"));					// Add label to sub panel
		panPortOfServer.add(txtPortOfServer);								// Add port field to sub panel
		this.panClientConfig.add(panIPOfServer);							// Add sub panel to client configuration panel
		this.panClientConfig.add(panPortOfServer);							// Add sub panel to client configuration panel

		this.panClientConfig.setBorder(BorderFactory.createTitledBorder(
				"Client"));													// Create title for client configuration panel
		this.panServerConfig.setBorder(BorderFactory.createTitledBorder(
				"Server"));													// Create title for server configuration panel
		this.panConfig.add(panServerConfig);								// Add server configuration panel to section panel
		this.panConfig.add(panClientConfig);								// Add client configuration panel to section panel
		this.panConfig.add(panServerConfig, "SERVER");						// Add server configuration panel to card layout
		this.panConfig.add(panClientConfig, "CLIENT");						// Add client configuration panel to card layout

		// Buttons
		JPanel panButtons = new JPanel();									// Create section panel for buttons
		this.btnCancel = new JButton("Cancel");								// Create cancel button
		this.btnCancel.addActionListener(new CancelButtonListener());		// Add event handler to cancel button
		this.btnOkay = new JButton("OK");									// Create OK button
		this.btnOkay.addActionListener(new OKButtonListener());				// Add event listener to OK button
		panButtons.setLayout(new BoxLayout(panButtons,
			BoxLayout.LINE_AXIS));											// Set layout of section panel to be horizontal
		panButtons.add(Box.createHorizontalGlue());							// Add space in front of buttons
		panButtons.add(this.btnCancel);										// Add cancel button
		panButtons.add(Box.createRigidArea(new Dimension(10, 0)));			// Add space between cancel and OK buttons
		panButtons.add(this.btnOkay);										// Add OK button

		// Add section panels to main window
		this.add(panID);													// Add "This application is the:" titled panel
		this.add(panConfig);												// Add Client/Server configuration panel
		this.add(panButtons);												// Add buttons
	}	// End of method createWidgets

	//*******************************************************
	//* Event Handlers
	//*******************************************************
	/**
	 * Listens for when the "OK" button is clicked.
	 **/
	private class OKButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			// If Server selected, instantiate a server
			if (rdoServer.isSelected())
			{
				try
				{
					if (!ConnectionWindow.this.win.isConnected())
					{
						int intPortToHost = Integer.parseInt(ConnectionWindow.this.txtPortToHost.getText());
						ConnectionWindow.this.dispose();
						ConnectionWindow.this.win.setSession(new IMServer(intPortToHost, ConnectionWindow.this.win));
					}	// End of if
					else
					{
						// Display error dialog
						JOptionPane.showMessageDialog(ConnectionWindow.this, "Session already running.", "Error", JOptionPane.ERROR_MESSAGE);
					}	// End of else
				}
				catch (NumberFormatException e)
				{
					// Display error dialog
					JOptionPane.showMessageDialog(ConnectionWindow.this, "Invalid port.", "Error", JOptionPane.ERROR_MESSAGE);
				}	// End of catch
			}	// End of if
			// If Client selected, instantiate a client
			else if (rdoClient.isSelected())
			{
				try
				{
					if (!ConnectionWindow.this.win.isConnected() && !(ConnectionWindow.this.win.getSession() instanceof IMServer))
					{
						int intPortToHost = Integer.parseInt(ConnectionWindow.this.txtPortOfServer.getText());
						String IP = ConnectionWindow.this.txtIPOfServer.getText();
						ConnectionWindow.this.dispose();
						ConnectionWindow.this.win.setSession(new IMClient(IP, intPortToHost, ConnectionWindow.this.win));
					}	// End of if
					else
					{
						// Display error dialog
						JOptionPane.showMessageDialog(ConnectionWindow.this, "Server running on same port.", "Error", JOptionPane.ERROR_MESSAGE);
					}	// End of else
				}
				catch (NumberFormatException e)
				{
					// Display error dialog
					JOptionPane.showMessageDialog(ConnectionWindow.this, "Invalid port.", "Error", JOptionPane.ERROR_MESSAGE);
				}	// End of catch
			}	// End of else if
		}	// End of method actionPerformed
	}	// End of class RadioButtonListener

	/**
	 * Listens for when the "Cancel" button is clicked.
	 **/
	private class CancelButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			// Close window
			ConnectionWindow.this.dispose();
		}	// End of method actionPerformed
	}	// End of class CancelButtonListener

	/**
	 * Listens for selection of radiobuttons.
	 **/
	private class RadioButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			CardLayout cl = (CardLayout) (panConfig.getLayout());

			if (event.getSource().equals(rdoServer))
				cl.show(panConfig, "SERVER");
			else if (event.getSource().equals(rdoClient))
				cl.show(panConfig, "CLIENT");
		}	// End of method actionPerformed
	}	// End of class RadioButtonListener
}	// End of class ConnectionWindow
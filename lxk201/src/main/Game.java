package main;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.*;

import org.eclipse.swt.widgets.Display;

import protocol.*;
import protocol.Action.ActionBox;

public final class Game {
	
	public static Display display = new Display();
	
	public static InetAddress ServerIP;
	public static int Port;
	
	public static boolean Loggedin = false;
	public static int myID = -1;
	public static String myNickname = "";
	public static boolean inMatch = false;
	
	public static MessageRetriever messageRetriever;
	public static ActionRetriever actionRetriever;

	public static void startMatch (int oID, int mID, boolean moveFirst) {
		display.syncExec(new Runnable () {
			public void run(){
				Match.startMatch(oID, mID, moveFirst);
			}
		});
	}
	
	public static MainWindow.Friend findFriend (int id) {
		for (int i = 0; i < MainWindow.friends.size(); i++){
			if (MainWindow.friends.get(i).ID == id) return MainWindow.friends.get(i);
		}
		return null;
	}
	
	public static void openLoginWindow() {
		
		JFrame frame = new JFrame("Login Window");
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel usernameLabel = new JLabel("Username:");
		JLabel passwordLabel = new JLabel("Password:");
		JTextField usernameField = new JTextField();
		JPasswordField passwordField = new JPasswordField();
		JButton login = new JButton("Login");
		JLabel serverLabel = new JLabel("Server Address:");
		JLabel portLabel = new JLabel("Port Number:");
		JTextField serverAddress = new JTextField("localhost");
		JTextField portNumber = new JTextField("10010");
		
		panel.setBackground(new Color(0, 100, 255));

		login.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {  
				if(usernameField.getText().length() == 0 || passwordField.getPassword().length == 0) {
					JOptionPane.showMessageDialog(frame, "Incomplete Fields");
					usernameField.setText("");
					passwordField.setText("");
					return;
				}
				try {
					ServerIP = InetAddress.getByName(serverAddress.getText());
					Port = Integer.valueOf(portNumber.getText());
				} catch (UnknownHostException e1) {
					JOptionPane.showMessageDialog(frame, "Check the server address or your internet connection.");
					usernameField.setText("");
					passwordField.setText("");
					return;
				} catch (NumberFormatException e2) {
					JOptionPane.showMessageDialog(frame, "Invalid port number input");
					usernameField.setText("");
					passwordField.setText("");
					return;
				}
				String username = usernameField.getText();
				String password = new String(passwordField.getPassword());
				NetClient.sendLogin(username, password);
				usernameField.setText("");
				passwordField.setText("");
				if (Game.Loggedin) {
					frame.removeAll(); 
					frame.setVisible(false);
				}
			}
		});
		
		frame.addWindowListener(new WindowAdapter () {
			@Override
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(frame, "Exit?") == 1)
					System.exit(0);
			}
		});
		
		usernameLabel.setBorder(BorderFactory.createEmptyBorder());
		usernameLabel.setBackground(new Color(0, 100, 255));
		passwordLabel.setBorder(BorderFactory.createEmptyBorder());
		passwordLabel.setBackground(new Color(0, 100, 255));
		serverLabel.setBorder(BorderFactory.createEmptyBorder());
		serverLabel.setBackground(new Color(0, 100, 255));
		portLabel.setBorder(BorderFactory.createEmptyBorder());
		portLabel.setBackground(new Color(0, 100, 255));
    
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridheight = 2;
		constraints.gridwidth = 1;
		panel.add(usernameLabel, constraints);
    
		constraints.gridy = 2;
		constraints.gridheight = 1;
		constraints.gridwidth = 5;
		panel.add(usernameField, constraints);
    
		constraints.gridy = 3;
		constraints.gridheight = 2;
		constraints.gridwidth = 1;
		panel.add(passwordLabel, constraints);
    
		constraints.gridy = 5;
		constraints.gridheight = 1;
		constraints.gridwidth = 3;
		panel.add(passwordField, constraints);
		
		constraints.gridy = 6;
		constraints.gridheight = 2;
		constraints.gridwidth = 1;
		panel.add(serverLabel, constraints);
    
		constraints.gridy = 8;
		constraints.gridheight = 1;
		constraints.gridwidth = 3;
		panel.add(serverAddress, constraints);
		
		constraints.gridy = 9;
		constraints.gridheight = 2;
		constraints.gridwidth = 1;
		panel.add(portLabel, constraints);
    
		constraints.gridy = 11;
		constraints.gridheight = 1;
		constraints.gridwidth = 3;
		panel.add(portNumber, constraints);
    
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.gridx = 2;
		constraints.gridy = 12;
		constraints.gridwidth = 1;
		panel.add(login, constraints);
    
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 400);
		frame.setLocation(0, 0);
		frame.setVisible(true);

		while(!Loggedin) {
			try {Thread.sleep(1);} 
			catch (InterruptedException e) {e.printStackTrace();}
		}
		new MainWindow(myNickname);
		messageRetriever = new MessageRetriever();
		messageRetriever.start();
		
	}

	public static void main(String[] args) {
		
		if (args.length > 0 && args[0].equals("-test")) {
			NetClient.startNetClient();
			runServer();
			openLoginWindow();
			while (Loggedin)
				if (!display.readAndDispatch()) display.sleep();
			display.close();
			NetClient.close();
		}
		else {
			NetClient.startNetClient();
			openLoginWindow();
			while (Loggedin)
				if (!display.readAndDispatch()) display.sleep();
			display.close();
			NetClient.close();
		}
		
	}
	
	public static void runServer() {
		new Thread() {
			@Override
			public void run() {
				try {
					server.Server.main("-test".split(" "));
				} catch (IOException e) {e.printStackTrace();}
			}
		}.start();
	}
	
	public final static class MessageRetriever extends Thread {
		@Override
		public void run () {
			while (Loggedin) {

				Retrieve r = new Retrieve(myID);
				String response = NetClient.send(r.toString());
				Message.MessageBox mb = new Message.MessageBox(response);
				mb.clientParse();
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		
	}
	
	public final static class ActionRetriever extends Thread {
		public int matchNum;
		
		public ActionRetriever(int n) {
			matchNum = n;
		}
		
		@Override
		public void run () {
			while (Game.inMatch) {
				Retrieve r = new Retrieve(matchNum, myID);
				String response = NetClient.send(r.toString());
				ActionBox ab = new ActionBox(response);
				display.syncExec(new Runnable () {
					@Override
					public void run(){
						ab.clientParse();
					}
				});
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}
	//new content below!
	public static void win(int id, boolean isTesting) {
		if(isTesting)
			return;
		DatabaseAccessor x = new DatabaseAccessor();
		x.win(new Integer(id).toString());
		
	}
	
	public static void lose(int id, boolean isTesting) {
		if(isTesting)
			return;
		DatabaseAccessor x = new DatabaseAccessor();
		x.loss(new Integer(id).toString());
	}
	
	public static void updateCurrency(String id, String gold, String dust,boolean isTesting) {
		if(isTesting)
			return;
		DatabaseAccessor x = new DatabaseAccessor();
		x.updateCurrency(id,gold,dust);
	}
	
	public static void addCard(String id, String c_id, boolean isTesting) {
		if(isTesting)
			return;
		DatabaseAccessor x = new DatabaseAccessor();
		x.addCard(id, c_id);
	}
	
	public static String findUser(String id, boolean isTesting) {
		if(isTesting)
			return null;
		DatabaseAccessor x = new DatabaseAccessor();
		return x.findUser(id);
	}
	
	public static String findFriends(String id,boolean isTesting ) {
		if(isTesting)
			return null;
		DatabaseAccessor x = new DatabaseAccessor();
		return x.findFriends(id);
	}
	
	public static void specialPractice(String id, String c_id, boolean isTesting) {
		if(isTesting)
			return;
		DatabaseAccessor x = new DatabaseAccessor();
		x.specialPractice(id, c_id);
	}
	
	public static String getAllCardInfo(String id, boolean isTesting) {
		if(isTesting){
			return null;
		}		
		return new DatabaseAccessor().getAllCardInfo(id);
	}
	
	public static String[] getCardInfo(String id, String c_id, boolean isTesting) {
		if(isTesting)
			return null;
		
		return new DatabaseAccessor().getCardInfo(id, c_id);
	}
	
	public static void addFriend(String id, boolean isTesting) {
		if(isTesting)
			return;
		DatabaseAccessor x= new DatabaseAccessor();
		try {
		x.addFriend(new Integer(Game.myID).toString(),id);
		}
		catch (NumberFormatException e) {
			return;
		}
	}
	
	public static void removeFriend(String id, boolean isTesting) {
		if(isTesting)
		return;
	DatabaseAccessor x= new DatabaseAccessor();
	try {
	x.removeFriend(new Integer(Game.myID).toString(),id);
	}
	catch(NumberFormatException e) {
		return;
	}
	}
}



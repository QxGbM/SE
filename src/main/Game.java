package main;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.eclipse.swt.widgets.Display;

import protocol.*;
import protocol.Action.ActionBox;

public final class Game {
	
	public static Display display = new Display();
	
	public static final String ServerIP = "localhost";
	public static final int Port = 10010;
	
	public static boolean Loggedin = false;
	public static boolean Logout = false;
	public static int myID = -1;
	public static String myNickname = "";
	
	public static boolean inMatch = false;

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
    
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.gridx = 2;
		constraints.gridy = 6;
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
		new MessageRetriever().start();
	}

	public static void main(String[] args) {
		NetClient.startNetClient();
		
		MainWindow.friends.add(new MainWindow.Friend(101, "Tester1", true, ""));
		new Message(101, 100, 1000).clientParse();
		
		openLoginWindow();
		
		while (Loggedin)
			if (!display.readAndDispatch()) display.sleep();
		
		display.close();
		NetClient.close();
	}
	
	public final static class MessageRetriever extends Thread {
		@Override
		public void run () {
			while (Loggedin) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {e.printStackTrace();}
				Retrieve r = new Retrieve(myID);
				NetClient.send(r.toString());
				String response = NetClient.get();
				Message.MessageBox mb = new Message.MessageBox(response);
				mb.clientParse();
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
			while (inMatch) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {e.printStackTrace();}
				Retrieve r = new Retrieve(matchNum, myID);
				NetClient.send(r.toString());
				ActionBox ab = new ActionBox(NetClient.get());
				display.syncExec(new Runnable () {
					@Override
					public void run(){
						ab.clientParse();
					}
				});
			}
		}
	}
}



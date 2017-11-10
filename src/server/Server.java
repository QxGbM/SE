package server;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import protocol.*;

public final class Server {
	
	public static final int port = 10010;
	
	public static DatagramSocket ds = null;
	public static DatagramPacket sendDp = null;
	public static DatagramPacket receiveDp = null;
	
	public static int LastMatch = 1000;
	
	public static JTextArea serverLog = new JTextArea();
	
	public static final class User {
		
		public int ID;
		public String Nickname;
		
		private String username;
		private String password;
		
		public boolean Online = false;
		public ArrayList<User> friends = new ArrayList<User>();
		public ArrayList<Message> MessageBuffer = new ArrayList<Message>();
		
		public User(int id, String nickname, String un, String pw) {
			ID = id; Nickname = nickname;
			username = un; password = pw;
		}
		
		public boolean login(String un, String pw) {
			return Online = username.equals(un) && password.equals(pw);
		}
		
		public String toString() {
			return "User " + ID + " " + Nickname + " " + Online; 
		}
	}

	
	public static final class Match {
		public int player1;
		public int player2;
		public int matchID;
		
		public ArrayList<Action> ActionBuffer1 = new ArrayList<Action>();
		public ArrayList<Action> ActionBuffer2 = new ArrayList<Action>();
		
		public Match(int mid, int p1, int p2) {
			matchID = mid; player1 = p1; player2 = p2;
		}

		public void writeStart() {
			Random r = new Random();
			Boolean b = r.nextBoolean();
			ActionBuffer1.add(new Action(matchID, player2, b));
			ActionBuffer2.add(new Action(matchID, player1, !b));
		}
		
		public void close() {
			ActionBuffer1.add(new Action(matchID, player2, "close"));
			ActionBuffer2.add(new Action(matchID, player1, "close"));
			
			new Thread() {
				@Override
				public void run() {
					try {
						sleep(10000);
					} catch (InterruptedException e) {e.printStackTrace();}
					matches.remove(this);
				}
			}.start();
		}
	}
	
	public static final ArrayList<User> users = new ArrayList<User>();
	public static final ArrayList<Match> matches = new ArrayList<Match>();
	
	public static User findUser(int id) {
		for (int i = 0; i < users.size(); i++){
			if (users.get(i).ID == id) return users.get(i);
		}
		return null;
	}
	
	public static Match findMatch(int id) {
		for (int i = 0; i < matches.size(); i++){
			if (matches.get(i).matchID == id) return matches.get(i);
		}
		return null;
	}
	
	public static int createMatch(int player1, int player2) {
		LastMatch++;
		Match m = new Match (LastMatch, player1, player2);
		matches.add(m);
		return LastMatch;
	}
	
	public static Response parseCommand(String s) {
		String[] args = s.split(" ", 2);
		if(args[0].equals("Login")) {
			Login l = new Login(s);
			l.serverProcess();
			return l.loginResult;
		} 
		else if (args[0].equals("Message")) {
			Message m = new Message(s);
			m.serverProcess();
			return new ACK(m.matchID);
		}
		else if (args[0].equals("Action")) {
			new Action(s).serverProcess();
			return new ACK();
		}
		else if (args[0].equals("Retrieve")) {
			Retrieve r = new Retrieve(s);
			r.serverProcess();
			return r.retrievalResult;
		}
		else return new ACK("ERR Command Unknown");
	}
	
	public static void readwrite() throws IOException {
		
		if(serverLog.getText().length() >= 10000) serverLog.setText("");
		
		byte[] b = new byte[1024];
		receiveDp = new DatagramPacket(b, b.length);
		ds.receive(receiveDp);
		
		InetAddress clientIP = receiveDp.getAddress();
		int clientPort = receiveDp.getPort();
		byte[] data = receiveDp.getData();
		int len = receiveDp.getLength();
		serverLog.append("received: " + new String(data, 0, len) + "\n");
		
		String response = parseCommand(new String(data, 0, len)).toString();
		byte[] bData = response.getBytes();
		sendDp = new DatagramPacket(bData, bData.length, clientIP, clientPort);
		ds.send(sendDp);
		serverLog.append("returned: " + response + "\n\n");
	}
	
	public static void main(String args[]) throws IOException {
		
		ds = new DatagramSocket(port);
		serverLog.setEditable(false);
		
		User server = new User(0, "Server", "", "");
		User tester0 = new User(100, "Tester0", "admin0", "password");
		User tester1 = new User(101, "Tester1", "admin1", "password");
		users.add(server);
		users.add(tester0);
		users.add(tester1);
		tester0.friends.add(tester1);
		tester0.friends.add(server);
		tester1.friends.add(tester0);
		tester1.friends.add(server);
		
		Match m = new Match(1000, 100, 101);
		matches.add(m);
		
		new Thread() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Server Panel");
				JButton button = new JButton("Terminate");
				JPanel panel = new JPanel(new GridBagLayout());
				panel.setBorder(new EmptyBorder(5,5,5,5));
				button.addActionListener(new ActionListener () {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						frame.dispose();
						System.exit(0);
					}
				});
				
				JScrollPane scrollpane = new JScrollPane(serverLog);
				
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.BOTH;
				constraints.weightx = 1;
				constraints.weighty = 3;
				constraints.gridx = 1;
				constraints.gridy = 1;
				panel.add(scrollpane, constraints);
				constraints.weighty = 1;
				constraints.gridy = 2;
				panel.add(button, constraints);
				frame.add(panel);
				frame.setSize(500, 500);
				frame.setVisible(true);
			}
		}.start();
		
		new Thread() {
			@Override
			public void run() {
				test.ActionMocker.main(null);
			}
		}.start();

		
		while(true) {
			readwrite();
		}
	}
}
package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;

import protocol.*;

public final class Server {
	
	public static final int port = 10010;
	
	public static DatagramSocket ds = null;
	public static DatagramPacket sendDp = null;
	public static DatagramPacket receiveDp = null;
	
	public static int LastMatch = 1000;
	
	public static final class User {
		
		public int ID;
		public String Nickname;
		public boolean Online;
		public ArrayList<User> friends = new ArrayList<User>();
		public ArrayList<Message> MessageBuffer = new ArrayList<Message>();
		
		private String username;
		private String password;
		
		public User(int id, String nickname, String un, String pw) {
			ID = id; Nickname = nickname; Online = false;
			username = un; password = pw;
		}
		
		public boolean login(String un, String pw) {
			if (username.equals(un) && password.equals(pw)) Online = true;
			return Online;
		}
		
		public String toString() {
			return "3 " + ID + " " + Nickname + " " + Online; 
		}
		
		public String getFriendsBox() {
			String friendbox = "3 friendbox " + friends.size();
			for (int i = 0; i < friends.size(); i++){
				friendbox += " " + friends.get(i).toString();
			}
			return friendbox;
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
			matches.add(this);
			new Message(player1, player2, matchID).serverProcess();
		}

		public void writeStart() {
			Random r = new Random();
			Boolean b = r.nextBoolean();
			ActionBuffer1.add(new Action(matchID, player2, b));
			ActionBuffer2.add(new Action(matchID, player1, !b));
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
	
	public static String login(String username, String password) {
		for (int i = 0; i < users.size(); i++){
			if (users.get(i).login(username, password)) 
				return "4 login successful " + users.get(i).ID + " " + users.get(i).Nickname;
		}
		return "3 login error Incorrect username or password";
	}
	
	public static String parseCommand(String s) {
		String[] args = s.split(" ", 2);
		if(args[0].equals("Login")) {
			return login (args[2], args[3]);
		} 
		else if (args[0].equals("Message")) {
			new Message(s).serverProcess();
			return "message received";
		}
		else if (args[0].equals("Action")) {
			new Action(s).serverProcess();
			return "action received";
		}
		else if (args[0].equals("retrieve")) {
			Retrieve r = new Retrieve(s);
			r.serverProcess();
			return r.retrievalResult;
		}
		else return "general error Command not recognized.";
	}
	
	public static String readwrite() throws IOException{
		byte[] b = new byte[1024];
		receiveDp = new DatagramPacket(b, b.length);
		ds.receive(receiveDp);
		
		InetAddress clientIP = receiveDp.getAddress();
		int clientPort = receiveDp.getPort();
		byte[] data = receiveDp.getData();
		int len = receiveDp.getLength();
		
		String response = parseCommand(new String(data, 0, len));
		byte[] bData = response.getBytes();
		sendDp = new DatagramPacket(bData, bData.length, clientIP, clientPort);
		ds.send(sendDp);
		return response;
	}
	
	public static void main(String args[]) throws IOException {
		
		ds = new DatagramSocket(port);
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
		
		new Thread() {
			@Override
			public void run() {
				JFrame frame = new JFrame("server panel");
				JButton button = new JButton("Terminate");
				button.addActionListener(new ActionListener () {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						frame.dispose();
						System.exit(0);
					}
				});
				frame.add(button);
				frame.pack();
				frame.setVisible(true);
			}
		}.start();
		
		while(true) {
			System.out.println(readwrite());
		}
	}
}
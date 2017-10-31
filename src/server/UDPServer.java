package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;

import protocol.Message;

public final class UDPServer {
	
	public static final int port = 10010;
	
	public static DatagramSocket ds = null;
	public static DatagramPacket sendDp = null;
	public static DatagramPacket receiveDp = null;
	
	public static int nextMatch = 1000;
	
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
		
		public String getMessageBox() {
			String messagebox =  "3 messagebox " + MessageBuffer.size();
			while (MessageBuffer.size() > 0){
				messagebox += " " + MessageBuffer.remove(0).toString();
			}
			return messagebox;
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
		
		public Match(int mid, String p1, String p2) {
			matchID = mid; player1 = Integer.valueOf(p1); player2 = Integer.valueOf(p2);
			matches.add(this);
			new Message(player1, player2, matchID).intoMessageBox();
		}
		
		public String getAction(int id) {
			if (id == player1) {
				String actionbox =  "3 actionbox " + ActionBuffer1.size();
				while (ActionBuffer1.size() > 0){
					actionbox += " " + ActionBuffer1.remove(0).toString();
				}
				return actionbox;
			}
			else if (id == player2) {
				String actionbox =  "3 actionbox " + ActionBuffer2.size();
				while (ActionBuffer2.size() > 0){
					actionbox += " " + ActionBuffer2.remove(0).toString();
				}
				return actionbox;
			}
			return "3 actionbox 0";
		}

		public void writeStart() {
			Random r = new Random();
			Boolean b = r.nextBoolean();
			new Action(matchID, player2, "2 start " + b);
			ActionBuffer1.add(new Action(matchID, player2, "2 start " + b));
			ActionBuffer2.add(new Action(matchID, player1, "2 start " + !b));
		}
	}
	
	public static final class Action {
		public int match;
		public int player;
		public String action;
		
		public Action(String s) {
			int n = Integer.valueOf(s.split(" ", 2)[0]);
			String[] args = s.split(" ", n + 1);
			match = Integer.valueOf(args[1]);
			player = Integer.valueOf(args[2]);
			action = args[3];
		}
		
		public Action(int m, int p, String a) {
			match = m; player = p; action = a;
		}
		
		public String toString() {
			return "3 " + match + " " + player + " " + action;
		}
		
		public void intoActionBox() {
			Match m = findMatch(match);
			if(m.player1 == player) m.ActionBuffer1.add(this);
			if(m.player2 == player) m.ActionBuffer2.add(this);
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
	
	public static String login(String username, String password) {
		for (int i = 0; i < users.size(); i++){
			if (users.get(i).login(username, password)) 
				return "4 login successful " + users.get(i).ID + " " + users.get(i).Nickname;
		}
		return "3 login error Incorrect username or password";
	}
	
	public static String parseCommand(String s) {
		int n = Integer.valueOf(s.split(" ", 2)[0]);
		String[] args = s.split(" ", n + 1);
		if(args[1].equals("login")) {
			// "3 login USERNAME PASSWORD"
			return login (args[2], args[3]);
		} 
		else if (args[1].equals("message")) {
			// "2 message BODY" BODY = "3 FROMID TOID MESSAGE"
			new Message(args[2]).intoMessageBox();
			return "2 message received";
		}
		else if (args[1].equals("match")) {
			if (args[2].equals("request")) {
				// "4 match request PLAYER1 PLAYER2"
				new Match(nextMatch, args[3], args[4]);
				nextMatch++;
				return "3 match accepted " + (nextMatch-1);
			}
			else if (args[2].equals("accept")) {
				// "3 match accept MATCHID"
				Match m = findMatch(Integer.valueOf(args[3]));
				m.writeStart();
				return "3 match accepted " + m.matchID;
			}
			else
				return "2 match unknown";
		}
		else if (args[1].equals("action")) {
			// "2 action BODY"
			new Action(args[2]).intoActionBox();
			return "2 action received";
		}
		else if (args[1].equals("retrieve")) {
			if (args[2].equals("message")) {
				// "3 retrieve message PLAYERID"
				return findUser(Integer.valueOf(args[3])).getMessageBox();
			}
			else if (args[2].equals("action")) {
				// "3 retrieve action MATCHID PLAYERID"
				return findMatch(Integer.valueOf(args[3])).getAction(Integer.valueOf(args[4]));
			}
			else if (args[2].equals("friends")) {
				// "3 retrieve friends PLAYERID"
				return findUser(Integer.valueOf(args[3])).getFriendsBox();
			}
			else 
				return "3 retrieve error Unknown retrieval type";
		}
		else return "3 general error Command not recognized.";
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
				JButton button = new JButton("shutoff");
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
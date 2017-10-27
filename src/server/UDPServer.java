package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

public final class UDPServer {
	
	private DatagramSocket ds = null;
	private DatagramPacket sendDp = null;
	private DatagramPacket receiveDp = null;
	private int port = 10010;
	
	private int nextMatch = 1000;
	
	private final static class User{
		int ID;
		String Nickname;
		boolean Online;
		ArrayList<User> friends = new ArrayList<User>();
		ArrayList<String> MessageBuffer = new ArrayList<String>();
		
		public User(int id, String nickname, boolean online) {
			ID = id; Nickname = nickname; Online = online;
		}
		
		public void putMessage(String from, String to, String length, String message) {
			String s = "4 " + from + " " + to + " " + length + " " + message;
			MessageBuffer.add(s);
		}
		
		public String getMessage() {
			String messagebox =  "3 messagebox " + MessageBuffer.size();
			while (MessageBuffer.size() > 0){
				messagebox += " " + MessageBuffer.remove(0);
			}
			return messagebox;
		}
		
		public String getFriends() {
			String friendbox = "3 friendbox " + friends.size();
			for (int i = 0; i < friends.size(); i++){
				friendbox += " 3 " + friends.get(i).ID + " " + friends.get(i).Nickname + " " + friends.get(i).Online;
			}
			return friendbox;
		}
	}
	
	private final static class Match{
		int player1;
		int player2;
		int ID;
		ArrayList<String> ActionBuffer1 = new ArrayList<String>();
		ArrayList<String> ActionBuffer2 = new ArrayList<String>();
		
		public Match(int id, int p1, int p2) {
			ID = id; player1 = p1; player2 = p2;
		}
		
		public void putAction(int id, String action) {
			if (id == player2)
				ActionBuffer1.add(action);
			else if (id == player1)
				ActionBuffer2.add(action);
		}
		
		public String getAction(int id) {
			if (id == player1) {
				String actionbox =  "3 actionbox " + Integer.toString(ActionBuffer1.size());
				while (ActionBuffer1.size() > 0){
					actionbox += " " + ActionBuffer1.remove(0);
				}
				return actionbox;
			}
			else if (id == player2) {
				String actionbox =  "3 actionbox " + Integer.toString(ActionBuffer2.size());
				while (ActionBuffer2.size() > 0){
					actionbox += " " + ActionBuffer2.remove(0);
				}
				return actionbox;
			}
			return "3 actionbox 0";
		}

		public void writeStart() {
			Random r = new Random();
			Boolean b = r.nextBoolean();
			ActionBuffer1.add("4 start " + player2 + " " + b);
			ActionBuffer2.add("4 start " + player1 + " " + !b);
		}
	}
	
	private ArrayList<User> users = new ArrayList<User>();
	private ArrayList<Match> matches = new ArrayList<Match>();
	
	private User findUser(int id) {
		for (int i = 0; i < users.size(); i++){
			if (users.get(i).ID == id) return users.get(i);
		}
		return null;
	}
	
	private Match findMatch(int id) {
		for (int i = 0; i < matches.size(); i++){
			if (matches.get(i).ID == id) return matches.get(i);
		}
		return null;
	}
	
	public UDPServer() throws IOException {
		ds = new DatagramSocket(port);
	}
	
	private String parseCommand(String command) {
		int n = Integer.valueOf(command.split(" ", 2)[0]);
		String[] args = command.split(" ", n + 1);
		if(args[1].equals("login")) {
			if(args[2].equals("admin0") && args[3].equals("password")) {
				findUser(100).Online = true;
				return "4 login successful 100 Tester0";
			}
			else if (args[2].equals("admin1") && args[3].equals("password")) {
				findUser(101).Online = true;
				return "4 login successful 101 Tester1";
			}
			else
				return "3 login error Incorrect username or password";
		} 
		else if (args[1].equals("message")) {
			if(args[2].equals("0")) {
				String length = Integer.toString(Integer.valueOf(args[4]) + 30);
				String message = "Server received your message: " + args[5];
				findUser(Integer.valueOf(args[2])).putMessage(args[3], args[2], length, message);
			}
			else {
				findUser(Integer.valueOf(args[2])).putMessage(args[3], args[2], args[4], args[5]);
			}
			return "2 message received";
		}
		else if (args[1].equals("match")) {
			if (args[2].equals("request")) {
				matches.add(new Match(nextMatch, Integer.valueOf(args[3]), Integer.valueOf(args[4])));
				findUser(Integer.valueOf(args[4])).putMessage(args[3], args[4], "br", Integer.toString(nextMatch));
				nextMatch++;
				return "3 match pending " + (nextMatch-1);
			}
			else if (args[2].equals("accept")) {
				Match m = findMatch(Integer.valueOf(args[3]));
				m.writeStart();
				return "3 match accepted " + m.ID;
			}
			else {
				findMatch(Integer.valueOf(args[2])).putAction(Integer.valueOf(args[3]),args[4]);
				return "2 action received";
			}
			
		}
		else if (args[1].equals("retrieve")) {
			if (args[2].equals("message")) {
				return findUser(Integer.valueOf(args[3])).getMessage();
			}
			else if (args[2].equals("action")) {
				return findMatch(Integer.valueOf(args[3])).getAction(Integer.valueOf(args[4]));
			}
			else 
				return "3 retrieve error Unknown retrieval type";
		}
		else return "3 general error Command not recognized.";
	}
	
	private String readwrite() throws IOException{
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
		UDPServer s = new UDPServer();
		User server = new User(0, "Server", true);
		User tester0 = new User(100, "Tester0", false);
		User tester1 = new User(101, "Tester1", false);
		s.users.add(server);
		s.users.add(tester0);
		s.users.add(tester1);
		tester0.friends.add(tester1);
		tester0.friends.add(server);
		tester1.friends.add(tester0);
		tester1.friends.add(server);
		while(true) {
			try {
				System.out.println(s.readwrite());
			} catch (IOException e) {e.printStackTrace();}
		}
	}
}
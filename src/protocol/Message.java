package protocol;

import java.util.ArrayList;
import java.util.Date;

import main.Game;
import server.Server;
import server.Server.User;

public class Message implements Request, Response {
	
	public final String type = "Message";
	
	public int fromID = 0;
	public int toID = 0;
	public boolean isBattleRequest = false;
	public String message = "";
	public int length = 0;
	public int matchID = 0;
	public String remaining = "";
	
	public Message(String s) {
		String[] args = s.split(" ");
		fromID = Integer.valueOf(args[1]);
		toID = Integer.valueOf(args[2]);
		int i = 3;
		if(args[3].equals("BattleRequest")) {
			isBattleRequest = true;
			message = args[4];
			matchID = Integer.valueOf(args[5]);
			i = 6;
		}
		else {
			length = Integer.valueOf(args[3]);
			i = 4;
			for (int j = i; j < i + length; j++) {
				if (j != i) message += " ";
				message += args[j];
			}
			i += length;
		}
		while (i < args.length) {
			remaining += args[i];
			i++;
			if (i != args.length) remaining += " ";
		}
	}
	
	public Message(int from, int to) {
		// Client side Initial Battle Request
		fromID = from; toID = to;
		isBattleRequest = true;
		message = "Request";
	}
	
	public Message(int from, int to, int matchID) {
		// Server side Battle Request
		fromID = from; toID = to;
		isBattleRequest = true;
		this.matchID = matchID;
	}
	
	public Message(int from, int matchID, boolean accept) {
		// Client side Battle Request w/ accept or reject
		fromID = from;
		isBattleRequest = true;
		this.matchID = matchID;
		if (accept) message = "Accept"; 
		else message = "Reject";
	}
	
	public Message(int from, int to, String message) {
		// General Message
		fromID = from; toID = to; this.message = message;
		length = message.length() - message.replace(" ", "").length() + 1;
		isBattleRequest = false;
		remaining = "";
	}
	
	public String toString() {
		if (isBattleRequest) 
			return type + " " + fromID + " " + toID + " BattleRequest " + message + " " + matchID;
		else 
			return type + " " + fromID + " " + toID + " " + length + " " + message;
	}
	
	public void serverProcess() {
		User i = Server.findUser(toID);
		if (isBattleRequest) {
			if (message.equals("Request")) {
				matchID = Server.createMatch(fromID, toID);
				System.out.println(matchID + " is created");
				i.MessageBuffer.add(this);
			}
			else if (message.equals("Accept")) {
				Server.findMatch(matchID).writeStart();
			}
			else if (message.equals("Reject")) {
				Server.findMatch(matchID).close();
			}
		}
		else i.MessageBuffer.add(this);
	}
	
	public void clientParse() {
		if (isBattleRequest) {
			Game.findFriend(fromID).BattleRequest = true;
			Game.findFriend(fromID).matchNum = matchID;
		}
		else {
			Game.findFriend(fromID).appendMessage(message, new Date());
		}
	}
	
	public static class MessageBox implements Response {
		
		public final String type = "MessageBox";
		public int length;
		public Message[] args;
		
		public MessageBox(String response) {
			String[] l = response.split(" ", 3);
			length = Integer.valueOf(l[1]);
			args = new Message[length];
			if (length > 0) {
				String s = l[2];
				for (int i = 0; i < length; i++) {
					args[i] = new Message(s);
					s = args[i].remaining;
				}
			}
		}
		
		public MessageBox(ArrayList<Message> Buffer) {
			length = Buffer.size(); 
			args = new Message[length];
			int i = 0;
			while (i < length) {
				args[i] = Buffer.remove(0);
				i++;
			}
		}
		
		public String toString() {
			String s = type + " " + length;
			for(int i = 0; i < length; i++) {
				s += " " + args[i].toString();
			}
			return s;
		}
		
		public void clientParse() {
			for (int i = 0; i < length; i++) {
				args[i].clientParse();
			}
		}
	}
}
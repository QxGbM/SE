package protocol;

import java.util.Date;

import main.Game;
import server.UDPServer;
import server.UDPServer.User;

public class Message {
	
	public int fromID;
	public int toID;
	public String message;
	public int length;
	public boolean isBattleRequest;
	public String remaining;
	
	public Message(String s) {
		String[] m = s.split(" ", 5);
		fromID = Integer.valueOf(m[1]);
		toID = Integer.valueOf(m[2]);
		if(m[3].equals("br")) {
			length = 0;
			isBattleRequest = true;
			message = m[4].split(" ", 2)[0];
			remaining = m[4].split(" ", 2)[1];
		}
		else {
			length = Integer.valueOf(m[3]);
			isBattleRequest = false;
			message = m[4].substring(0, length);
			if(m[4].length() > length)
				remaining = m[4].substring(length + 1);
			else 
				remaining = "";
		}
	}
	
	public Message(int from, int to, int matchID) {
		fromID = from; toID = to; message = Integer.toString(matchID);
		length = 0;
		isBattleRequest = true;
		remaining = "";
	}
	
	public Message(int from, int to, String me) {
		fromID = from; toID = to; message = me;
		length = message.length();
		isBattleRequest = false;
		remaining = "";
	}
	
	public String toString() {
		if (isBattleRequest) return "4 " + fromID + " " + toID + " br " + message;
		else return "4 " + fromID + " " + toID + " " + length + " " + message;
	}
	
	public void intoMessageBox() {
		User i = UDPServer.findUser(toID);
		i.MessageBuffer.add(this);
	}
	
	public void parse() {
		if (isBattleRequest) {
			Game.findFriend(fromID).BattleRequest = true;
			Game.findFriend(fromID).matchNum = Integer.valueOf(message);
		}
		else {
			Game.findFriend(fromID).appendMessage(message, new Date());
		}
	}
}

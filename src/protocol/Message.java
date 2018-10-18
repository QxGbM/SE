package protocol;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.Game;
import main.MainWindow;
import main.NetClient;
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
	
	public Message(int from, int to, int mode) {
		// Client side Initial Battle Request
		fromID = from; toID = to;
		isBattleRequest = true;
		if (mode == 1) message = "Quickmatch";
		else if (mode == 2) message = "Coop";
	}
	
	public Message(int from, int to, int matchID, String nickname) {
		// Server side Battle Request
		fromID = from; toID = to;
		isBattleRequest = true;
		message = nickname;
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
			if (message.equals("Quickmatch")) {
				matchID = Server.createMatch(fromID, toID);
				i.MessageBuffer.add(this);
			}
			else if (message.equals("Coop")) {
				matchID = Server.createCoop(fromID, toID);
				i.MessageBuffer.add(this);
			}
			else if (message.equals("Accept")) {
				if (matchID >= 1000 && matchID < 2000)
					Server.findMatch(matchID).writeStart();
				if (matchID >= 2000 && matchID < 3000)
					Server.findCoopMatch(matchID).writeStart();
			}
		}
		else i.MessageBuffer.add(this);
	}
	
	public void clientParse() {
		MainWindow.Friend friend = Game.findFriend(fromID);
		if (friend == null) {
			friend = new MainWindow.Friend(fromID, message, true, "");
			MainWindow.friends.add(friend);
		}
		if (isBattleRequest) {
			MainWindow.matched = true;
			
			JFrame frame = new JFrame("BattleRequest");
			JPanel panel = new JPanel(new BorderLayout());
			
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent arg0) {
					NetClient.sendBattleAccept(matchID, false);
					if (matchID >= 1000 && matchID < 2000)
						NetClient.sendAction(new Action(matchID, Game.myID, "reject"));
					if (matchID >= 2000 && matchID < 3000)
						NetClient.sendCoopAction(new CoopAction(matchID, Game.myID, "reject"));
					if (main.Match.shell == null || main.Match.shell.isDisposed())
						Game.inMatch = false;
					frame.dispose();
				}
				
			});
			
			JLabel text = new JLabel(message + " has request a match with you.");
			JButton accept = new JButton("Accept");
			JButton reject = new JButton("Reject");
			
			accept.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (Game.inMatch) {
						JOptionPane.showMessageDialog(frame, "You are in a Game!");
						return;
					}
					if (!MainWindow.deckIsReady) {
						JOptionPane.showMessageDialog(frame, "Finish building your deck first!");
						return;
					}
					NetClient.sendBattleAccept(matchID, true);
					Game.inMatch = true;
					frame.dispose();
				}
			});
			
			reject.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					NetClient.sendBattleAccept(matchID, false);
					if (matchID >= 1000 && matchID < 2000)
						NetClient.sendAction(new Action(matchID, Game.myID, "reject"));
					if (matchID >= 2000 && matchID < 3000)
						NetClient.sendCoopAction(new CoopAction(matchID, Game.myID, "reject"));
					if (main.Match.shell == null || main.Match.shell.isDisposed())
						Game.inMatch = false;
					frame.dispose();
				}
			});
			
			panel.add(text, BorderLayout.NORTH);
			panel.add(accept, BorderLayout.CENTER);
			panel.add(reject, BorderLayout.SOUTH);
			
			frame.add(panel);
			frame.setSize(300, 300);
			frame.setVisible(true);
		}
		else {
			friend.appendMessage(message, new Date());
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
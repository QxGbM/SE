package protocol;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import main.Game;
import main.NetClient;
import server.Server;
import server.Server.CoopMatch;

public class CoopAction implements Request, Response {
	public final String type = "CoopAction";
	
	public int matchID;
	public int playerID;
	public String actionType;
	
	public int intField1 = 0;
	public int intField2 = 0;
	public Boolean boolField1 = false;
	
	public String remaining = "";
	
	public CoopAction(String s) {
		String[] args = s.split(" ");
		matchID = Integer.valueOf(args[1]);
		playerID = Integer.valueOf(args[2]);
		actionType = args[3];
		int i = 4;
		if(actionType.equals("skill")) {
			intField1 = Integer.valueOf(args[4]);
			boolField1 = Boolean.valueOf(args[5]); 
			i = 6;
			if (boolField1) {intField2 = Integer.valueOf(args[6]); i = 7;}
		}
		if(actionType.equals("summon")) {
			intField1 = Integer.valueOf(args[4]);
			intField2 = Integer.valueOf(args[5]);
			i = 6;
		}
		if(actionType.equals("start")) {
			boolField1 = Boolean.valueOf(args[4]);
			i = 5;
		}
		while (i < args.length) {
			remaining += args[i];
			i++;
			if (i != args.length) remaining += " ";
		}
	}
	
	public CoopAction(int m, int p, String move) {
		// ball, end turn, surrender, close
		matchID = m; playerID = p; actionType = move;
	}
	
	public CoopAction(int m, int p, int x1, int y1, String location1, int x2, int y2, String location2) {
		// skill with target
		matchID = m; playerID = p; actionType = "skill";
		switch (location1) {
		case "Board": intField1 = x1 * 4 + y1 + 8; break;
		case "Friend": intField1 = x1 * 4 + y1; break;
		default: intField1 = 0;
		}
		switch (location2) {
		case "Board": intField2 = x2 * 4 + y2 + 8; break;
		case "Friend": intField2 = x2 * 4 + y2; break;
		case "Boss": intField2 = y2 + 16; break;
		default: intField2 = 0;
		}
		boolField1 = true;
	}
	
	public CoopAction(int m, int p, int x, int y, String location) {
		// skill without target
		matchID = m; playerID = p; actionType = "skill";
		switch (location) {
		case "Board": intField1 = x * 4 + y + 8; break;
		case "Friend": intField1 = x * 4 + y; break;
		default: intField1 = 0;
		}
		intField2 = 0; boolField1 = false;
	}
	
	public CoopAction(int m, int p, int mID, int x, int y, String location) {
		// summon
		matchID = m; playerID = p; actionType = "summon";
		intField1 = mID;
		switch (location) {
		case "Board": intField2 = x * 4 + y + 8; break;
		case "Friend": intField2 = x * 4 + y; break;
		default: intField2 = 0;
		}
		boolField1 = false;
	}
	
	public CoopAction(int m, int p, boolean host) {
		// start
		matchID = m; playerID = p; actionType = "start";
		intField1 = 0; intField2 = 0; boolField1 = host;
	}
	
	public String toString() {
		String s = type + " " + matchID + " " + playerID + " " + actionType;
		switch (actionType) {
		case "skill": s += " " + intField1 + " " + boolField1; if(boolField1) s += " " + intField2; break;
		case "summon": s += " " + intField1 + " " + intField2; break;
		case "start": s += " " + boolField1; break;
		default:
		}
		return s;
	}

	public void clientParse() {
		switch (actionType) {
		case "skill": 
			if (boolField1) Coop.Coop.friendSkillActivation(intField1, intField2);
			else Coop.Coop.friendSkillActivation(intField1);
			break;
		case "summon":
			Coop.Coop.friendSummon(intField1, intField2);
			break;
		case "start":
			Coop.Coop.startCoop(matchID, playerID, boolField1);
			break;
		case "reject":
			main.Game.inMatch = false;
			NetClient.sendCoopAction(new CoopAction(matchID, Game.myID, "close"));
			JOptionPane.showMessageDialog(null, "Opponent Rejected Match.");
			break;
		case "ball":
			Coop.Coop.friendPassBall();
			break;
		case "endturn":
			Coop.Coop.friendEndTurn();
			break;
		case "surrender":
			Coop.Coop.friendLeft();
			break;
		default:
		}
	}

	public void serverProcess() {
		CoopMatch m = Server.findCoopMatch(matchID);
		if(actionType.equals("close")) {m.close(); return;}
		if(m.player2 == playerID) m.ActionBuffer1.add(this);
		if(m.player1 == playerID) m.ActionBuffer2.add(this);

	}
		
	public static class CoopActionBox implements Response {
		
		public final String type = "CoopActionBox";
		public int length;
		public CoopAction[] args;
		
		public CoopActionBox(String response) {
			String[] l = response.split(" ", 3);
			length = Integer.valueOf(l[1]);
			args = new CoopAction[length];
			if (length > 0) {
				String s = l[2];
				for (int i = 0; i < length; i++) {
					args[i] = new CoopAction(s);
					s = args[i].remaining;
				}
			}
		}
		
		public CoopActionBox(ArrayList<CoopAction> Buffer) {
			length = Buffer.size(); 
			args = new CoopAction[length];
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

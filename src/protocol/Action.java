package protocol;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import server.Server;
import server.Server.Match;

public class Action implements Request, Response {
	public final String type = "Action";
	
	public int matchID;
	public int playerID;
	public String actionType;
	
	public int intField1 = 0;
	public int intField2 = 0;
	public Boolean boolField1 = false;
	
	public String remaining = "";
	
	public Action(String s) {
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
	
	public Action(int m, int p, String move) {
		matchID = m; playerID = p; actionType = move;
	}
	
	public Action(int m, int p, int x1, int y1, int x2, int y2) {
		// skill with target
		matchID = m; playerID = p; actionType = "skill";
		intField1 = (3-x1) * 4 + (3-y1); intField2 = (3-x2) * 4 + (3-y2); boolField1 = true;
	}
	
	public Action(int m, int p, int x, int y) {
		// skill without target
		matchID = m; playerID = p; actionType = "skill";
		intField1 = (3-x) * 4 + (3-y);
	}
	
	public Action(int m, int p, int mID, int x, int y) {
		// summon
		matchID = m; playerID = p; actionType = "summon";
		intField1 = mID; intField2 = (3-x) * 4 + (3-y);
	}
	
	public Action(int m, int p, boolean moveFirst) {
		matchID = m; playerID = p; actionType = "start";
		boolField1 = moveFirst;
	}
	
	public String toString() {
		String s = type + " " + matchID + " " + playerID + " " + actionType;
		if(actionType.equals("skill")) {
			s += " " + intField1 + " " + boolField1;
			if(boolField1) s += " " + intField2;
		}
		if(actionType.equals("summon")) {
			s += " " + intField1 + " " + intField2;
		}
		if(actionType.equals("start")) {
			s += " " + boolField1;
		}
		return s;
	}

	public void clientParse() {
		if(actionType.equals("start")) {
			main.Game.startMatch(playerID, matchID, boolField1);
		}
		else if (actionType.equals("endturn")) {
			main.Match.opponentEndTurn();
		}
		else if (actionType.equals("summon")) {
			main.Match.opponentSummon(this);
		}
		else if (actionType.equals("skill")) {
			main.Match.opponentSkillActivation(this);
		}
		else if (actionType.equals("surrender")) {
			main.Match.win();
		}
		else if (actionType.equals("close")) {
			main.Game.inMatch = false;
			JOptionPane.showMessageDialog(null, "Your opponent Left");
		}
		
	}

	public void serverProcess() {
		Match m = Server.findMatch(matchID);
		if(m.player2 == playerID) m.ActionBuffer1.add(this);
		if(m.player1 == playerID) m.ActionBuffer2.add(this);
		if(actionType.equals("close")) m.close();
	}
	
	public static class ActionBox implements Response {
		
		public final String type = "ActionBox";
		public int length;
		public Action[] args;
		
		public ActionBox(String response) {
			String[] l = response.split(" ", 3);
			length = Integer.valueOf(l[1]);
			args = new Action[length];
			if (length > 0) {
				String s = l[2];
				for (int i = 0; i < length; i++) {
					args[i] = new Action(s);
					s = args[i].remaining;
				}
			}
		}
		
		public ActionBox(ArrayList<Action> Buffer) {
			length = Buffer.size(); 
			args = new Action[length];
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
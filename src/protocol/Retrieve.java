package protocol;

import server.Server;
import server.Server.Match;
import server.Server.User;

public class Retrieve implements Request {
	
	public final String type = "Retrieve";
	
	public String arg1 = "";
	public int intField1 = 0;
	public int intField2 = 0;
	
	public Response retrievalResult;
	
	public Retrieve (String s) {
		String[] args = s.split(" ");
		arg1 = args[1];
		if (arg1.equals("MessageBox")) {
			intField1 = Integer.valueOf(args[2]);
		}
		if (arg1.equals("ActionBox")) {
			intField1 = Integer.valueOf(args[2]);
			intField2 = Integer.valueOf(args[3]);
		}
	}
	
	public Retrieve (int playerID) {
		// MessageBox
		arg1 = "MessageBox";
		intField1 = playerID;
	}
	
	public Retrieve (int playerID, int MatchID) {
		// ActionBox
		arg1 = "ActionBox";
		intField1 = playerID;
		intField2 = MatchID;
	}
	
	public String toString() {
		if (arg1.equals("MessageBox"))
			return type + " " + arg1 + " " + intField1;
		else if (arg1.equals("ActionBox"))
			return type + " " + arg1 + " " + intField1 + " " + intField2;
		else
			return "";
	}
	
	public void serverProcess() {
		if (arg1.equals("MessageBox")) {
			User i = Server.findUser(intField1);
			if (i != null)
				retrievalResult = new Message.MessageBox(i.MessageBuffer);
		}
		if (arg1.equals("ActionBox")) {
			Match i = Server.findMatch(intField1);
			if (i != null && i.player1 == intField2) {
				retrievalResult = new Action.ActionBox(i.ActionBuffer1);
			}
			if (i != null && i.player2 == intField2) {
				retrievalResult = new Action.ActionBox(i.ActionBuffer2);
			}
		}
	}

}

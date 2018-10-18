package protocol;

import server.Server;

public class QuickMatch implements Request{
	
	public final String type = "Quickmatch";
	public int ID;
	public String match;
	public String action;
	
	public QuickMatch(int ID, boolean b1, boolean b2) {
		this.ID = ID;
		if (b1) match = "quickmatch";
		else match = "coop";
		if (b2) action = "enter";
		else action = "leave";
	}
	
	public QuickMatch(String s) {
		String[] args = s.split(" ");
		ID = Integer.valueOf(args[1]);
		match = args[2];
		action = args[3];
	}
	
	public void serverProcess() {
		if (action.equals("enter")) {
			if (match.equals("quickmatch")) {
				Server.queue.add(ID);
			}
			if (match.equals("coop")) {
				Server.Coopqueue.add(ID);
			}
		}
		if (action.equals("leave")) {
			if (match.equals("quickmatch")) {
				for(int i = 0; i < Server.queue.size(); i++) {
					if (Server.queue.get(i).intValue() == ID)
						Server.queue.remove(i);
				}
			}
			if (match.equals("coop")) {
				for(int i = 0; i < Server.Coopqueue.size(); i++) {
					if (Server.Coopqueue.get(i).intValue() == ID)
						Server.Coopqueue.remove(i);
				}
			}
		}
	}
	
	public String toString() {
		return type + " " + ID + " " + match + " "+ action;
	}
}

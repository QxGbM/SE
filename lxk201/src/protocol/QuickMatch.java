package protocol;

import server.Server;

public class QuickMatch implements Request{
	
	public final String type = "Quickmatch";
	public int ID;
	public String action;
	
	public QuickMatch(int ID, boolean b) {
		this.ID = ID;
		if (b) action = "enter";
		else action = "leave";
	}
	
	public QuickMatch(String s) {
		String[] args = s.split(" ");
		ID = Integer.valueOf(args[1]);
		action = args[2];
	}
	
	public void serverProcess() {
		if (action.equals("enter")) {
			Server.queue.add(ID);
		}
		else if (action.equals("leave")) {
			for(int i = 0; i < Server.queue.size(); i++) {
				if (Server.queue.get(i).intValue() == ID)
					Server.queue.remove(i);
			}
		}
	}
	
	public String toString() {
		return type + " " + ID + " " + action;
	}
}

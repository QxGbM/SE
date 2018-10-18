package protocol;

import server.Server;

public class Login implements Request{
	public final String type = "Login";
	public String username;
	public String password;
	
	public LoginResult loginResult = new LoginResult();
	
	public Login (String un, String pw) {
		username = un; password = pw;
	}
	
	public Login (String s) {
		String[] args = s.split(" ");
		username = args[1];
		password = args[2];
	}
	
	public String toString() {
		return type + " " + username + " " + password;
	}
	
	public void serverProcess() {
		for (int i = 0; i < Server.users.size(); i++){
			if (Server.users.get(i).login(username, password)) {
				loginResult = new LoginResult(Server.users.get(i).ID, Server.users.get(i).Nickname);
				return;
			}
		}
	}
}

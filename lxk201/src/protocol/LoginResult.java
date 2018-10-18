package protocol;

import javax.swing.JOptionPane;

import main.Game;

public class LoginResult implements Response{
	
	public final String type = "LoginResult";
	public boolean success = false;
	public int ID = 0;
	public String Nickname = "";
	
	public LoginResult() {}
	public LoginResult(int ID, String Nickname) {
		success = true; this.ID = ID; this.Nickname = Nickname;
	}
	
	public LoginResult(String s) {
		String[] args = s.split(" ");
		success = Boolean.valueOf(args[1]);
		if (success) {
			ID = Integer.valueOf(args[2]);
			Nickname = args[3];
		}
	}
	
	public String toString() {
		if(success) return type + " " + success + " " + ID + " " + Nickname;
		else return type + " " + success;
	}
	
	public void clientParse() {
		if(!success) JOptionPane.showMessageDialog(null, "Incorrect Login Credentials");
		else {
			JOptionPane.showMessageDialog(null, "Login success");
			Game.Loggedin = true;
			Game.myID = ID;
			Game.myNickname = Nickname;
		}
	}

}

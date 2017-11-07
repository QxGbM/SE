package protocol;

import javax.swing.JOptionPane;

public class ACK implements Response{
	public String type;
	public boolean acked;
	public int intField = 0;
	public String errMessage = "";
	
	public ACK() {type = "ACK"; acked = true;}
	
	public ACK(int i) {type = "ACK"; acked = true; intField = i;}
	
	public ACK(String s) {
		String[] args = s.split(" ", 2);
		if (args[0].equals("ACK")) {
			type = "ACK";
			acked = true;
			intField = Integer.valueOf(args[1]);
		}
		if (args[0].equals("ERR")) {
			type = "ERR";
			acked = false;
			errMessage = args[1];
		}
	}
	
	public String toString() {
		return type + " " + intField;
	}
	
	public void clientParse() {
		if (!acked)
			JOptionPane.showMessageDialog(null, errMessage);
	}

}

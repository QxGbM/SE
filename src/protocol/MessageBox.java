package protocol;

import java.util.ArrayList;

public class MessageBox {
	
	public int length;
	public String type;
	public Message[] args;
	
	public MessageBox(String response) {
		String[] l = response.split(" ", 4);
		type = l[1];
		length = Integer.valueOf(l[2]);
		args = new Message[length];
		String s = l[3];
		for (int i = 0; i < length; i++) {
			args[i] = new Message(s);
			s = args[i].remaining;
		}
	}
	
	public MessageBox(ArrayList<Message> Buffer) {
		length = Buffer.size();
		type = "messagebox";
		args = new Message[length];
		int i = 0;
		while (i < length) {
			args[i] = Buffer.remove(0);
			i++;
		}
	}
	
	public String toString() {
		String s = "3 messagebox " + length;
		for(int i = 0; i < length; i++) {
			s += " " + args[i].toString();
		}
		return s;
	}
	
	public void parse() {
		for (int i = 0; i < length; i++) {
			args[i].parse();
		}
	}
}

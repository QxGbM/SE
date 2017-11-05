package test;

import java.util.ArrayList;

import protocol.*;

public class testProtocol {
	public static void main(String args[]) {
		Action a = new Action(1000, 100, 201, 2, 3);
		Action b = new Action(1000, 100, 2, 3);
		ArrayList<Action> l = new ArrayList<Action>();
		l.add(a); l.add(b);
		Action.ActionBox ab = new Action.ActionBox (l);
		System.out.println(ab.toString());
		Action.ActionBox ab2 = new Action.ActionBox (ab.toString());
		System.out.println(ab2.toString());
		
		Message m1 = new Message(100, 101, 1000, true);
		Message m2 = new Message(100, 101, "how are you?");
		ArrayList<Message> l1 = new ArrayList<Message>();
		l1.add(m1); l1.add(m2);
		Message.MessageBox mb1 = new Message.MessageBox(l1);
		System.out.println(mb1.toString());
		Message.MessageBox mb2 = new Message.MessageBox(mb1.toString());
		System.out.println(mb2.toString());
	}
}

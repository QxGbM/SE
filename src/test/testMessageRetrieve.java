package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import protocol.*;
import protocol.Message.MessageBox;

public class testMessageRetrieve {
	
	@Test
	public final void test1_0() {
		Message m = new Message(100, 101, "hi");
		assertTrue(m.message.equals("hi"));
	}
	
	@Test
	public final void test1_1() {
		Message m = new Message(100, 101, "hi");
		m.serverProcess();
	}
	
	@Test
	public final void test2_0() {
		ArrayList<Message> l = new ArrayList<Message>();
		l.add(new Message(100, 101, "hi"));
		l.add(new Message(100, 101, "how are you?"));
		MessageBox mb = new MessageBox(l);
		assertTrue(mb.toString().equals("MessageBox 2 Message 100 101 1 hi Message 100 101 3 how are you?"));
	}
	
	@Test
	public final void test3_0() {
		Message m = new Message(100, 101);
		assertTrue(m.isBattleRequest);
	}
	
	@Test
	public final void test4_0() {
		Message m1 = new Message(100, 101, "hi");
		Message m2 = new Message(m1.toString());
		assertTrue(m2.message.equals("hi"));
	}
	
	@Test
	public final void test5_0() {
		Message m1 = new Message(100, 101);
		Message m2 = new Message(m1.toString());
		assertTrue(m2.isBattleRequest);
	}
	
}

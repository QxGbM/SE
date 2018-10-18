package test;

import static org.junit.Assert.*;

import org.junit.Test;

import protocol.Login;
import server.Server;
import server.Server.User;

public class testLogin {
	
	@Test
	public final void test1_0() {
		Login l = new Login("", "");
		l.serverProcess();
		assertFalse(l.loginResult.success);
	}

	@Test
	public final void test2_0() {
		Login l = new Login("SomeBadInput", "IncorrectPassword");
		l.serverProcess();
		assertFalse(l.loginResult.success);
	}
	
	@Test
	public final void test3_0() {
		Login l = new Login("SomeBadInput", "fasdf sadf asewr#@@#$@#$@#!@#^$%");
		l.serverProcess();
		assertFalse(l.loginResult.success);
	}
	
	@Test
	public final void test4_0() {
		User tester0 = new User(100, "Tester0", "admin0", "password");
		Server.users.add(tester0);
		Login l = new Login("admin0", "password");
		l.serverProcess();
		assertTrue(l.loginResult.success);
	}

}

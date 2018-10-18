package main;

import static org.junit.Assert.*;

import org.junit.Test;

public class DatabaseAccessorTest {
	protected DatabaseAccessor x = new DatabaseAccessor();
	@Test
	public void testFindFriends() {
		
		String[] friends = x.findFriends("0").split(" ");
		assertEquals(friends.length,2);
	}

	@Test
	public void testFindUser() {
		String[] user = x.findUser("0").split(" ");
		assertEquals(user[1],"tester115");
	}

	

	@Test
	public void testCheckSpecialPractice() {
		assertFalse(x.checkSpecialPractice("0", "101"));
	}

	@Test
	public void testWin() {
		String[] user = x.findUser("0").split(" ");
		int before = Integer.parseInt(user[2]);
		x.win("0");
		user = x.findUser("0").split(" ");
		assertEquals(Integer.parseInt(user[2]),before + 1);
	}

	@Test
	public void testActivate() {
		x.activate("0","101");
		String[] card = x.getCardInfo("0", "101");
		assertEquals("1",card[4]);
	}

	@Test
	public void testDeactivate() {
		x.deactivate("0", "101");
		String[] card = x.getCardInfo("0", "101");
		assertEquals("0",card[4]);
	}

	

	@Test
	public void testUpdateCurrency() {
		float oldgold =Float.parseFloat( x.findUser("0").split( " ")[4]);
		float olddust = Float.parseFloat(x.findUser("0").split(" ")[5]);
		x.updateCurrency("0", "10", "10");
		float gold = Float.parseFloat(x.findUser("0").split(" ")[4]);
		float dust  = Float.parseFloat(x.findUser("0").split(" ")[5]);
		if(oldgold < gold && olddust < dust)
			assertTrue(true);
		else
			fail("update on currency failed");
	}

	@Test
	public void testLoss() {
		String old = x.findUser("0").split(" ")[3];
		x.loss("0");
		String update = x.findUser("0").split(" ")[3];
		if(old.compareTo(update) < 0)
			assertTrue(true);
		else
			fail("update failed");
	}

	@Test
	public void testAddCard() {
		x.addCard("0", "90000");
		assertEquals("90000",x.getCardInfo("0", "90000")[1]);
	}

	@Test
	public void testSpecialPractice() {
		x.specialPractice("0", "202");
		assertEquals("1",x.getCardInfo("0", "202")[3]);
	}

	@Test
	public void testDevolve() {
		x.devolve("0", "202");
		assertEquals("0",x.getCardInfo("0", "202")[3]);
	}

	

}

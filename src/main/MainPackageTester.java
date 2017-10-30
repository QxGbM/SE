package main;

import org.junit.*;
import static org.junit.Assert.*;

public class MainPackageTester {

 
	@Test
	public void testCard() {
		Card test  = new Card();
  
		assertTrue(test.isEmpty());
  
		assertFalse(test.isMonster());
  
		assertTrue(test.matchID(0));
      
		String text = "";
		text += "Card Name: " + "Empty Slot" + "\n";
		text += "No Card" + "\n" +""+ "\n";
		text += "mp: " + 0 + "/" +0 + "\n";
		text += "hp: " + 0 + "/" + 0 + "\n";
		assertEquals(test.toString(),text);
  
		assertEquals(test.summonSPCheck(), true);
  
		assertEquals(test.hpDeduct(5),true);
  
		assertTrue(test.mpDeduct(0));

  
	}
 
	@Test 
	public void testMainWindow() {
		MainWindow test = new MainWindow("tester");
	 
		assertEquals(true, test.loadMainPanel());
	 
	}
 
 
	@Test
	public void testGame() {

	 
		assertEquals(false,Game.sendLogin("bogus", "trollbard"));
	 
		assertEquals(false,Game.sendMessage("test", 1));
	 
		assertEquals(false,Game.retrieveMessagebox());
	 
		assertEquals(false,Game.sendInMatchActions(null));
	 
		assertEquals(false,Game.sendBattleRequest(0));
	 
		assertEquals(false,Game.sendBattleAccept(1));
	 
		assertEquals(null,Game.findFriend(-1));
	 
	 
	 
	}
 
}
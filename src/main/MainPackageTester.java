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
 
}
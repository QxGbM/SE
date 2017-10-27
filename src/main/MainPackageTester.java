package main;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import org.eclipse.swt.*;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.layout.GridLayout;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
public class MainPackageTester {
/*
 @Test
 public void testChatWindow() {//problem exists. Can't test this yet. lf unable to find friend, program crashes
  ChatWindow test = new  ChatWindow(1,"test");
  
  assertEquals(test.appendMessage("test"),true);
  
 }
*/
 
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
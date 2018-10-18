package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import main.Card;
import main.Match;
import protocol.Action;
import protocol.Action.ActionBox;
import protocol.Retrieve;

public class testGamePlay {
	int matchID = 1000;
	int playerID = 100;

	@Test
	public final void test1_0() {
		Action a = new Action(matchID, playerID, "endturn");
		assertTrue(a.toString().equals("Action 1000 100 endturn"));
	}
	
	@Test
	public final void test1_1() {
		Action a = new Action(matchID, playerID, 3, 3, 0, 0);
		assertTrue(a.toString().equals("Action 1000 100 skill 0 true 15"));
	}
	
	@Test
	public final void test1_2() {
		Action a = new Action(matchID, playerID, 3, 3);
		assertTrue(a.toString().equals("Action 1000 100 skill 0 false"));
	}
	
	@Test
	public final void test1_3() {
		Action a = new Action(matchID, playerID, 101, 3, 3);
		assertTrue(a.toString().equals("Action 1000 100 summon 101 0"));
	}
	
	@Test
	public final void test1_4() {
		Action a = new Action(matchID, playerID, true);
		assertTrue(a.toString().equals("Action 1000 100 start true"));
	}
	
	@Test
	public final void test2_0() {
		Retrieve a = new Retrieve(matchID, playerID);
		assertTrue(a.arg1.equals("ActionBox"));
	}
	
	@Test
	public final void test3_0() {
		ArrayList<Action> list = new ArrayList<Action>();
		list.add(new Action(matchID, playerID, true));
		list.add(new Action(matchID, playerID, 3, 3, 0, 0));
		ActionBox ab = new ActionBox (list);
		assertTrue(ab.toString().equals("ActionBox 2 Action 1000 100 start true Action 1000 100 skill 0 true 15"));
	}
	
	@Test
	public final void test4_0() {
		Card c = new Card();
		assertTrue(c.isEmpty());
	}
	
	@Test
	public final void test4_1() {
		Card c = new Card();
		assertTrue(c.isActive());
	}
	
	@Test
	public final void test4_2() {
		Card c = new Card();
		assertTrue(!c.isMonster());
	}
	
	@Test (expected = Exception.class)
	public final void test4_3() {
		Match.cardList = Card.cardLoader();
		Match.SP = 1;
		Card c = Match.findCardByID(205);
		assertTrue(!c.summonSPCheck());
	}
	
	@Test
	public final void test4_4() {
		Match.cardList = Card.cardLoader();
		Match.SP = 5;
		Card c = Match.findCardByID(205);
		assertTrue(c.summonSPCheck());
	}
	
	@Test
	public final void test5_0() {
		Match.vpGen = 0;
		Match.cardList = Card.cardLoader();
		Card c = Match.findCardByID(101);
		c.aftTurnCheck();
		assertTrue(Match.vpGen > 0);
	}
	
	@Test
	public final void test5_1() {
		Match.spGen = 0;
		Match.cardList = Card.cardLoader();
		Card c = Match.findCardByID(101);
		c.preTurnCheck();
		assertTrue(Match.spGen > 0);
	}
	
	@Test (expected = Exception.class)
	public final void test6_0() {
		Match.VP = 100;
		Match.myAftTurn();
	}
}

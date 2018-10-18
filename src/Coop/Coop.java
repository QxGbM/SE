package Coop;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Coop {
	
	public static int matchNum;
	public static int friendID;
	public static int playerID;
	
	public static Display display;
	public static Shell shell;
	
	public static CoopCard[][] myBoard = new CoopCard[2][4];
	public static CoopCard[][] friendBoard = new CoopCard[2][4];
	public static CoopMinion[] minions = new CoopMinion[2];
	public static CoopBoss boss;
	
	public static CoopCard[] myHand = new CoopCard[5];
	public static Text text;
	
	public static javax.swing.JTextArea logDisplay;
	public static Button ball;
	public static Button endTurn;
	
	public static int sp;
	public static int fsp;
	public static int spgen;
	public static int fspgen;
	public static int spcost;
	public static int fspcost;
	public static int vpgen;
	public static int fvpgen;
	public static int vp;
	
	public static Label mySP;
	public static Label mySPGen;
	public static Label friendSP;
	public static Label friendSPGen;
	public static Label myVPGen;
	public static Label friendVPGen;
	public static Label VP;
	public static Label bossAura;
	
	public static ArrayList<CoopCard> cards = cardLoader();
	
	public static boolean myTurn;
	public static boolean host;
	public static boolean oneEndedTurn;
	public static boolean friendLeft;
	
	public static int deck[] = {101,101,102,102,103,104,105,201,201,202,202,203,204,205};
	
	public static CardInAction actionHead = null;
	public static String nextCard = "";
	
	public static ArrayList<CardInAction> cardsOnBoard = new ArrayList<CardInAction>();
	
	public static void win() {
		text.setText("Boss: Well fought. I concede.");
		//Game.inMatch = false;
		MessageBox message = new MessageBox(shell, SWT.BALLOON | SWT.OK);
		message.setText("Notification");
		message.setMessage("You Won!");
		message.open();
		
		shell.dispose();
	}


	public static void lose() {
		text.setText("Boss: Looks like it is my victory.");
		//Game.inMatch = false;
		MessageBox message = new MessageBox(shell, SWT.BALLOON | SWT.OK);
		message.setText("Notification");
		message.setMessage("You Lost!");
		message.open();
		
		shell.dispose();
	}
	
	public static void reject(String ErrorMessage) {
		MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
		dialog.setText("Notification");
		dialog.setMessage(ErrorMessage);
		if (dialog.open() != SWT.OK) return;
	}
	
	public static Listener genMyBoardListener(int x, int y) {
		return e-> {
			if(nextCard.equals("AllyEmpty") && myTurn) {
				if(myBoard[x][y].isEmpty()) {
					actionHead.getLast().nextCard = new CardInAction(x, y, "Board");
					if (actionHead.location.equals("Hand")) {
						summonFromHand();
					}
					else if (actionHead.location.equals("Board") || actionHead.location.equals("Friend")) {
						skillActivation(actionHead);
					}
					actionHead = null;
					nextCard = "";
				}
				else {
					reject("An empty spot is required.");
				}
			}
			else if (nextCard.equals("") && myTurn) {
				if(myBoard[x][y].isEmpty()) {
					reject("Cannot do anything to an empty spot.");
				}
				else if (myBoard[x][y].skillUsed) {
					reject("This card has already activated its skill in this turn.");
				}
				else if (myBoard[x][y].hold) {
					reject("This card is currently inactive.");
				}
				else {
					actionHead = new CardInAction(x, y, "Board");
					skillActivation(actionHead);
				}
			}
			else if (!myTurn) {
				reject("It is not your turn.");
			}
			else {
				reject("Unsupported action.");
			}
		};
	}
	
	public static Listener genFriendBoardListener(int x, int y) {
		return e-> {
			if(nextCard.equals("AllyEmpty")) {
				if(friendBoard[x][y].isEmpty()) {
					actionHead.getLast().nextCard = new CardInAction(x, y, "Friend");
					if (actionHead.location.equals("Hand")) {
						summonFromHand();
					}
					else if (actionHead.location.equals("Board") || actionHead.location.equals("Friend")) {
						skillActivation(actionHead);
					}
					actionHead = null;
					nextCard = "";
				}
				else {
					reject("An empty spot is required.");
				}
			}
			else if (nextCard.equals("") && myTurn && friendLeft) {
				if(friendBoard[x][y].isEmpty()) {
					reject("Cannot do anything to an empty spot.");
				}
				else if (friendBoard[x][y].skillUsed) {
					reject("This card has already activated its skill in this turn.");
				}
				else if (friendBoard[x][y].hold) {
					reject("This card is currently inactive.");
				}
				else {
					actionHead = new CardInAction(x, y, "Friend");
					skillActivation(actionHead);
				}
			} 
			else {
				reject("Unsupported action.");
			}
		};
	}
	
	public static Listener genBossListener(int x) {
		return e-> {
			if(nextCard.equals("Enemy")) {
				if (x == 0 || x == 1 || (x == 2 && minions[0].isEmpty() && minions[1].isEmpty())) {
					actionHead.getLast().nextCard = new CardInAction(x, "Boss");
					skillActivation(actionHead);
					actionHead = null;
					nextCard = "";
				}
				else {
					reject("You have to attack minions first.");
					text.setText("Boss: Hahaha! You can't touch me!");
				}
			}
			else {
				text.setText("Boss: What, mortal?");
			}
		};
	}
	
	public static Composite initializeBoard() {
		
		Composite board = new Composite(shell, SWT.EMBEDDED);
		
		GridData data;
		GridLayout layout;
		
		Composite cboard = new Composite(board, SWT.EMBEDDED);
		
		minions[0] = new CoopMinion();
		Composite m1 = minions[0].genImage(cboard);
		minions[0].actionButton.addListener(SWT.Selection, genBossListener(0));
		
		boss = new CoopBoss();
		Composite b1 = boss.genImage(cboard);
		boss.actionButton.addListener(SWT.Selection, genBossListener(2));
		
		minions[1] = new CoopMinion();
		Composite m2 = minions[1].genImage(cboard);
		minions[1].actionButton.addListener(SWT.Selection, genBossListener(1));
		
		text = new Text(cboard, SWT.BORDER | SWT.CENTER);
		
		layout = new GridLayout(3, false);
		layout.marginWidth = layout.marginHeight = 5;
		layout.horizontalSpacing = layout.verticalSpacing = 5;
		cboard.setLayout(layout);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.verticalSpan = 1;
		m1.setLayoutData(data);
		m2.setLayoutData(data);
		b1.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		data.verticalSpan = 1;
		text.setLayoutData(data);
		
		Label s1 = new Label(board, SWT.SEPARATOR | SWT.HORIZONTAL);
		
		ArrayList<Composite> cl = new ArrayList<Composite>();
		
		Composite fboard = new Composite(board, SWT.NONE);
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				friendBoard[i][j] = new CoopCard();
				cl.add(friendBoard[i][j].genImage(fboard));
			}
		}
		
		layout = new GridLayout(3, false);
		board.setLayout(layout);
		
		layout = new GridLayout(4, true);
		layout.marginWidth = layout.marginHeight = 5;
		layout.horizontalSpacing = layout.verticalSpacing = 5;
		fboard.setLayout(layout);
		
		Label s2 = new Label(board, SWT.SEPARATOR | SWT.VERTICAL);
		
		Composite mboard = new Composite(board, SWT.EMBEDDED);
		
		layout = new GridLayout(4, true);
		layout.marginWidth = layout.marginHeight = 5;
		layout.horizontalSpacing = layout.verticalSpacing = 5;
		mboard.setLayout(layout);
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				myBoard[i][j] = new CoopCard();
				cl.add(myBoard[i][j].genImage(mboard));
			}
		}

		for(int i = 0; i < 16; i++) {
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 1;
			cl.get(i).setLayoutData(data);
		}
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				friendBoard[i][j].actionButton.addListener(SWT.Selection, genFriendBoardListener(i, j));
				myBoard[i][j].actionButton.addListener(SWT.Selection, genMyBoardListener(i, j));
			}
		}
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		cboard.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		data.verticalSpan = 1;
		s1.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		fboard.setLayoutData(data);
		
		data = new GridData(GridData.FILL_VERTICAL);
		data.horizontalSpan = 1;
		data.verticalSpan = 1;
		s2.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		mboard.setLayoutData(data);
		
		board.pack();
		return board;
	}

	
	public static Composite initializeRightPanel (boolean move) {
		Composite b = new Composite (shell, SWT.EMBEDDED);
		
		GridLayout layout = new GridLayout(4, true);
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		b.setLayout(layout);
		
		Composite log = initializeBattleLogPanel(b);
		
		Label s1 = new Label(b, SWT.SEPARATOR | SWT.HORIZONTAL);
		
		mySP = new Label(b, SWT.CENTER);
		mySPGen = new Label(b, SWT.CENTER);
		myVPGen = new Label(b, SWT.CENTER);
		friendSP = new Label(b, SWT.CENTER);
		friendSPGen = new Label(b, SWT.CENTER);
		friendVPGen = new Label(b, SWT.CENTER);
		VP = new Label(b, SWT.CENTER);
		bossAura = new Label(b, SWT.CENTER);

		mySP.setText("SP: " + sp);
		mySPGen.setText("SPGen: " + spgen);
		myVPGen.setText("VPGen: " + vpgen);
		friendSP.setText("friend SP: " + fsp);
		friendSPGen.setText("friend SPGen: " + fspgen);
		friendVPGen.setText("friend VPGen: " + fvpgen);
		VP.setText("VP: " + vp);
		bossAura.setText("Aura: " + boss.auraLevel);
		
		Composite fb = initializeFunctionalButtons(b, move);
		
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		data.verticalSpan = 1;
		log.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		data.verticalSpan = 1;
		s1.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.verticalSpan = 1;
		mySP.setLayoutData(data);
		mySPGen.setLayoutData(data);
		myVPGen.setLayoutData(data);
		friendSP.setLayoutData(data);
		friendSPGen.setLayoutData(data);
		friendVPGen.setLayoutData(data);
		VP.setLayoutData(data);
		bossAura.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		data.verticalSpan = 1;
		fb.setLayoutData(data);
		
		b.pack();
		return b;
	}
	
	public static Composite initializeBattleLogPanel(Composite b) {
		Composite bl = new Composite(b, SWT.EMBEDDED);
		Composite TextComp = new Composite(bl, SWT.EMBEDDED);
		
		Label separator = new Label(bl, SWT.SEPARATOR | SWT.HORIZONTAL);
		java.awt.Frame TextFrame = SWT_AWT.new_Frame(TextComp);
		
		logDisplay = new JTextArea();
		logDisplay.setEditable(false);
		logDisplay.setText("Battle Start!\n");
		
		JScrollPane scrollpane = new JScrollPane(logDisplay);
		TextFrame.add(scrollpane);
		
		Composite comp = new Composite(bl, SWT.EMBEDDED);
		Text input = new Text(comp, SWT.NONE);
		Button send = new Button(comp, SWT.PUSH);
		send.setText("Send");
		send.addListener(SWT.Selection, e-> {
			if(!input.getText().equals("")) {
				// TODO send a message
				logDisplay.append("Tester 0: " + input.getText() + "\n");
				input.setText("");
			}
			else {
				
			}
		});
		
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = 10;
		layout.horizontalSpacing = layout.verticalSpacing = 20;
		bl.setLayout(layout);
		
		GridData data;
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		separator.setLayoutData(data);
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		TextComp.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		comp.setLayoutData(data);
		
		layout = new GridLayout(3, true);
		layout.marginWidth = layout.marginHeight = 0;
		layout.horizontalSpacing = layout.verticalSpacing = 20;
		comp.setLayout(layout);
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		input.setLayoutData(data);
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		send.setLayoutData(data);
		bl.pack();
		
		java.awt.Font font = new java.awt.Font("Times New Roman", Font.PLAIN, TextComp.getSize().y);
		logDisplay.setFont(font);
		
		return bl;
	}
	
	public static Composite initializeFunctionalButtons (Composite c, boolean move) {
		Composite b = new Composite (c, SWT.EMBEDDED);
		
		GridLayout layout = new GridLayout(4, true);
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 5;
		b.setLayout(layout);
		
		ball = new Button(b, SWT.PUSH);
		ball.setEnabled(move);
		if(move)
			ball.setText("Ball");
		else
			ball.setText("Friend's Turn");
		
		ball.addListener(SWT.Selection, e -> {
			MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_INFORMATION);
			dialog.setText("Confirmation");
			dialog.setMessage("This would temporarily pass the turn to your friend, are you sure?");
			if (dialog.open() != SWT.OK) return;
			
			sendCoopAction(new protocol.CoopAction(matchNum, playerID, "ball"));
			myTurn = false;
			actionHead = null;
			nextCard = "";
			ball.setText("Friend's Turn");
			ball.setEnabled(false);
		});
		
		endTurn = new Button(b, SWT.PUSH);
		endTurn.setText("End Turn");
		endTurn.setEnabled(move);
		
		endTurn.addListener(SWT.Selection, e -> {
			MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_INFORMATION);
			dialog.setText("Confirmation");
			dialog.setMessage("It would then become Boss's turn, are you sure to end turn?");
			if (dialog.open() != SWT.OK) return;
			myTurn = false;
			actionHead = null;
			nextCard = "";
			if (!friendLeft && !oneEndedTurn) {
				sendCoopAction(new protocol.CoopAction(matchNum, playerID, "endturn"));
				ball.setText("Friend's Turn");
				ball.setEnabled(false);
				endTurn.setText("Friend's Turn");
				endTurn.setEnabled(false);
				oneEndedTurn = true;
			}
			else {
				if (!friendLeft) sendCoopAction(new protocol.CoopAction(matchNum, playerID, "endturn"));
				aftTurn();
				if (!shell.isDisposed()) bossTurn();
			}
			
		});
		
		Button cancel = new Button(b, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.addListener(SWT.Selection, e -> {
			if (nextCard.equals("")) {
				MessageBox message = new MessageBox(shell, SWT.ERROR | SWT.OK | SWT.CANCEL);
				message.setText("Error");
				message.setMessage("Nothing to cancel");
				message.open();
			}
			else {
				MessageBox message = new MessageBox(shell, SWT.BALLOON | SWT.OK | SWT.CANCEL);
				message.setText("Confirmation");
				message.setMessage("Cancel this action?");
				if(message.open() == SWT.OK) {
					nextCard = "";
					actionHead = null;
				}
			}
			
		});
		
		Button surrender = new Button(b, SWT.PUSH);
		surrender.setText("Surrender");
		Listener l = e -> {
			MessageBox message = new MessageBox(shell, SWT.BALLOON | SWT.OK | SWT.CANCEL);
			message.setText("Confirmation");
			message.setMessage("Are you sure to leave your team?");
			if(message.open() == SWT.OK) {
				lose();
				sendCoopAction(new protocol.CoopAction(matchNum, playerID, "surrender"));
			}
		};
		surrender.addListener(SWT.Selection, l);
		shell.addListener(SWT.Close, l);
		
		Label s1 = new Label(b, SWT.SEPARATOR | SWT.HORIZONTAL);
		
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.verticalSpan = 1;
		ball.setData(data);
		endTurn.setData(data);
		cancel.setData(data);
		surrender.setData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		data.verticalSpan = 1;
		s1.setLayoutData(data);
		
		Composite c1 = initializeMyHand(b);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		data.verticalSpan = 1;
		c1.setLayoutData(data);
		
		b.pack();
		return b;
	}
	
	public static Listener genMyHandListener(int i) {
		return e -> {
			if(nextCard.equals("") && myTurn) {
				if(myHand[i].isEmpty()) {
					reject("Cannot summon an empty card");
				}
				else {
					actionHead = new CardInAction(i, "Hand");
					nextCard = "AllyEmpty";
				}
			}
			else if (!myTurn) {
				reject("It is not your turn.");
			}
			else {
				reject("Cannot target your hand");
			}
		};
	}
	
	public static Composite initializeMyHand (Composite c) {
		Composite b = new Composite (c, SWT.EMBEDDED);
		
		GridLayout layout = new GridLayout(5, true);
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		b.setLayout(layout);
		
		Label l = new Label(b, SWT.CENTER);
		l.setText("My Hand");
		
		ArrayList<Composite> cl = new ArrayList<Composite>();
		
		for (int i = 0; i < 5; i++) {
			myHand[i] = lookupCard(drawACard());
			cl.add(myHand[i].genImage(b));
		}
		
		for (int i = 0; i < 5; i++) {
			myHand[i].actionButton.addListener(SWT.Selection, genMyHandListener(i));
		}
		
		GridData data;
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 5;
		data.verticalSpan = 1;
		l.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.verticalSpan = 1;
		
		for(int i = 0; i < 5; i++) {
			cl.get(i).setLayoutData(data);
		}
		
		b.pack();
		return b;
	}
	
	public static void summonFromHand() {
		int i = actionHead.y, x = actionHead.nextCard.x, y = actionHead.nextCard.y;
		String location = actionHead.nextCard.location;
		if (location.equals("Board"))
			summon(myHand[i], myBoard[x][y], x, y, location);
		else if (location.equals("Friend"))
			summon(myHand[i], friendBoard[x][y], x, y, location);
	}
	
	public static void summon (CoopCard card, CoopCard slot, int x, int y, String location) {
		if (location.equals("Board")) {
			if (card.spCost > sp) {reject("Insufficient SP to summon"); return;}
			sp -= card.spCost;
			mySP.setText("SP: " + sp);
		}
		else if (location.equals("Friend")) {
			if (card.spCost > fsp) {reject("Your friend has insufficient SP to summon"); return;}
			fsp -= card.spCost;
			friendSP.setText("friend SP: " + fsp);
		}
		slot.updateImage(card.getImage());
		slot.loadCardInfo(card.genCardInfo());
		slot.updateInfo();
		slot.updateGraphics();
		slot.summonCheck();
		card.setEmpty();
		cardsOnBoard.add(new CardInAction(x, y, location));
		if (myTurn && !friendLeft) sendCoopAction(new protocol.CoopAction(matchNum, playerID, slot.getID(), x, y, location));
	}
	
	public static void friendSummon (int id, int location) {
		CoopCard c = lookupCard(id);
		if (location < 8) {
			int x = location / 4, y = location % 4;
			String s = "Board";
			summon (c, myBoard[x][y], x, y, s);
		}
		else if (location < 16) {
			int x = location / 4 - 2, y = location % 4;
			String s = "Friend";
			summon (c, friendBoard[x][y], x, y, s);
		}
	}
	
	public static void skillActivation (CardInAction actionHead) {
		int x = actionHead.x, y = actionHead.y;
		String location = actionHead.location;
		if (location.equals("Board")) {
			Skill s = myBoard[x][y].parseSkill();
			if (actionHead.nextCard == null && s.requiresTarget) {
				nextCard = s.targetType;
			}
			else {
				s.reflect(actionHead);
				if (myTurn && !friendLeft) {
					if (s.requiresTarget) {
						int x2 = actionHead.nextCard.x, y2 = actionHead.nextCard.y;
						String location2 = actionHead.nextCard.location;
						sendCoopAction(new protocol.CoopAction(matchNum, playerID, x, y, location, x2, y2, location2));
					}
					else {
						sendCoopAction(new protocol.CoopAction(matchNum, playerID, x, y, location));
					}
				}
				nextCard = "";
				actionHead = null;
			}
		}
		else if (location.equals("Friend")) {
			Skill s = friendBoard[x][y].parseSkill();
			if (actionHead.nextCard == null && s.requiresTarget) {
				nextCard = s.targetType;
			}
			else {
				s.reflect(actionHead);
				if (myTurn && !friendLeft) {
					if (s.requiresTarget) {
						int x2 = actionHead.nextCard.x, y2 = actionHead.nextCard.y;
						String location2 = actionHead.nextCard.location;
						sendCoopAction(new protocol.CoopAction(matchNum, playerID, x, y, location, x2, y2, location2));
					}
					else {
						sendCoopAction(new protocol.CoopAction(matchNum, playerID, x, y, location));
					}
				}
				nextCard = "";
				actionHead = null;
			}
		}
		
	}
	
	public static void friendSkillActivation (int location) {
		if (location < 8) {
			int x = location / 4, y = location % 4;
			String s = "Board";
			CardInAction actionHead = new CardInAction(x, y, s);
			skillActivation(actionHead);
		}
		else if (location < 16) {
			int x = location / 4 - 2, y = location % 4;
			String s = "Friend";
			CardInAction actionHead = new CardInAction(x, y, s);
			skillActivation(actionHead);
		}
		
	}
	
	public static void friendSkillActivation (int location, int location2) {
		if (location < 8) {
			int x = location / 4, y = location % 4;
			String s = "Board";
			CardInAction actionHead = new CardInAction(x, y, s);
			if (location2 < 8) {
				int x2 = location / 4, y2 = location % 4;
				String s2 = "Board";
				CardInAction target = new CardInAction(x2, y2, s2);
				actionHead.nextCard = target;
				skillActivation(actionHead);
			}
			else if (location2 < 16) {
				int x2 = location / 4 - 2, y2 = location % 4;
				String s2 = "Friend";
				CardInAction target = new CardInAction(x2, y2, s2);
				actionHead.nextCard = target;
				skillActivation(actionHead);
			}
			else {
				int y2 = location % 4;
				String s2 = "Boss";
				CardInAction target = new CardInAction(y2, s2);
				actionHead.nextCard = target;
				skillActivation(actionHead);
			}
		}
		else if (location < 16) {
			int x = location / 4 - 2, y = location % 4;
			String s = "Friend";
			CardInAction actionHead = new CardInAction(x, y, s);
			if (location2 < 8) {
				int x2 = location / 4, y2 = location % 4;
				String s2 = "Board";
				CardInAction target = new CardInAction(x2, y2, s2);
				actionHead.nextCard = target;
				skillActivation(actionHead);
			}
			else if (location2 < 16) {
				int x2 = location / 4 - 2, y2 = location % 4;
				String s2 = "Friend";
				CardInAction target = new CardInAction(x2, y2, s2);
				actionHead.nextCard = target;
				skillActivation(actionHead);
			}
			else {
				int y2 = location % 4;
				String s2 = "Boss";
				CardInAction target = new CardInAction(y2, s2);
				actionHead.nextCard = target;
				skillActivation(actionHead);
			}
		}
	}
	
	public static void friendPassBall () {
		MessageBox message = new MessageBox(shell, SWT.BALLOON | SWT.OK);
		message.setText("Confirmation");
		message.setMessage("Your friend has gives back the turn, it is now your turn to move!");
		message.open();
		
		myTurn = true;
		actionHead = null;
		nextCard = "";
		ball.setText("Ball");
		ball.setEnabled(true);
	}
	
	public static void friendEndTurn () {
		MessageBox message = new MessageBox(shell, SWT.BALLOON | SWT.OK);
		message.setText("Confirmation");
		message.setMessage("Your friend has ended the turn.");
		message.open();
		
		if (oneEndedTurn) {
			message = new MessageBox(shell, SWT.BALLOON | SWT.OK);
			message.setText("Confirmation");
			message.setMessage("Both player ended turn");
			message.open();
			aftTurn();
			if (!shell.isDisposed()) bossTurn();
		}
		else {
			myTurn = true;
			actionHead = null;
			nextCard = "";
			ball.setText("Ball");
			ball.setEnabled(false);
			oneEndedTurn = true;
		}
	}
	
	public static void friendLeft() {
		MessageBox message = new MessageBox(shell, SWT.BALLOON | SWT.OK | SWT.CANCEL);
		message.setText("Confirmation");
		message.setMessage("Your friend has left the game, do you wish to continue?");
		if (message.open() == SWT.OK) {
			ball.setText("Ball");
			ball.setEnabled(false);
			host = true;
			friendLeft = true;
			if (!myTurn && oneEndedTurn) {
				aftTurn();
				bossTurn();
				if (!shell.isDisposed()) yourTurn();
			}
			else {
				myTurn = true;
			}
		}
		else {
			lose();
		}
	}
	
	public static CoopCard lookupCard(int n) {
		for(int i = 0; i < cards.size(); i++) {
			if (cards.get(i).getID() == n) return cards.get(i).clone();
		}
		return null;
	}
	
	public static int drawACard() {
		Random r = new Random();
		int t = r.nextInt(deck.length);
		int[] newDeck = new int[deck.length-1];
		for(int i = 0; i < t; i++) {
			newDeck[i] = deck[i];
		}
		for(int i = t+1; i < deck.length; i++) {
			newDeck[i-1] = deck[i];
		}
		t = deck[t];
		deck = newDeck;
		return t;
	}
	
	public static void shuffleInACard(int id) {
		int[] newDeck = new int[deck.length+1];
		for(int i = 0; i < deck.length; i++) {
			newDeck[i] = deck[i];
		}
		newDeck[deck.length] = id;
		deck = newDeck;
	}
	
	public static ArrayList<CoopCard> cardLoader() {
		ArrayList<CoopCard> cardList = new ArrayList<CoopCard>();
		try {
			File cardFile = new File("cards/cards.txt");
			BufferedReader reader = new BufferedReader(new FileReader(cardFile));
			while (reader.ready()) {
				String[] cardInfo = new String[10];
				for (int i = 0; i < 10 && reader.ready(); i++) {
					cardInfo[i] = reader.readLine();
				}
				cardList.add(new CoopCard(cardInfo));
			}
			reader.close();
		} catch (IOException e) {e.printStackTrace();}
		return cardList;
	}
	
	public static void preTurn() {
		sp = fsp = 1;
		spgen = fspgen = spcost = fspcost = 0;
		for (int i = 0; i < cardsOnBoard.size(); i++) {
			int x = cardsOnBoard.get(i).x, y = cardsOnBoard.get(i).y;
			String location = cardsOnBoard.get(i).location;
			if (location.equals("Board")) {
				if(myBoard[x][y].hold) {
					myBoard[x][y].turns --;
					if (myBoard[x][y].turns == 0) {
						myBoard[x][y].hold = false;
						myBoard[x][y].updateInfo();
						myBoard[x][y].updateGraphics();
					}
					else {myBoard[x][y].updateInfo(); continue;}
				}
				if(myBoard[x][y].shielded) {
					myBoard[x][y].shieldedTurns --;
					if (myBoard[x][y].shieldedTurns == 0) {
						myBoard[x][y].shielded = false;
					}
				}
				Skill s = myBoard[x][y].parsePreturnEffects();
				if (!myBoard[x][y].hold) s.reflect(cardsOnBoard.get(i));
				myBoard[x][y].skillUsed = false;
				if(myBoard[x][y].consumesSP()) spcost += myBoard[x][y].spCost;
				
			}
			else if (location.equals("Friend")) {
				if(friendBoard[x][y].hold) {
					friendBoard[x][y].turns --;
					if (friendBoard[x][y].turns == 0) {
						friendBoard[x][y].hold = false;
						friendBoard[x][y].updateInfo();
						friendBoard[x][y].updateGraphics();
					}
					else {friendBoard[x][y].updateInfo(); continue;}
				}
				if(friendBoard[x][y].shielded) {
					friendBoard[x][y].shieldedTurns --;
					if (friendBoard[x][y].shieldedTurns == 0) {
						friendBoard[x][y].shielded = false;
					}
				}
				Skill s = friendBoard[x][y].parsePreturnEffects();
				if (!friendBoard[x][y].hold) s.reflect(cardsOnBoard.get(i));
				friendBoard[x][y].skillUsed = false;
				if(friendBoard[x][y].consumesSP()) fspcost += friendBoard[x][y].spCost;
			}
		}
		sp = 1 + spgen - spcost;
		fsp = 1 + fspgen - fspcost;
		mySP.setText("SP: " + sp);
		mySPGen.setText("SPGen: " + spgen);
		friendSP.setText("friend SP: " + fsp);
		friendSPGen.setText("friend SPGen: " + fspgen);
		
		if (sp < 0) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 4; j++) {
					if (myBoard[i][j].consumesSP()) {
						myBoard[i][j].hold = true;
						myBoard[i][j].turns = 1;
					}
				}
			}
			MessageBox message = new MessageBox(shell, SWT.BALLOON);
			message.setText("Insufficient SP");
			message.setMessage("Please build more buildings to supply your monsters. ");
			message.open();
		}
		
		if (fsp < 0) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 4; j++) {
					if (friendBoard[i][j].consumesSP()) {
						friendBoard[i][j].hold = true;
						friendBoard[i][j].turns = 1;
					}
				}
			}
			MessageBox message = new MessageBox(shell, SWT.BALLOON);
			message.setText("Insufficient SP");
			message.setMessage("Your friend has insufficent SP.");
			message.open();
		}
	}
	
	public static void aftTurn() {
		vpgen = fvpgen = 0;
		for (int i = 0; i < cardsOnBoard.size(); i++) {
			int x = cardsOnBoard.get(i).x, y = cardsOnBoard.get(i).y;
			String location = cardsOnBoard.get(i).location;
			if (location.equals("Board")) {
				Skill s = myBoard[x][y].parseAftturnEffects();
				if (!myBoard[x][y].hold) s.reflect(cardsOnBoard.get(i));
			}
			else if (location.equals("Friend")) {
				Skill s = friendBoard[x][y].parseAftturnEffects();
				if (!friendBoard[x][y].hold) s.reflect(cardsOnBoard.get(i));
			}
		}
		myVPGen.setText("VPGen: " + vpgen);
		friendVPGen.setText("friend VPGen: " + fvpgen);
		vp += vpgen + fvpgen;
		VP.setText("VP: " + vp);
		
		if (vp >= 100) win();
	}
	
	public static void yourTurn() {

		if (host) {
			myTurn = true;
			if (friendLeft) {
				ball.setEnabled(false);
				ball.setText("Friend Left");
			}
			else {
				ball.setEnabled(true);
				ball.setText("Your Turn");
			}
		}
		else {
			myTurn = false;
			ball.setEnabled(false);
			ball.setText("Friend's Turn");
		}
		
		endTurn.setEnabled(true);
		endTurn.setText("End Turn");
		oneEndedTurn = false;
		
		for (int i = 0; i < 5; i++) {
			if(myHand[i].isEmpty() && deck.length != 0) {
				CoopCard c = lookupCard(drawACard());
				myHand[i].loadCardInfo(c.genCardInfo());
				myHand[i].updateImage(c.getImage());
				myHand[i].updateInfo();
				myHand[i].updateGraphics();
			}
		}
		
		preTurn();
		
		MessageBox message = new MessageBox(shell, SWT.BALLOON);
		message.setText("Your Turn");
		message.setMessage("It is now your turn to move!");
		message.open();
	}
	
	
	public static void bossTurn () {
		boss.Action();
	}
	
	public static void sendCoopAction(protocol.CoopAction a) {
		main.NetClient.sendCoopAction(a);
	}
	

	public static void startCoop (int mID, int fID, boolean Host) {
		
		matchNum = mID;
		friendID = fID;
		playerID = main.Game.myID;
		sp = 1;
		fsp = 1;
		
		host = Host;
		r.setSeed(mID);
		
		shell = new Shell(display);
		
		shell.setText("Simple Card Collection and Battling Game");
		
		Composite c1 = initializeBoard();
		
		Label s1 = new Label(shell, SWT.SEPARATOR | SWT.VERTICAL);
		
		Composite c2 = initializeRightPanel(true);
		
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = layout.marginHeight = 5;
		layout.horizontalSpacing = layout.verticalSpacing = 5;
		shell.setLayout(layout);
		
		GridData data;
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		data.verticalSpan = 2;
		c1.setLayoutData(data);
		
		data = new GridData(GridData.FILL_VERTICAL);
		data.horizontalSpan = 1;
		s1.setData(data);
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		c2.setLayoutData(data);
		
		shell.pack();
		shell.open();
		
		MessageBox message = new MessageBox(shell, SWT.BALLOON);
		message.setText("Message from the villagers");
		message.setMessage("Please help us, our villiage is being attacked!");
		message.open();
		
		message.setMessage("Please defeat the boss, or rebuild the village to drive him out.");
		message.open();
		
		yourTurn();

	}
	
	public static Random r = new Random();
	
	public static boolean getRandom() {
		boolean b = r.nextBoolean();
		if (host) return b; else return !b;
	}
	

}

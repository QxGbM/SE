package AI;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;

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

public final class Match {
	
	public static Card[][] board;
	public static ArrayList<Card> myHand;
	public static ArrayList<Card> myDeck;
	
	public static ArrayList<Card> cardList;
	
	public static boolean endTurn;
	
	public static int VP;
	public static int SP;
	
	public static int opponentVP;
	public static int opponentSP;
	
	public static int spGen;
	public static int spCost;
	public static int vpGen;
	
	public static int opponentspGen;
	public static int opponentspCost;
	public static int opponentvpGen;
	
	public static int[] coordinatesTemp;
	
	public static ArrayList<int[]> myOrder;
	public static ArrayList<int[]> opponentOrder;
	
	public static Display display;
	public static Shell shell;
	
	public static boolean selectedSummon;
	public static int cardSummon;
	
	public static boolean selectedSkillActivation;
	public static String targetSkillActivation;
	public static int[] cardSkillActivation;
	
	public static javax.swing.JTextArea logDisplay;
	
	public static Label SPlabel;
	public static Label SPCostlabel;
	public static Label VPlabel;
	public static Label VPGenlabel;
	
	public static Label opponentSPlabel;
	public static Label opponentSPCostlabel;
	public static Label opponentVPlabel;
	public static Label opponentVPGenlabel;
	
	public static int[] AIdeck = {101, 101, 102, 102, 103, 103, 104, 105, 201, 201, 202, 202, 203, 204, 205};
	public static int[] AIhand = new int[5];
	
	public static void win() {
		MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
		dialog.setText("Notification");
		dialog.setMessage("You Won!");
		dialog.open();
		shell.dispose();
		AI.endGame(true);
	}
	
	public static void lose() {
		MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
		dialog.setText("Notification");
		dialog.setMessage("You Lost!");
		dialog.open();
		shell.dispose();
		AI.endGame(false);
	}
	
	public static void reject(String ErrorMessage) {
		MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
		dialog.setText("Notification");
		dialog.setMessage(ErrorMessage);
		if (dialog.open() != SWT.OK) return;
	}
	
	public static Composite initializeFunctionButtons() {
		Composite fb = new Composite(shell, SWT.EMBEDDED);
		
		Button endTurnButton = new Button(fb, SWT.PUSH);
		Button surrender = new Button(fb, SWT.PUSH);
		Button cancel = new Button(fb, SWT.PUSH);
		
		endTurnButton.setText("End Turn");
		Listener endTurnListener = e -> {
			if (endTurn) reject("Your Turn is over.");
			else {
				MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION);
				dialog.setText("End Turn Confirmation");
				dialog.setMessage("End Turn?");
				if (e.type == SWT.Close) e.doit = false;
				if (dialog.open() != SWT.OK) return;
				if (selectedSummon) {
					selectedSummon = false;
					logDisplay.append("Summon cancelled.\n");
				}
				if (selectedSkillActivation) {
					selectedSkillActivation = false;
					logDisplay.append("Skill cancelled.\n");
				}
				logDisplay.append("Turn End.\n");
				
				myAftTurn();
				
				if(!shell.isDisposed()) {
					endTurn = true;
					opponentPreTurn();
				}
			}
		};
		endTurnButton.addListener(SWT.Selection, endTurnListener);
		
		surrender.setText("Surrender");
		Listener surrenderListener = e -> {
			MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION);
			dialog.setText("Surrender Confirmation");
			dialog.setMessage("Surrender?");
			if (e.type == SWT.Close) e.doit = false;
			if (dialog.open() != SWT.OK) return;
			lose();
		};
		surrender.addListener(SWT.Selection, surrenderListener);
		shell.addListener(SWT.Close, surrenderListener);
		
		cancel.setText("Cancel");
		Listener cancelListener = e -> {
			if (!selectedSummon && ! selectedSkillActivation) 
			{reject("nothing to cancel"); return;}
			MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION);
			dialog.setText("Cancel Confirmation");
			dialog.setMessage("Cancel?");
			if (e.type == SWT.Close) e.doit = false;
			if (dialog.open() != SWT.OK) return;
			if (selectedSummon) {
				selectedSummon = false;
				logDisplay.append("Summon cancelled.\n");
			}
			if (selectedSkillActivation) {
				selectedSkillActivation = false;
				logDisplay.append("Skill cancelled.\n");
			}
		};
		cancel.addListener(SWT.Selection, cancelListener);
		
		SPlabel = new Label(fb, SWT.CENTER);
		SPlabel.setText("Remaining SP: " + SP);
		SPCostlabel = new Label(fb, SWT.CENTER);
		SPCostlabel.setText("Current SP cost: " + spCost);
		VPlabel = new Label(fb, SWT.CENTER);
		VPlabel.setText("VP: " + VP);
		VPGenlabel = new Label(fb, SWT.CENTER);
		VPGenlabel.setText("Current VP gen: " + vpGen);
		
		opponentSPlabel = new Label(fb, SWT.CENTER);
		opponentSPlabel.setText("Opponent SP: " + opponentSP);
		opponentSPCostlabel = new Label(fb, SWT.CENTER);
		opponentSPCostlabel.setText("Opponent SP cost: " + opponentspCost);
		opponentVPlabel = new Label(fb, SWT.CENTER);
		opponentVPlabel.setText("Opponent VP: " + opponentVP);
		opponentVPGenlabel = new Label(fb, SWT.CENTER);
		opponentVPGenlabel.setText("Opponent VP gen: " + opponentvpGen);
		
		GridLayout layout = new GridLayout(4, true);
		layout.marginWidth = layout.marginHeight = 5;
		layout.horizontalSpacing = layout.verticalSpacing = 5;
		fb.setLayout(layout);
		
		GridData data;
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		endTurnButton.setLayoutData(data);
		surrender.setLayoutData(data);
		cancel.setLayoutData(data);

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		SPlabel.setLayoutData(data);
		SPCostlabel.setLayoutData(data);
		VPlabel.setLayoutData(data);
		VPGenlabel.setLayoutData(data);
		opponentSPlabel.setLayoutData(data);
		opponentSPCostlabel.setLayoutData(data);
		opponentVPlabel.setLayoutData(data);
		opponentVPGenlabel.setLayoutData(data);

		fb.pack();
		return fb;
	}
	
	public static Composite initializeBoard() {

		shell.setText("Simple Card Collection and Battling Game");
		
		Composite b = new Composite(shell, SWT.EMBEDDED);
		ArrayList<Composite> cl = new ArrayList<Composite>();
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				board[i][j] = new Card();
				cl.add(board[i][j].genImage(b));
			}
		}
		
		addEnemySlotListener();
		
		Label s = new Label(b, SWT.SEPARATOR | SWT.HORIZONTAL);
		
		for (int i = 2; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				board[i][j] = new Card();
				cl.add(board[i][j].genImage(b));
			}
		}
		
		addAllySlotListener();
		
		GridLayout layout = new GridLayout(4, true);
		layout.marginWidth = layout.marginHeight = 5;
		layout.horizontalSpacing = layout.verticalSpacing = 5;
		b.setLayout(layout);
		
		GridData data;
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		s.setLayoutData(data);
		
		for(int i = 0; i < 16; i++) {
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 1;
			cl.get(i).setLayoutData(data);
		}
		
		b.pack();
		return b;
	}
	
	public static Composite fillMyHandFirstTime() {
		Composite h = new Composite(shell, SWT.EMBEDDED);
		Label l = new Label (h, SWT.CENTER);
		l.setText("My Hand");
		ArrayList<Composite> hl = new ArrayList<Composite>();
		
		while (myHand.size() < 5){
			Random r = new Random();
			Card c = myDeck.remove(r.nextInt(myDeck.size()));
			myHand.add(c);
			hl.add(c.genImage(h));
		}
		
		addHandListener();
		
		GridLayout layout = new GridLayout(5, true);
		layout.marginWidth = layout.marginHeight = 5;
		layout.horizontalSpacing = layout.verticalSpacing = 5;
		h.setLayout(layout);
		
		GridData data;
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 5;
		l.setLayoutData(data);
		
		for(int i = 0; i < 5; i++) {
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 1;
			hl.get(i).setLayoutData(data);
		}
		
		h.pack();
		return h;
	}
	
	public static void fillMyHand() {
		for (int i = 0; i < 5; i++) {
			if (myHand.get(i).isEmpty()) {
				Random r = new Random();
				Card c = myDeck.remove(r.nextInt(myDeck.size()));
				String[] cardInfo = c.genCardInfo();
				myHand.get(i).loadCardInfo(cardInfo);
				myHand.get(i).updateImage(c.getImage());
				myHand.get(i).updateDisplay();
			}
		}
		logDisplay.append("Remaining Deck: " + myDeck.size() + " cards.\n");
	}
	
	public static boolean summon(Card m, int[] coordinates) {
		int x = coordinates[0], y = coordinates[1];
		if (m.summonSPCheck()) {
			board[x][y].loadCardInfo(m.genCardInfo());
			board[x][y].updateImage(m.getImage());
			board[x][y].summonCheck();
			board[x][y].updateDisplay();
			if (x >= 2)
				Match.myOrder.add(coordinates);
			else
				Match.opponentOrder.add(coordinates);
			return true;
		}
		else return false;
	}
	
	public static void addHandListener() {
		for (int i = 0; i < 5; i++) {
			Listener l = genHandListener(i);
			myHand.get(i).actionButton.addListener(SWT.Selection, l);
		}
	}
	
	public static Listener genHandListener(int i) {
		return e -> {
			if (endTurn) reject("Your Turn is over.");
			else if (selectedSkillActivation) reject("Invalid Skill Target");
			else if (myHand.get(i).isEmpty()) reject("You cannot summon from an empty slot");
			else if (!selectedSummon) {
				cardSummon = i;
				selectedSummon = true;
				logDisplay.append("Summon:\n" + myHand.get(i).toString());
			}
			else reject("Illegal Action");
		};
	}
	
	public static void addAllySlotListener() {
		for (int i = 2; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				Listener l = genAllySlotListener(i, j);
				board[i][j].actionButton.addListener(SWT.Selection, l);
			}
		}
	}
	
	public static Listener genAllySlotListener(int i, int j) {
		return e -> {
			if (endTurn) reject("Your Turn is over.");
			else if (selectedSkillActivation) {
				if (targetSkillActivation.equals("AllyEmpty")) {
					if (board[i][j].isEmpty()) {
						int x = cardSkillActivation[0], y = cardSkillActivation[1];
						coordinatesTemp[0] = i; coordinatesTemp[1] = j;
						board[x][y].spellCheckWithSelectedCoordinates();
						selectedSkillActivation = false;
					}
					else reject("An empty slot is required.");
				}
				else reject("Cannot target your allies.");
			}
			else if (selectedSummon) {
				if (!board[i][j].isEmpty()) reject("Cannot summon to a occupied space");
				else {
					int[] coordinates = new int[2];
					coordinates[0] = i; coordinates[1] = j;
					if(summon(myHand.get(cardSummon), coordinates)) {
						myHand.get(cardSummon).setEmpty();
						selectedSummon = false;
						logDisplay.append("Summon to: (" + i + ", " + j + ").\n");
					}
				}
			}
			else if (!selectedSkillActivation) {
				cardSkillActivation[0] = i;
				cardSkillActivation[1] = j;
				selectedSkillActivation = board[i][j].spellCheck();
			}
			else reject("Illegal Action");
		};
	}
	
	public static void addEnemySlotListener() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				Listener l = genEnemySlotListener(i, j);
				board[i][j].actionButton.addListener(SWT.Selection, l);
			}
		}
	}
	
	public static Listener genEnemySlotListener(int i, int j) {
		return e -> {
			if (endTurn) reject("Your Turn is over.");
			else if (selectedSummon) reject("Cannot summon into opponent's slots!");
			else if (selectedSkillActivation) {
				if (targetSkillActivation.equals("Enemy")) {
					if (board[i][j].isEmpty()) reject("Cannot target a slot where it is empty.");
					else if (i == 0 && !board[1][j].isEmpty()) 
						reject("This target cannot be attacked");
					else {
						int x = cardSkillActivation[0], y = cardSkillActivation[1];
						coordinatesTemp[0] = i; coordinatesTemp[1] = j;
						board[x][y].spellCheckWithSelectedCoordinates();
						selectedSkillActivation = false;
					}
				}
				else reject("An non-empty enemy slot is required.");
			}
			else reject("Illegal Action");
		};
	}
	
	public static Composite initializeBattleLogPanel() {
		Composite bl = new Composite(shell, SWT.EMBEDDED);
		java.awt.Frame TextFrame = SWT_AWT.new_Frame(bl);
		logDisplay = new JTextArea();
		logDisplay.setEditable(false);
		logDisplay.setText("Battle Start!\n");
		javax.swing.JScrollPane panel = new javax.swing.JScrollPane(logDisplay);
		TextFrame.add(panel);
		bl.pack();
		java.awt.Font font = new java.awt.Font("Times New Roman", Font.PLAIN, bl.getSize().y);
		logDisplay.setFont(font);
		return bl;
	}
	
	public static void buildDeck(int[] Deck) {
		for (int i = 0; i < 15; i++) {
			myDeck.add(findCardByID(Deck[i]).clone());
		}
	}
	
	public static Card findCardByID (int id) {
		for (int i = 0; i < cardList.size(); i++){
			if (cardList.get(i).matchID(id)) return cardList.get(i).clone();
		}
		return new Card();
	}
	
	public static void processDestroiedCard(int[] coordinates) {
		int x = coordinates[0], y = coordinates[1];
		if (x >= 2) {
			int id = board[x][y].getID();
			if (id % 1000 < 300)
				myDeck.add(findCardByID(id).clone());
			if (board[x][y].isActive()) {
				spGen -= board[x][y].spGen;
				vpGen -= board[x][y].vpGen;
				VPGenlabel.setText("Current VP gen: " + vpGen);
			}
			spCost -= board[x][y].spCost;
			SPCostlabel.setText("Current SP cost: " + spCost);
			int k = 0;
			while (k < myOrder.size()) {
				int i = myOrder.get(k)[0], j = myOrder.get(k)[1];
				if (i == x && j == y) myOrder.remove(k);
				else k++;
			}
		}
		else {
			int id = board[x][y].getID();
			if (id % 1000 < 300) {
				AIshuffleInAcard(id);
			}
			if (board[x][y].isActive()) {
				opponentspGen -= board[x][y].spGen;
				opponentvpGen -= board[x][y].vpGen;
				opponentVPGenlabel.setText("Opponent VP gen: " + opponentvpGen);
			}
			opponentspCost -= board[x][y].spCost;
			opponentSPCostlabel.setText("Opponent SP cost: " + opponentspCost);
			int k = 0;
			while (k < opponentOrder.size()) {
				int i = opponentOrder.get(k)[0], j = opponentOrder.get(k)[1];
				if (i == x && j == y) opponentOrder.remove(k);
				else k++;
			}
		}
	}
	
	public static void myPreTurn() {
		spGen = 0;
		fillMyHand();
		MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
		dialog.setText("Notification");
		dialog.setMessage("It's Your Turn To Move!");
		dialog.open();
		for (int i = 0; i < myOrder.size(); i++) {
			int[] coordinates = myOrder.get(i);
			int x = coordinates[0], y = coordinates[1];
			board[x][y].preTurnCheck();
		}
		SP = 1 + spGen - spCost;
		SPlabel.setText("Remaining SP: " + SP);
		if (SP < 0) {
			SP = 0;
			markAllyMonstersInactive();
			logDisplay.append("Insufficient SP, All Ally Monsters cannot cast spell");
		}
	}
	
	public static void markAllyMonstersInactive() {
		for (int i = 0; i < myOrder.size(); i++) {
			int x = myOrder.get(i)[0], y = myOrder.get(i)[1];
			if (board[x][y].isMonster()) board[x][y].markSPInactive();
		}
	}
	
	public static void myAftTurn() {
		vpGen = 0;
		for (int i = 0; i < myOrder.size(); i++) {
			int[] coordinates = myOrder.get(i);
			int x = coordinates[0], y = coordinates[1];
			board[x][y].aftTurnCheck();
		}
		VP = VP + vpGen;
		VPlabel.setText("VP: " + VP);
		VPGenlabel.setText("Current VP gen: " + vpGen);
		if (VP >= 100) win();
	}
	
	public static void opponentPreTurn() {
		opponentspGen = 0;
		for (int i = 0; i < opponentOrder.size(); i++) {
			int[] coordinates = opponentOrder.get(i);
			int x = coordinates[0], y = coordinates[1];
			board[x][y].preTurnCheck();
		}
		opponentSP = 1 + opponentspGen - opponentspCost;
		opponentSPlabel.setText("Opponent SP: " + opponentSP);
		if (opponentSP < 0) {
			opponentSP = 0;
		}
		AIturn();
	}
	
	public static int AIdrawAcard() {
		if (AIdeck.length == 0) return 0;
		else {
			Random r = new Random();
			int t = r.nextInt(AIdeck.length);
			int[] newDeck = new int[AIdeck.length-1];
			for(int i = 0; i < t; i++) {
				newDeck[i] = AIdeck[i];
			}
			for(int i = t+1; i < AIdeck.length; i++) {
				newDeck[i-1] = AIdeck[i];
			}
			t = AIdeck[t];
			AIdeck = newDeck;
			return t;
		}
	}
	
	public static void AIshuffleInAcard(int id) {
		int[] newDeck = new int[AIdeck.length+1];
		for(int i = 0; i < AIdeck.length; i++) {
			newDeck[i] = AIdeck[i];
		}
		newDeck[AIdeck.length] = id;
		AIdeck = newDeck;
	}
	
	public static void AIturn() {
		int[] hand = new int[5];
		for (int i = 0; i < 5; i++) {
			if(AIhand[i] == 0) AIhand[i] = AIdrawAcard();
			hand[i] = AIhand[i];
			System.out.println(AIhand[i]);
		}
		System.out.println(AIdeck.length);
		VirtualCard[][] virtualBoard = new VirtualCard[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (board[i][j].isEmpty())
					virtualBoard[i][j] = new VirtualCard();
				else 
					virtualBoard[i][j] = board[i][j].genVirtualCard();
			}
		}
		ArrayList<String> movements = AI.search(virtualBoard, hand, opponentSP);
		System.out.println(movements);
		
		for (int i = 0; i < movements.size(); i++) {
			String movement = movements.get(i);
			long time = i * 3000;
			boolean last = (i == movements.size() - 1);
			new Thread() {
				@Override
				public void run() {
					try {
						sleep(time);
					} catch (InterruptedException e) {e.printStackTrace();}
					display.syncExec(new Runnable () {
						@Override
						public void run(){
							reflectAI(movement);
						}
					});
					if (last) {
						try {
							sleep(3000);
						} catch (InterruptedException e) {e.printStackTrace();}
						display.syncExec(new Runnable () {
							@Override
							public void run(){
								opponentEndTurn();
							}
						});
					}
				}
			}.start();
		}
	}
	
	public static void reflectAI(String movement) {
		String[] s = movement.split(" ");
		if (s[0].equals("activate")) {
			int[] coordinates = new int[2];
			coordinates[0] = Integer.valueOf(s[1]) / 4;
			coordinates[1] = Integer.valueOf(s[1]) % 4;
			if (s.length > 2) {
				int[] target = new int[2];
				target[0] = Integer.valueOf(s[2]) / 4;
				target[1] = Integer.valueOf(s[2]) % 4;
				opponentSkillActivation(coordinates, target);
			}
			else {
				opponentSkillActivation(coordinates);
			}
		}
		else if (s[0].equals("summon")) {
			int k = Integer.valueOf(s[1]); 
			int[] coordinates = new int[2];
			coordinates[0] = Integer.valueOf(s[2]) / 4;
			coordinates[1] = Integer.valueOf(s[2]) % 4;
			opponentSummon(k, coordinates);
		}
	}
	
	public static void opponentAftTurn() {
		opponentvpGen = 0;
		for (int i = 0; i < opponentOrder.size(); i++) {
			int[] coordinates = opponentOrder.get(i);
			int x = coordinates[0], y = coordinates[1];
			board[x][y].aftTurnCheck();
		}
		opponentVP = opponentVP + opponentvpGen;
		opponentVPlabel.setText("Opponent VP: " + opponentVP);
		opponentVPGenlabel.setText("Opponent VP gen: " + opponentvpGen);
		if (opponentVP >= 100) lose();
	}
	
	public static void opponentEndTurn() {
		opponentAftTurn();
		if(!shell.isDisposed()) {
			endTurn = false;
			myPreTurn();
		}
	}
	
	public static void opponentSkillActivation(int[] c) {
		int x = c[0], y = c[1];
		board[x][y].spellCheck();
	}
	
	public static void opponentSkillActivation(int[] c, int[] t) {
		int x = c[0], y = c[1];
		coordinatesTemp[0] = t[0];
		coordinatesTemp[1] = t[1];
		board[x][y].spellCheckWithSelectedCoordinates();
	}
	
	public static void opponentSummon(int i, int[] t) {
		int mID = AIhand[i];
		int[] coordinates = new int[2];
		coordinates[0] = t[0]; coordinates[1] = t[1];
		summon(Match.findCardByID(mID), coordinates);
		AIhand[i] = 0;
	}
	
	public static ArrayList<int[]> selectAllEnemyMonsters() {
		ArrayList<int[]> l = new ArrayList<int[]>();
		for (int i = 0; i < opponentOrder.size(); i++) {
			int x = opponentOrder.get(i)[0], y = opponentOrder.get(i)[1];
			if (board[x][y].isMonster()) l.add(opponentOrder.get(i));
		}
		return l;
	}
	
	public static ArrayList<int[]> selectAllAttackableEnemies() {
		ArrayList<int[]> l = new ArrayList<int[]>();
		for (int i = 0; i < 4; i++) {
			int[] cordinates = new int[2];
			cordinates[0] = 0; cordinates[1] = i;
			if (!board[1][i].isEmpty()) {cordinates[0] = 1; l.add(cordinates);}
			else if (!board[0][i].isEmpty())
				l.add(cordinates);
		}
		return l;
	}

	public static ArrayList<int[]> selectAllAllyMonsters() {
		ArrayList<int[]> l = new ArrayList<int[]>();
		for (int i = 0; i < myOrder.size(); i++) {
			int x = myOrder.get(i)[0], y = myOrder.get(i)[1];
			if (board[x][y].isMonster()) l.add(myOrder.get(i));
		}
		return l;
	}

	public static ArrayList<int[]> selectAllAttackableAllies() {
		ArrayList<int[]> l = new ArrayList<int[]>();
		for (int i = 0; i < 4; i++) {
			int[] cordinates = new int[2];
			cordinates[0] = 3; cordinates[1] = i;
			if (!board[2][i].isEmpty()) {cordinates[0] = 2; l.add(cordinates);}
			else if (!board[3][i].isEmpty())
				l.add(cordinates);
		}
		return l;
	}

	public static void startMatch(int deck[], Display mainDisplay) {
		
		endTurn = false;
		
		board = new Card[4][4];
		myHand = new ArrayList<Card>();
		myDeck = new ArrayList<Card>();
		
		cardList = Card.cardLoader();
		
		VP = 0;
		SP = 1;
		
		opponentVP = 0;
		opponentSP = 1;
		
		spGen = 0;
		spCost = 0;
		vpGen = 0;
		
		opponentspGen = 0;
		opponentspCost = 0;
		opponentvpGen = 0;
		
		coordinatesTemp = new int[2];
		
		myOrder = new ArrayList<int[]>();
		opponentOrder = new ArrayList<int[]>();
		
		display = mainDisplay;
		shell = new Shell(display);
		
		selectedSummon = false;
		cardSummon = 0;
		
		selectedSkillActivation = false;
		targetSkillActivation = "";
		cardSkillActivation = new int[2];
		
		logDisplay = new JTextArea();
		
		buildDeck(deck);
		
		Composite c1 = initializeBoard();
		Label s1 = new Label(shell, SWT.SEPARATOR | SWT.VERTICAL);
		Composite c2 = initializeBattleLogPanel();
		Label s2 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		Composite c3 = initializeFunctionButtons();
		Composite c4 = fillMyHandFirstTime();
		
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = layout.marginHeight = 5;
		layout.horizontalSpacing = layout.verticalSpacing = 5;
		shell.setLayout(layout);
		
		GridData data;
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		data.verticalSpan = 4;
		c1.setLayoutData(data);
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		data.verticalSpan = 4;
		s1.setLayoutData(data);
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		data.verticalSpan = 1;
		data.heightHint = 300;
		c2.setLayoutData(data);
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		data.verticalSpan = 1;
		s2.setLayoutData(data);
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		data.verticalSpan = 1;
		c3.setLayoutData(data);
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		data.verticalSpan = 1;
		c4.setLayoutData(data);
		
		shell.setLocation(0, 0);
		shell.pack();
		shell.open();

	}
}

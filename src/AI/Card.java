package AI;

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


public class Card{
	
	private java.awt.Image image;
	
	private String cardName;
	private String inGameDescription1;
	private String inGameDescription2;
	private int identifier;
	private int mp; private int mpMax;
	private int hp; private int hpMax;
	private String preTurn;
	private String atSummon;
	private String skillActivate;
	private String afterTurn;
	
	public int spGen = 0;
	public int spCost = 0;
	public int vpGen = 0;
	
	private boolean hold = false;
	private int turns = 0;
	private String backupName = "";
	private boolean skillUsed = false;
	private boolean shielded = false;
	private int shieldedTurns = 0;
	
	public Composite comp;
	public Button actionButton;
	public Label labelMP;
	public Label labelHP;
	public javax.swing.JLabel Imglabel;
	
	private int frameHeight;
	private int frameWidth;
	
	public Card() {
		setEmpty();
	}

	public Card(String[] cardInfo) {
		loadCardInfo(cardInfo);
		File imgFile = new File("cards/" + cardInfo[0] + ".jpg");
		java.awt.Image img;
		try {
			img = javax.imageio.ImageIO.read(imgFile);
			image = img;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadCardInfo(String[] cardInfo) {
		cardName = cardInfo[0]; 
		inGameDescription1 =cardInfo[1];
		inGameDescription2 =cardInfo[2]; 
		identifier = Integer.valueOf(cardInfo[3]);
		mp = mpMax = Integer.valueOf(cardInfo[4]); 
		hp = hpMax = Integer.valueOf(cardInfo[5]);
		preTurn = cardInfo[6];
		atSummon = cardInfo[7];
		skillActivate = cardInfo[8];
		afterTurn = cardInfo[9];
	}
	
	public String[] genCardInfo () {
		String[] cardInfo = new String[10];
		cardInfo[0] = cardName;
		cardInfo[1] = inGameDescription1;
		cardInfo[2] = inGameDescription2;
		cardInfo[3] = Integer.toString(identifier);
		cardInfo[4] = Integer.toString(mpMax); 
		cardInfo[5] = Integer.toString(hpMax);
		cardInfo[6] = preTurn;
		cardInfo[7] = atSummon;
		cardInfo[8] = skillActivate;
		cardInfo[9] = afterTurn;
		return cardInfo;
	}
	
	public Card clone() {
		return new Card(genCardInfo());
	}
	
	public void setEmpty() {
		cardName = "Empty Slot";
		inGameDescription1 = "No Card";
		inGameDescription2 = "";
		identifier = 0;
		mp = 0; mpMax = 0;
		hp = 0; hpMax = 0;
		preTurn = "0";
		atSummon = "0";
		skillActivate = "0";
		afterTurn = "0";
		image = null;
		File imgFile = new File("cards/empty.jpg");
		try {
			image = javax.imageio.ImageIO.read(imgFile);
		} catch (IOException e) {e.printStackTrace();}
		if (comp != null) {
			actionButton.setText("Empty Slot");
			labelMP.setText("");
			labelHP.setText("");
			javax.swing.ImageIcon icon = new javax.swing.ImageIcon();
			icon.setImage(image.getScaledInstance(frameHeight, frameWidth, Image.SCALE_FAST));
			Imglabel.setIcon(icon);
		}
		
		spGen = 0;
		spCost = 0;
		vpGen = 0;
		
		hold = false;
		turns = 0;
		backupName = "";
		skillUsed = false;
		shielded = false;
		shieldedTurns = 0;
	}
	
	public boolean isEmpty() {
		return identifier == 0;
	}
	
	public boolean isMonster() {
		return identifier >= 200;
	}
	
	public boolean isActive() {
		return !(hold && turns != 0);
	}
	
	public int getID() {
		return identifier;
	}
	
	public boolean matchID(int id) {
		return id == identifier;
	}
	
	public void updateImage(java.awt.Image img) {
		image = img;
	}
	
	public java.awt.Image getImage() {
		return image;
	}
	
	public void markSPInactive() {
		hold = true;
		turns = 1;
		backupName = cardName;
		cardName = "Inactive";
		updateDisplay();
	}
	
	public String toString() {
		String text = "";
		text += "Card Name: " + cardName + "\n";
		text += inGameDescription1 + "\n" + inGameDescription2 + "\n";
		text += "mp: " + mp + "/" + mpMax + "\n";
		text += "hp: " + hp + "/" + hpMax + "\n";
		if (hold && turns != 0) 
			text += "this building takes another " + turns + " turns to finish.\n";
		if (hold && turns == 0) 
			text += "this monster is inactive due to low on SP.\n";
		return text;
	}
	
	public void preTurnCheck() {
		if (!isActive()) { 
			turns -= 1; 
			if (turns > 0) {
				cardName = "Finishing in " + turns;
				updateDisplay();
				return;
			}
			else {
				hold = false;
				cardName = backupName;
				backupName = "";
				File imgFile = new File("cards/" + cardName + ".jpg");
				try {
					image = javax.imageio.ImageIO.read(imgFile);
				} catch (IOException e) {e.printStackTrace();}
				updateDisplay();
				Match.logDisplay.append(cardName + " has finished building.\n");
			}
		}
		if (shielded && shieldedTurns >= 1)  
		{shieldedTurns--; if (shieldedTurns == 0) shielded = false;}
		String[] s = preTurn.split(" ");
		int n = Integer.valueOf(s[0]);
		int k = 1;
		for (int i = 0; i < n; i++) {
			if (s[k].equals("spGen")) {
				k++; spGen = Integer.valueOf(s[k]); 
				if (!Match.endTurn) 
					Match.spGen += spGen; 
				else 
					Match.opponentspGen += spGen; 
				k++;
				Match.logDisplay.append(cardName + " is providing: " + spGen + "pts of SP.\n");
			}
		}
	}
	
	public boolean summonSPCheck() {	
		String[] s = atSummon.split(" ");					
		if(!s[0].equals("0") && s[1].equals("spCost")) {
			spCost = Integer.valueOf(s[2]);
			if (!Match.endTurn && spCost > Match.SP) 
			{Match.reject("Not enough sp"); return false;}
		}
		return true;
	}
	
	public void summonCheck() {
		String[] s = atSummon.split(" ");
		int n = Integer.valueOf(s[0]);
		int k = 1;
		for (int i = 0; i < n; i++) {
			if (s[k].equals("spCost")) {
				k++; spCost = Integer.valueOf(s[k]); k++;
				if (!Match.endTurn)	{
					Match.SP -= spCost; Match.spCost += spCost;
					Match.SPlabel.setText("Remaining SP: " + Match.SP);
					Match.SPCostlabel.setText("Current SP cost: " + Match.spCost);
					Match.logDisplay.append(cardName + " cost " + spCost + "pts of SP.\n");
				}
				else {
					Match.opponentSP -= spCost; Match.opponentspCost += spCost;
					Match.opponentSPlabel.setText("Opponent SP: " + Match.opponentSP);
					Match.opponentSPCostlabel.setText("Opponent SP cost: " + Match.opponentspCost);
					Match.logDisplay.append(cardName + " cost " + spCost + "pts of SP.\n");
				}
			}
			else if (s[k].equals("buildUnf")) {
				k++; hold = true; turns = Integer.valueOf(s[k]); k++;
				backupName = cardName;
				cardName = "Finishing in " + turns;
				File imgFile = new File("cards/unf.jpg");
				try {
					image = javax.imageio.ImageIO.read(imgFile);
				} catch (IOException e) {e.printStackTrace();}
				Match.logDisplay.append("A Building Finishing in: " + turns + " turns.\n");
			}
		}
	}
	
	public boolean spellCheck() {
		if (!isActive()) {Match.reject("This card is inactive."); return false;}
		if (skillUsed) {Match.reject("Skill used"); return false;}
		String[] s = skillActivate.split(" ");
		int n = Integer.valueOf(s[0]);
		if (n == 0) {Match.reject("No skill can be activated"); return false;}
		boolean skillRequiresTarget = Boolean.valueOf(s[1]);
		if (!skillRequiresTarget) {
			int k = 2;
			Match.logDisplay.append(cardName + " activates its skill:\n");
			for (int i = 0; i < n; i++) {
				if (s[k].equals("damage")) {
					k++;
					if (s[k].equals("self")) { 
						k++; 
						if (!mpDeduct(Integer.valueOf(s[k]))) return false;
						k++; hpDeduct(Integer.valueOf(s[k]));
						k++; updateDisplay();
					}
					else if (s[k].equals("all")) {
						ArrayList<int[]> coordinates;
						if (!Match.endTurn) coordinates = Match.selectAllAttackableEnemies();
						else coordinates = Match.selectAllAttackableAllies();
						k++; int mpDmg = Integer.valueOf(s[k]);
						k++; int hpDmg = Integer.valueOf(s[k]);
						k++;
						for (int j = 0; j < coordinates.size(); j++) {
							int x = coordinates.get(j)[0], y = coordinates.get(j)[1];
							Match.board[x][y].mpDeduct(mpDmg);
							if (Match.board[x][y].hpDeduct(hpDmg)) {
								Match.processDestroiedCard(coordinates.get(j));
								Match.board[x][y].setEmpty();
							} else Match.board[x][y].updateDisplay();
						}
					}
				}
				else if (s[k].equals("heal")) {
					k++;
					if (s[k].equals("self")) { 
						k++; mpRecover(Integer.valueOf(s[k]));
						k++; hpRecover(Integer.valueOf(s[k]));
						k++; updateDisplay();
					}
					else if (s[k].equals("all")) {
						ArrayList<int[]> coordinates;
						if (!Match.endTurn) coordinates = Match.selectAllAllyMonsters();
						else coordinates = Match.selectAllEnemyMonsters();
						k++; int mpRec = Integer.valueOf(s[k]);
						k++; int hpRec = Integer.valueOf(s[k]);
						k++;
						for (int j = 0; j < coordinates.size(); j++) {
							int x = coordinates.get(j)[0], y = coordinates.get(j)[1];
							Match.board[x][y].mpRecover(mpRec);
							Match.board[x][y].hpRecover(hpRec);
							Match.board[x][y].updateDisplay();
						}
					}
				}
				else if (s[k].equals("shield")) {
					shielded = true; 
					k++; shieldedTurns = Integer.valueOf(s[k]);
					k++;
				}
			}
			if (!Match.endTurn) {
				int w = Match.cardSkillActivation[0], z = Match.cardSkillActivation[1];
				Match.board[w][z].updateDisplay();
				skillUsed = true;
			}
			return false;
		}
		else {
			Match.targetSkillActivation = s[2];
			Match.logDisplay.append("Please select a target for the skill.\n");
			return true;
		}
	}
	
	public void spellCheckWithSelectedCoordinates() {
		if (!isActive()) {Match.reject("This Card is Inactive."); return;}
		if (skillUsed) {Match.reject("Skill Already Used."); return;}
		String[] s = skillActivate.split(" ");
		int n = Integer.valueOf(s[0]);
		Match.logDisplay.append(cardName + " activates its skill:\n");
		int k = 3;
		for (int i = 0; i < n; i++) {
			if (s[k].equals("damage")) {
				k++;
				if (s[k].equals("self")) {
					k++; if (!mpDeduct(Integer.valueOf(s[k]))) return;
					k++; hpDeduct(Integer.valueOf(s[k]));
					k++; updateDisplay();
				}
				else if (s[k].equals("single")) {
					int[] coordinates = new int[2];
					int x = coordinates[0] = Match.coordinatesTemp[0];
					int y = coordinates[1] = Match.coordinatesTemp[1];
					k++; Match.board[x][y].mpDeduct(Integer.valueOf(s[k]));
					k++; 
					if (Match.board[x][y].hpDeduct(Integer.valueOf(s[k]))) {
						Match.processDestroiedCard(coordinates);
						Match.board[x][y].setEmpty();
					} else Match.board[x][y].updateDisplay();
					k++;
				}
				else if (s[k].equals("column")) {
					int[] coordinates = new int[2];
					int x = coordinates[0] = Match.coordinatesTemp[0];
					int y = coordinates[1] = Match.coordinatesTemp[1];
					k++; int mpDmg = Integer.valueOf(s[k]);
					k++; int hpDmg = Integer.valueOf(s[k]);
					k++;
					
					Match.board[x][y].mpDeduct(mpDmg);
					if (Match.board[x][y].hpDeduct(hpDmg)) {
						Match.processDestroiedCard(coordinates);
						Match.board[x][y].setEmpty();
					} else Match.board[x][y].updateDisplay();
					
					if (!Match.endTurn) coordinates[0] = x = (x == 0)? 1:0;
					else coordinates[0] = x = (x == 2)? 3:2;
					
					if (!Match.board[x][y].isEmpty()) {
						Match.board[x][y].mpDeduct(mpDmg);
						if (Match.board[x][y].hpDeduct(hpDmg)) {
							Match.processDestroiedCard(coordinates);
							Match.board[x][y].setEmpty();
						} else Match.board[x][y].updateDisplay();
					}
				}
				else if (s[k].equals("all")) {
					ArrayList<int[]> coordinates;
					if (!Match.endTurn) coordinates = Match.selectAllAttackableEnemies();
					else coordinates = Match.selectAllAttackableAllies();
					k++; int mpDmg = Integer.valueOf(s[k]);
					k++; int hpDmg = Integer.valueOf(s[k]);
					k++;
					for (int j = 0; j < coordinates.size(); j++) {
						int x = coordinates.get(j)[0], y = coordinates.get(j)[1];
						Match.board[x][y].mpDeduct(mpDmg);
						if (Match.board[x][y].hpDeduct(hpDmg)) {
							Match.processDestroiedCard(coordinates.get(j));
							Match.board[x][y].setEmpty();
						} else Match.board[x][y].updateDisplay();
					}
				}
			}
			else if (s[k].equals("heal")) {
				k++;
				if (s[k].equals("self")) { 
					k++; mpRecover(Integer.valueOf(s[k]));
					k++; hpRecover(Integer.valueOf(s[k]));
					k++; updateDisplay();
				}
				else if (s[k].equals("all")) {
					ArrayList<int[]> coordinates;
					if (!Match.endTurn) coordinates = Match.selectAllAllyMonsters();
					else coordinates = Match.selectAllEnemyMonsters();
					k++; int mpRec = Integer.valueOf(s[k]);
					k++; int hpRec = Integer.valueOf(s[k]);
					k++;
					for (int j = 0; j < coordinates.size(); j++) {
						int x = coordinates.get(j)[0], y = coordinates.get(j)[1];
						Match.board[x][y].mpRecover(mpRec);
						Match.board[x][y].hpRecover(hpRec);
						Match.board[x][y].updateDisplay();
					}
				}
			}
			else if (s[k].equals("summon")) {
				k++; int cardID = Integer.valueOf(s[k]); k++;
				Card c = Match.findCardByID(cardID);
				int[] coordinates = new int[2];
				coordinates[0] = Match.coordinatesTemp[0];
				coordinates[1] = Match.coordinatesTemp[1];
				if(Match.summon(c, coordinates)) {
					Match.logDisplay.append("Monster Summoned\n");
				}
			}
		}
		if (!Match.endTurn) {
			int w = Match.cardSkillActivation[0], z = Match.cardSkillActivation[1];
			Match.board[w][z].updateDisplay();
			skillUsed = true;
		}
	}
	
	public void aftTurnCheck() {
		if (!isActive()) return;
		String[] s = afterTurn.split(" ");
		int n = Integer.valueOf(s[0]);
		int k = 1;
		for (int i = 0; i < n; i++) {
			if (s[k].equals("vpGen")) {
				k++; vpGen = Integer.valueOf(s[k]);
				Match.logDisplay.append(cardName + " provides:" + vpGen + "pts of VP.\n");
				if (!Match.endTurn) {
					Match.vpGen += vpGen;
				}
				else {
					Match.opponentvpGen += vpGen;
				}
				k++;
			}
			else if (s[k].equals("heal")) {
				k++; 
				if (s[k].equals("self")) {
					k++; mpRecover(Integer.valueOf(s[k]));
					k++; hpRecover(Integer.valueOf(s[k]));
					k++; updateDisplay();
				}
			}
		}
		skillUsed = false;
	}

	public boolean hpDeduct(int n) {
		if (n == 0) return false;
		Match.logDisplay.append(cardName + " takes " + n + " damage.\n");
		if (shielded) {
			hp--; Match.logDisplay.append("This monster is shielded, damage reduce to 1.\n");
		}
		else hp = hp - n;
		if (hp <= 0) {
			Match.logDisplay.append(cardName + " is dead.\n");
			return true;
		}
		else return false;
	}
	
	public void hpRecover(int n) {
		if (n != 0) Match.logDisplay.append(cardName + " recovers for " + n + " HP.\n");
		if (hp + n > hpMax) hp = hpMax;
		else hp = hp + n;
	}
	
	public boolean mpDeduct(int n) {
		if (!Match.endTurn && mp - n < 0) {
			Match.reject("Not enough Mana"); 
			return false;
		}
		else {
			mp = mp - n;
			if (n != 0) Match.logDisplay.append(cardName + " uses " + n + " mana.\n");
			return true;
		}
	}
	
	public void mpRecover(int n) {
		if (n != 0)Match.logDisplay.append(cardName + " recovers " + n + " mana.\n");
		if (mp + n > mpMax) mp = mpMax;
		else mp = mp + n;
	}
	
	public static ArrayList<Card> cardLoader() {
		ArrayList<Card> cardList = new ArrayList<Card>();
		try {
			File cardFile = new File("cards/cards.txt");
			BufferedReader reader = new BufferedReader(new FileReader(cardFile));
			while (reader.ready()) {
				String[] cardInfo = new String[10];
				for (int i = 0; i < 10 && reader.ready(); i++) {
					cardInfo[i] = reader.readLine();
				}
				cardList.add(new Card(cardInfo));
			}
			reader.close();
		} catch (IOException e) {e.printStackTrace();}
		return cardList;
	}
	
	public Composite genImage(Composite shell) {
		comp = new Composite(shell, SWT.EMBEDDED);
		
		Composite imgComp = new Composite(comp, SWT.EMBEDDED);
		java.awt.Frame frame = SWT_AWT.new_Frame(imgComp);
		
		Composite labelComp = new Composite(comp, SWT.EMBEDDED);
		actionButton = new Button(labelComp, SWT.PUSH | SWT.CENTER);
		labelMP = new Label(labelComp, SWT.BORDER);
		labelHP = new Label(labelComp, SWT.BORDER);
		
		Imglabel = new javax.swing.JLabel();
		
		
		
		frame.add(Imglabel);
		
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = 5;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 5;
		comp.setLayout(layout);
		
		layout = new GridLayout(2, true);
		layout.marginWidth = layout.marginHeight = 0;
		layout.horizontalSpacing = layout.verticalSpacing = 0;
		labelComp.setLayout(layout);
		
		GridData data;
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.heightHint = 100;
		data.widthHint = 100;
		imgComp.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.heightHint = 50;
		data.widthHint = 100;
		labelComp.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		actionButton.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		labelHP.setLayoutData(data);

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		labelMP.setLayoutData(data);
		
		comp.pack();
		
		frameHeight = frame.getHeight();
		frameWidth = frame.getHeight();
		
		updateDisplay();
		
		return comp;
	}
	
	public void updateDisplay() {
		if (mpMax != 0 && !hold) labelMP.setText("MP:" + mp);
		if (hpMax != 0) labelHP.setText("HP:" + hp);
		actionButton.setText(cardName);
		javax.swing.ImageIcon icon = new javax.swing.ImageIcon();
		icon.setImage(image.getScaledInstance(frameHeight, frameWidth, Image.SCALE_FAST));
		Imglabel.setIcon(icon);
	}
	
	public VirtualCard genVirtualCard() {
		return new VirtualCard(identifier, mp, hp, skillUsed || hold, shielded);
	}
	
}
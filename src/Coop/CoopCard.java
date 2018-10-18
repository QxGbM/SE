package Coop;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CoopCard {
	
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
	
	public boolean hold = false;
	public int turns = 0;
	public boolean skillUsed = false;
	public boolean shielded = false;
	public int shieldedTurns = 0;
	public int spCost = 0;
	public int vpGen = 0;
	
	public Composite comp;
	public Button actionButton;
	public Label labelMP;
	public Label labelHP;
	public javax.swing.JLabel Imglabel;
	
	private int frameHeight;
	private int frameWidth;
	
	
	public CoopCard() {
		setEmpty();
	}
	
	public CoopCard(String[] cardInfo) {
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
		loadSPCost();
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
		
		spCost = 0;
		vpGen = 0;
		
		hold = false;
		turns = 0;
		skillUsed = false;
		shielded = false;
		shieldedTurns = 0;
	}
	
	public boolean isEmpty() {
		return identifier == 0;
	}
	
	public boolean consumesSP() {
		return spCost > 0;
	}
	
	public boolean isMonster() {
		return (identifier % 1000 >= 200) && (identifier % 1000 <= 400);
	}
	
	public boolean isToken() {
		return (identifier % 1000 >= 100) && (identifier % 1000 < 300);
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
	
	public CoopCard clone() {
		return new CoopCard(genCardInfo()); 
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
		
		
		
		updateInfo();
		updateGraphics();
		
		return comp;
	}
	
	public void updateInfo() {
		if (mpMax != 0 && !hold) labelMP.setText("MP:" + mp);
		if (hpMax != 0) labelHP.setText("HP:" + hp);
		String s = cardName;
		if (hold) {
			s += " (" + turns + ")";
		}
		actionButton.setText(s);
	}
	
	public void updateGraphics() {
		javax.swing.ImageIcon icon = new javax.swing.ImageIcon();
		icon.setImage(image.getScaledInstance(frameHeight, frameWidth, Image.SCALE_FAST));
		Imglabel.setIcon(icon);
	}
	
	public Skill parseSkill() {
		String[] s = skillActivate.split(" ");
		int n = Integer.valueOf(s[0]);
		if (n == 0) {Coop.reject("No skill can be activated"); return new Skill(new String[0]);}
		else {
			String[] effects = new String[n];
			boolean requiresTarget = Boolean.valueOf(s[1]);
			int i = 2, t = 0;
			if (requiresTarget) i++;
			while (i < s.length && t < n) {
				if(s[i].equals("damage") || s[i].equals("heal")) {
					effects[t] = s[i] + " " +s[i+1] + " " + s[i+2] + " " + s[i+3];
					i += 4;
					t ++;
				}
				else if(s[i].equals("summon") || s[i].equals("shield")){
					effects[t] = s[i] + " " +s[i+1];
					i += 2;
					t ++;
				}
				else {
					System.out.println("skill parse error: " + s[i]); break;
				}
			}
			if (requiresTarget) return new Skill(s[2], effects);
			else return new Skill(effects);
		}
	}
	
	public Skill parsePreturnEffects() {
		String[] s = preTurn.split(" ");
		int n = Integer.valueOf(s[0]);
		if (n == 0) {return new Skill(new String[0]);}
		else {
			String[] effects = new String[n];
			int i = 1, t = 0;
			while (i < s.length && t < n) {
				if(s[i].equals("spGen")) {
					effects[t] = s[i] + " " +s[i+1];
					i += 2;
					t ++;
				}
				else {
					System.out.println("preturn effect parse error: " + s[i]); break;
				}
			}
			return new Skill(effects);
		}
	}
	
	public Skill parseAftturnEffects() {
		String[] s = afterTurn.split(" ");
		int n = Integer.valueOf(s[0]);
		if (n == 0) {return new Skill(new String[0]);}
		else {
			String[] effects = new String[n];
			int i = 1, t = 0;
			while (i < s.length && t < n) {
				if(s[i].equals("vpGen")) {
					effects[t] = s[i] + " " +s[i+1];
					vpGen = Integer.valueOf(s[i+1]);
					i += 2;
					t ++;
				}
				else if(s[i].equals("heal")) {
					effects[t] = s[i] + " " +s[i+1] + " " + s[i+2] + " " + s[i+3];
					i += 4;
					t ++;
				}
				else {
					System.out.println("afterturn effect parse error: " + s[i]); break;
				}
			}
			return new Skill(effects);
		}
	}
	
	public void loadSPCost() {
		String[] s = atSummon.split(" ");
		int n = Integer.valueOf(s[0]);
		if (n > 0 && s[1].equals("spCost")) {
			spCost = Integer.valueOf(s[2]);
		}
		else {
			spCost = 0;
		}
	}
	
	public void summonCheck() {
		String[] s = atSummon.split(" ");
		int n = Integer.valueOf(s[0]);
		if (n > 0 && s[1].equals("buildUnf")) {
			int turns = Integer.valueOf(s[2]);
			buildUnf(turns);
		}
	}
	
	public void buildUnf(int turns) {
		this.turns = turns;
		hold = true;
		File imgFile = new File("cards/unf.jpg");
		try {
			BufferedImage image2 = javax.imageio.ImageIO.read(imgFile);
			javax.swing.ImageIcon icon = new javax.swing.ImageIcon();
			icon.setImage(image2.getScaledInstance(frameHeight, frameWidth, Image.SCALE_FAST));
			Imglabel.setIcon(icon);
		} catch (IOException e) {e.printStackTrace();}
		actionButton.setText(cardName + " (" + turns + ")");
		labelMP.setText("");
		
	}
	
	public void hpDeduct (int amount) {
		if (shielded && amount > 1) hp -= 1;
		else hp -= amount;
		if (hp <= 0) {setEmpty();}
		else {updateInfo();}
	}
	
	public void hpDeduct2 (int amount) {
		if (shielded && amount > 1) hp -= 1;
		else hp -= amount;
		if (hp <= 0) {setEmpty(); if(!isToken()) Coop.shuffleInACard(identifier);}
		else {updateInfo();}
	}
	
	public void mpDeduct (int amount) {
		mp -= amount;
		if(mp <= 0) mp = 0;
		updateInfo();
		return;
	}
	
	public boolean mpCost (int amount) {
		if (amount > mp) {Coop.reject("Not enough mana"); return false;}
		else {mpDeduct(amount); return true;}
	}
	
	public void hpRecover (int amount) {
		hp += amount;
		if (hp > hpMax) hp = hpMax;
		updateInfo();
	}
	
	public void mpRecover (int amount) {
		mp += amount;
		if (mp > mpMax) mp = mpMax;
		updateInfo();
	}
	
	public String toString() {
		return identifier + " " + cardName + " (" + hp + ":" + hpMax + ") (" + mp + ":" + mpMax + ")";
	}
	
}

package Coop;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CoopBoss {
	
	private java.awt.Image image;
	
	public int mp; public int mpMax;
	public int hp; public int hpMax;
	
	public int auraLevel;
	public int nextAuraTurns;
	public int selfRegen;
	public int regenTurns;
	
	public boolean ultimateUsed;
	
	public Composite comp;
	public Button actionButton;
	public Label labelMP;
	public Label labelHP;
	public javax.swing.JLabel Imglabel;
	
	private int frameHeight;
	private int frameWidth;
	
	public CoopBoss() {
		mpMax = mp = 50;
		hpMax = hp = 50;
		auraLevel = 1;
		nextAuraTurns = 0;
		selfRegen = 0;
		regenTurns = 0;
		ultimateUsed = false;
		File imgFile = new File("cards/boss.jpg");
		try {
			image = javax.imageio.ImageIO.read(imgFile);
		} catch (IOException e) {e.printStackTrace();}

	}
	
	public Composite genImage(Composite shell) {
		comp = new Composite(shell, SWT.EMBEDDED);
		
		Composite imgComp = new Composite(comp, SWT.EMBEDDED);
		java.awt.Frame frame = SWT_AWT.new_Frame(imgComp);
		
		Composite labelComp = new Composite(comp, SWT.EMBEDDED);
		actionButton = new Button(labelComp, SWT.PUSH | SWT.CENTER);
		actionButton.setText("Boss");
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
		if (mpMax != 0) labelMP.setText("MP:" + mp);
		if (hpMax != 0) labelHP.setText("HP:" + hp);
		javax.swing.ImageIcon icon = new javax.swing.ImageIcon();
		icon.setImage(image.getScaledInstance(frameHeight, frameWidth, Image.SCALE_FAST));
		Imglabel.setIcon(icon);
	}
	
	public void Action() {
		ArrayList<String> s = new ArrayList<String>();
		if (mp >= 30 && hp >= 30) {
			// 提升气场，召唤进攻性怪物，进攻怪用boss蓝攻击
			s.add("aura");
			if (minionNotFull()) s.add("minion 1");
		}
		else if (mp >= 30 && hp < 30) {
			// 提升气场，召唤防御性怪物，防守怪不攻击，在boss血不到40时给boss回，boss血在10以下牺牲自己。
			s.add("aura");
			if (minionNotFull()) s.add("minion 2");
		}
		else if (mp < 30 && hp >= 30) {
			// 亲自对高仇恨进行普通攻击，优先建筑
			s.add("attack " + getPriorityTarget(false));
		}
		else if (mp < 30 && hp < 30 && !ultimateUsed && auraLevel > 1) {
			// 释放终结技
			s.add("ult");
		}
		else {
			// 亲自对高仇恨进行普通攻击，优先怪物
			s.add("attack " + getPriorityTarget(false));
		}
		s.add("regen " + selfRegen);
		if (!Coop.minions[0].isEmpty()) {
			s.add("skill 0");
		}
		if (!Coop.minions[1].isEmpty()) {
			s.add("skill 1");
		}
		
		for (int i = 0; i < s.size(); i++) {
			String movement = s.get(i);
			long time = i * 3000;
			boolean last = (i == s.size() - 1);
			new Thread() {
				@Override
				public void run() {
					try {
						sleep(time);
					} catch (InterruptedException e) {e.printStackTrace();}
					Coop.display.syncExec(new Runnable () {
						@Override
						public void run(){
							reflect(movement);
						}
					});
					if (last) {
						try {
							sleep(3000);
						} catch (InterruptedException e) {e.printStackTrace();}
						Coop.display.syncExec(new Runnable () {
							@Override
							public void run(){
								Coop.yourTurn();
							}
						});
					}
				}
			}.start();
		}
	}
	
	public void reflect (String movement) {
		System.out.println(movement);
		String[] s = movement.split(" ");
		switch(s[0]) {
		case "aura":
			raiseAura();
			mpDeduct(5);
			break;
		case "minion":
			if(Coop.minions[0].isEmpty()) {
				Coop.minions[0].setMinion(Integer.valueOf(s[1]));
				mpDeduct(20);
			}
			else if(Coop.minions[1].isEmpty()) {
				Coop.minions[1].setMinion(Integer.valueOf(s[1]));
				mpDeduct(20);
			}
			Coop.text.setText("Boss: 現れよ、我が忠誠な僕たち。");
			break;
		case "regen":
			selfRegen();
			break;
		case "attack":
			String location = s[1];
			int x = Integer.valueOf(s[2]), y = Integer.valueOf(s[3]);
			if (location.equals("Board")) {
				Coop.myBoard[x][y].hpDeduct(5);
			}
			if (location.equals("Friend")) {
				Coop.friendBoard[x][y].hpDeduct(5);
			}
			Coop.text.setText("Boss: 喰らえ！");
			break;
		case "ult":
			ultimate();
			break;
		case "skill":
			Coop.minions[Integer.valueOf(s[1])].Action();
			break;
		default:
		}
	}
	
	public void DefensiveAction() {
		if (auraLevel > 1 && selfRegen == 0 && hp <= 20) {
			auraLevel --; selfRegen = 5; regenTurns = 2;
			Coop.bossAura.setText("Aura: " + auraLevel);
			Coop.text.setText("Boss: Looks like I have to take some rest.");
			return;
		}
		else {
			if (Coop.text.getText().equals("Boss: You dare challenge me?!"))
			Coop.text.setText("Boss: お前はもう死んでいる。");	
			else
			Coop.text.setText("Boss: You dare challenge me?!");
		}
	}
	
	public boolean minionNotFull() {
		return Coop.minions[0].isEmpty() || Coop.minions[0].isEmpty();
	}
	
	public void raiseAura() {
		if (nextAuraTurns == 0) {
			nextAuraTurns = auraLevel;
			Coop.text.setText("Boss: Ahhhhhhhhhhhh! (*Screams)");
		}
		else {
			raiseAuraAftTurn();
		}
	}
	
	public void raiseAuraAftTurn() {
		nextAuraTurns--;
		if (nextAuraTurns == 0 && auraLevel != 3) {
			auraLevel++;
			Coop.bossAura.setText("Aura: " + auraLevel);
			Coop.text.setText("Boss: My power is furtherly growing!");
		}
		else if (nextAuraTurns == 0 && auraLevel == 3) {
			auraLevel++;
			Coop.bossAura.setText("Aura: " + auraLevel);
			Coop.text.setText("Boss: No one could stop me anymore!");
			Coop.lose();
		}
	}
	
	public String getPriorityTarget(boolean isMonster) {
		boolean b = Coop.getRandom();
		if(isMonster) {
			if ((Coop.spcost > Coop.fspcost) || (Coop.spcost == Coop.fspcost && b)) {
				int x = 0, y = 0, max = 0;
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < 4; j++) {
						if (Coop.myBoard[i][j].spCost > max) {
							max = Coop.myBoard[i][j].spCost; x = i; y = j;
						}
					}
				}
				if (x == 1 && !Coop.myBoard[0][y].isEmpty())
					return "Board 0 " + y;
				else
					return "Board " + x + " " + y;
			}
			else {
				int x = 0, y = 0, max = 0;
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < 4; j++) {
						if (Coop.friendBoard[i][j].spCost > max) {
							max = Coop.friendBoard[i][j].spCost; x = i; y = j;
						}
					}
				}
				if (x == 1 && !Coop.friendBoard[0][y].isEmpty())
					return "Friend 0 " + y;
				else
					return "Friend " + x + " " + y;
			}
		}
		else {
			if ((Coop.vpgen > Coop.fvpgen) || (Coop.vpgen == Coop.fvpgen && b)) {
				int x = 0, y = 0, max = 0;
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < 4; j++) {
						if (Coop.myBoard[i][j].vpGen > max) {
							max = Coop.myBoard[i][j].vpGen; x = i; y = j;
						}
					}
				}
				if (x == 1 && !Coop.myBoard[0][y].isEmpty())
					return "Board 0 " + y;
				else
					return "Board " + x + " " + y;
			}
			else {
				int x = 0, y = 0, max = 0;
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < 4; j++) {
						if (Coop.friendBoard[i][j].vpGen > max) {
							max = Coop.friendBoard[i][j].vpGen; x = i; y = j;
						}
					}
				}
				if (x == 1 && !Coop.friendBoard[0][y].isEmpty())
					return "Friend 0 " + y;
				else
					return "Friend " + x + " " + y;
			}
		}
	}
	
	public void ultimate() {
		int damage = auraLevel * 2;
		Coop.text.setText("Boss: Nico Nico Ni~");
		for(int i = 0; i < 4; i++) {
			if (!Coop.myBoard[0][i].isEmpty()) {
				Coop.myBoard[0][i].hpDeduct2(damage);
			}
			else if (!Coop.myBoard[1][i].isEmpty()) {
				Coop.myBoard[1][i].hpDeduct2(damage);
			}
			
			if (!Coop.friendBoard[0][i].isEmpty()) {
				Coop.friendBoard[0][i].hpDeduct(damage);
			}
			else if (!Coop.friendBoard[1][i].isEmpty()) {
				Coop.friendBoard[1][i].hpDeduct(damage);
			}
		}
		ultimateUsed = true;
	}
	
	public void selfRegen() {
		if (regenTurns > 0) {
			hpRecover(selfRegen); mpRecover(selfRegen); regenTurns--; if (regenTurns == 0) selfRegen = 0; 
		}
		else {
			hpRecover(3); mpRecover(3);
		}
	}
	
	public void mpDeduct (int amount) {
		mp -= amount;
		updateDisplay();
	}
	
	public void hpDeduct (int amount) {
		hp -= amount;
		if (hp <= 0) hp = 0;
		updateDisplay();
		if (hp > 0)
			DefensiveAction();
		else
			Coop.win();
	}
	
	public void hpRecover(int amount) {
		hp += amount;
		if (hp > hpMax) hp = hpMax;
		updateDisplay();
		Coop.text.setText("Boss: Now I am feeling better.");
	}
	
	public void mpRecover(int amount) {
		mp += amount;
		if (mp > mpMax) mp = mpMax;
		updateDisplay();
	}

}

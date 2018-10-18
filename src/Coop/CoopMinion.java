package Coop;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CoopMinion {
	private java.awt.Image image;
	
	public int id;
	private int hp; private int hpMax;
	
	public Composite comp;
	public Button actionButton;
	public Label labelMP;
	public Label labelHP;
	public javax.swing.JLabel Imglabel;
	
	private int frameHeight;
	private int frameWidth;
	
	public CoopMinion() {
		setEmpty();
	}
	
	public void setEmpty() {
		id = 0;
		hpMax = hp = 0;
		File imgFile = new File("cards/empty.jpg");
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
		actionButton.setText("Minion");
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
		if (hpMax != 0) labelHP.setText("HP:" + hp);
		javax.swing.ImageIcon icon = new javax.swing.ImageIcon();
		icon.setImage(image.getScaledInstance(frameHeight, frameWidth, Image.SCALE_FAST));
		Imglabel.setIcon(icon);
	}
	
	public void setMinion(int i) {
		switch (i) {
			case 1: {
				hpMax = hp = 10;
				File imgFile = new File("cards/minion1.jpg");
				try {
					image = javax.imageio.ImageIO.read(imgFile);
				} catch (IOException e) {e.printStackTrace();}
				updateDisplay();
				Coop.text.setText("Minion 1: 結局僕が一番強くて凄いんだよね！");
				break;
			}
			case 2: {
				hpMax = hp = 10;
				File imgFile = new File("cards/minion2.jpg");
				try {
					image = javax.imageio.ImageIO.read(imgFile);
				} catch (IOException e) {e.printStackTrace();}
				updateDisplay();
				Coop.text.setText("Minion 2: 聖なる光、私に力を！なんちゃって～");
				break;
			}
			default:
		}
	}
	
	public boolean isEmpty() {
		return hpMax == 0;
	}
	
	public void Action() {
		switch (id) {
		case 1:
			int damage = 1;
			boolean aoe = false;
			if (Coop.boss.mp > 40) {
				aoe = true; Coop.boss.mpDeduct(10);
			}
			if (Coop.boss.mp > 20) {
				int mp = Coop.boss.mp;
				mp = mp / 10;
				damage += mp;
				mp = mp * 5;
				Coop.boss.mpDeduct(mp);
			}
			if (aoe) {
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
				Coop.text.setText("Minion 1: 数が多いだけが。");
			}
			else {
				String target = Coop.boss.getPriorityTarget(true);
				String[] s = target.split(" ");
				String location = s[0];
				int x = Integer.valueOf(s[1]), y = Integer.valueOf(s[2]);
				if (location.equals("Board")) {
					Coop.myBoard[x][y].hpDeduct2(damage);
				}
				if (location.equals("Friend")) {
					Coop.friendBoard[x][y].hpDeduct(damage);
				}
				Coop.text.setText("Minion 1: bossさんに挑戦なんて10000LY早いんだよ！");
			}
			break;
		case 2:
			if (Coop.boss.hp <= 10) {
				Coop.boss.hpRecover(hp / 2);
				Coop.boss.selfRegen = hp;
				Coop.boss.regenTurns = 1;
				Coop.text.setText("Minion 2: This is my last hamon! JoJo!");
				setEmpty();
			}
			else if (Coop.boss.hp <= 40) {
				Coop.boss.selfRegen = 5;
				Coop.boss.regenTurns = 1;
				Coop.text.setText("Minion 2: Purification! Purification! Sacred Create Water!");
			}
			else {
				hpRecover(2);
				Coop.text.setText("Minion 2: Ahhh.....");
			}
			break;
		default:
		}
	}
	
	public void hpDeduct(int amount) {
		hp -= amount;
		if (hp <= 0) setEmpty();
		else updateDisplay();
	}
	
	public void hpRecover(int amount) {
		hp += amount;
		if (hp > hpMax) hp = hpMax;
		updateDisplay();
	}
}

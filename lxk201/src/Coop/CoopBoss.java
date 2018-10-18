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

public class CoopBoss {
	
	private java.awt.Image image;
	
	private int mp; private int mpMax;
	private int hp; private int hpMax;
	
	public int auraLevel;
	public int selfRegen;
	public int regenTurns;
	
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
		selfRegen = 0;
		regenTurns = 0;
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
		// TODO TBD
		if (mpMax - mp >= 10) ActionPattern1();
		else ActionPattern2();
		if (!Coop.minions[0].isEmpty()) Coop.minions[0].Action();
		if (!Coop.minions[1].isEmpty()) Coop.minions[1].Action();
	}
	
	public void DefensiveAction() {
		if (auraLevel > 1) {
			auraLevel --; selfRegen += 5; regenTurns = 2;
			Coop.bossAura.setText("Aura: " + auraLevel);
			Coop.text.setText("Boss: Looks like I have to take some rest.");
			return;
		}
		else {
			if (Coop.text.getText().equals("Boss: You dare challenge me?!"))
			Coop.text.setText("Boss: お念はもう棒んでいる。");	
			else
			Coop.text.setText("Boss: You dare challenge me?!");
		}
	}
	
	public void ActionPattern1() {
		Coop.text.setText("Boss: Greetings travellers.");
		mp = mp - 5;
		updateDisplay();
		
	}
	
	public void ActionPattern2() {
		Coop.text.setText("Boss: You irritates me!");
		if (Coop.minions[0].isEmpty()) Coop.minions[0].setMinion(1);
		else if (Coop.minions[1].isEmpty()) Coop.minions[1].setMinion(1);
	}
	
	public void ActionPattern3() {
		// TODO more action patterns
	}
	
	public void mpDeduct(int amount) {
		mp -= amount;
		updateDisplay();
	}
	
	public void hpDeduct(int amount) {
		hp -= amount;
		updateDisplay();
		DefensiveAction();
		Action();
	}

}

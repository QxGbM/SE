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
	
	private int mp; private int mpMax;
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
		mpMax = mp = 0;
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
		if (mpMax != 0) labelMP.setText("MP:" + mp);
		if (hpMax != 0) labelHP.setText("HP:" + hp);
		javax.swing.ImageIcon icon = new javax.swing.ImageIcon();
		icon.setImage(image.getScaledInstance(frameHeight, frameWidth, Image.SCALE_FAST));
		Imglabel.setIcon(icon);
	}
	
	public void setMinion(int i) {
		switch (i) {
			case 1: {
				mpMax = mp = 10;
				hpMax = hp = 10;
				File imgFile = new File("cards/minion1.jpg");
				try {
					image = javax.imageio.ImageIO.read(imgFile);
				} catch (IOException e) {e.printStackTrace();}
				updateDisplay();
				Coop.text.append(" Minion 1: I heed your call.");
				break;
			}
			default:
		}
	}
	
	public boolean isEmpty() {
		return (hpMax == 0) && (mpMax == 0);
	}
	
	public void Action() {
		// TODO TBD
		if (mpMax - mp < 5) ActionPattern1();
		else ActionPattern2();
	}
	
	public void ActionPattern1() {
		mp = mp - 5;
		updateDisplay();
	}
	
	public void ActionPattern2() {
		mp = mp + 1;
		updateDisplay();
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
		if (hp <= 0) setEmpty();
		else updateDisplay();
	}
}

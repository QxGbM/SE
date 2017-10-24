package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public final class Login implements ActionListener{
  
	private String un;//user name
	private String pw;//password
	private JFrame holder;
	private JPanel content = new JPanel(new GridBagLayout());
	private JTextField[] fields = {new JTextField("User Name:"),new JTextField("Password:"),new JTextField(),new JTextField()};
	private JButton login = new JButton("log in");

	//you don't need  to read the constructor
	protected Login(){
		un = null;
		pw = null;
	  
		login.addActionListener(this);
		fields[0].setEditable(false);
		fields[0].setBorder(javax.swing.BorderFactory.createEmptyBorder());
		fields[0].setBackground(new Color(0,100,255));
		fields[1].setEditable(false);
		fields[1].setBorder(javax.swing.BorderFactory.createEmptyBorder());
		fields[1].setBackground(new Color(0,100,255));
		holder = new JFrame("Login");
		holder.setBackground(new Color(0,100,255));
		content.setBackground(new Color(0,100,255));
    
		GridBagConstraints t1 = new GridBagConstraints();
		t1.fill=GridBagConstraints.HORIZONTAL;
		t1.gridx = 0;
		t1.gridy = 0;
		t1.gridheight = 2;
		content.add(fields[0],t1);
    
		GridBagConstraints t2 = new GridBagConstraints();
		t2.fill=GridBagConstraints.HORIZONTAL;
		t2.gridx = 0;
		t2.gridy = 2;
		t2.gridwidth = 5;
		content.add(fields[2],t2);
    
		GridBagConstraints t3 = new GridBagConstraints();
		t3.fill=GridBagConstraints.HORIZONTAL;
		t3.gridx = 0;
		t3.gridy = 3;
		t3.gridheight = 2;
		content.add(fields[1], t3);
    
		GridBagConstraints t4 = new GridBagConstraints();
		t4.fill=GridBagConstraints.HORIZONTAL;
		t4.gridx = 0;
		t4.gridy = 5;
		t4.gridwidth = 3;
		content.add(fields[3],t4);
    
		GridBagConstraints bc = new GridBagConstraints();
		bc.anchor=GridBagConstraints.LINE_END;
		bc.gridx = 2;
		bc.gridy = 6;
		bc.gridwidth = 1;
		content.add(login,bc);
    
		holder.getContentPane().add(content,"Center");
		holder.setSize(500,400);
		holder.setLocation(600,600);
		holder.setVisible(true);
	}
  
	//the only thing about this is that if password or user name field  is empty
	//then those field will not be read
	@Override
	public void actionPerformed(ActionEvent click){
		if(click.getSource() == login){      
			//  badInput.dispatchEvent(new WindowEvent(badInput, WindowEvent.WINDOW_CLOSING));
			if(fields[2].getText().length() == 0 || fields[3].getText().length() == 0){
				JFrame badInput = new JFrame("Unacceptable");;
				JTextArea bad = new JTextArea("Missing Username and/or password.\nRequest rejected.");
				badInput.getContentPane().add(bad,"Center");
				bad.setEditable(false);
				badInput.setSize(400,200);
				badInput.setLocation(400,400);
				badInput.setVisible(true);
				return;              
			}
			un = fields[2].getText();
			pw = fields[3].getText();
			fields[2].setText("");
			fields[3].setText("");
		}
	}
  
	public static void main(){
		Login login = new Login();
		while (Game.Loggedin == false) {
			while (!(login.un != null && login.pw != null)) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
			Game.sendLogin(login.un, login.pw);
			login.pw = null; login.un = null;
		}
		login.holder.dispose();
	}
}
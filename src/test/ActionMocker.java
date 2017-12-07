package test;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import main.NetClient;

public class ActionMocker {
	
	public final static int MatchNum = 1001;
	public final static int playerID = 101;

	public static void main(String[] args) {
		if (NetClient.ds == null) NetClient.startNetClient();
		JFrame frame = new JFrame("Action Mocker");
		JComboBox<String> combobox = new JComboBox<String>();
		combobox.addItem("endturn");
		combobox.addItem("summon");
		combobox.addItem("skill");
		combobox.addItem("surrender");
		combobox.addItem("custom");
		
		JTextArea textarea = new JTextArea();
		JButton send = new JButton("send");
		JLabel label = new JLabel();
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(5,5,5,5));
		
		combobox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(combobox.getSelectedItem().equals("endturn"))
					textarea.setText("");
				if(combobox.getSelectedItem().equals("summon"))
					textarea.setText("<MonsterID> <Location>");
				if(combobox.getSelectedItem().equals("skill"))
					textarea.setText("<Location> <Requires Target?> <(Optional) Target>");
				if(combobox.getSelectedItem().equals("surrender"))
					textarea.setText("");
				else
					textarea.setText("");
			}
		});
		
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String s;
				if (combobox.getSelectedItem().equals("custom"))
					s = textarea.getText();
				else
					s = "Action " + MatchNum + " " + playerID + " " + combobox.getSelectedItem() + " " + textarea.getText();
				textarea.setText("");
				label.setText(NetClient.send(s));
			}
		});
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 1;
		constraints.gridy = 1;
		panel.add(combobox, constraints);
		constraints.weighty = 3;
		constraints.gridy = 2;
		panel.add(textarea, constraints);
		constraints.weighty = 1;
		constraints.gridy = 3;
		panel.add(label, constraints);
		constraints.gridy = 4;
		panel.add(send, constraints);
		frame.add(panel);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}

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
import protocol.Message;

public class ActionMocker {
	
	public final static int MatchNum = 1001;
	public final static int CoopNum = 2001;
	public final static int playerID = 101;
	
	public static boolean mode = true;

	public static void main(String[] args) {
		if (NetClient.ds == null) NetClient.startNetClient();
		JFrame frame = new JFrame("Action Mocker");
		JComboBox<String> combobox = new JComboBox<String>();
		combobox.addItem("quickmatch enter");
		combobox.addItem("quickmatch accept");
		combobox.addItem("endturn");
		combobox.addItem("summon");
		combobox.addItem("skill");
		combobox.addItem("surrender");
		
		JTextArea textarea = new JTextArea();
		JButton send = new JButton("send");
		JButton toggle = new JButton("Toggle: Quickmatch");
		JLabel label = new JLabel();
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(5,5,5,5));
		
		combobox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (combobox.getSelectedItem() == null) return;
				if (mode) {
					if(combobox.getSelectedItem().equals("quickmatch enter"))
						textarea.setText("");
					else if(combobox.getSelectedItem().equals("quickmatch accept"))
						textarea.setText("");
					else if(combobox.getSelectedItem().equals("endturn"))
						textarea.setText("");
					else if(combobox.getSelectedItem().equals("summon"))
						textarea.setText("<MonsterID> <Location>");
					else if(combobox.getSelectedItem().equals("skill"))
						textarea.setText("<Location> <Requires Target?> <(Optional) Target>");
					else if(combobox.getSelectedItem().equals("surrender"))
						textarea.setText("");
				}
				else {
					if(combobox.getSelectedItem().equals("coop enter"))
						textarea.setText("");
					else if(combobox.getSelectedItem().equals("coop accept"))
						textarea.setText("");
					else if(combobox.getSelectedItem().equals("summon"))
						textarea.setText("<int:id> <int:slot>");
					else if(combobox.getSelectedItem().equals("skill"))
						textarea.setText("<int:slot> <bool:requiresTarget> (<int:target>)");
					else if(combobox.getSelectedItem().equals("endturn"))
						textarea.setText("");
					else if(combobox.getSelectedItem().equals("ball"))
						textarea.setText("");
					else if(combobox.getSelectedItem().equals("surrender"))
						textarea.setText("");
				}
				
			}
		});
		
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String S = (String) combobox.getSelectedItem(), s = "";
				if (mode) {
					switch (S) {
					case "quickmatch enter":
						s = new protocol.QuickMatch(playerID, true, true).toString();
						break;
					case "quickmatch accept":
						s = new Message(playerID, MatchNum, true).toString();
						break;
					default:
						s = "Action " + MatchNum + " " + playerID + " " + combobox.getSelectedItem() + " " + textarea.getText();
					}
				}
				else {
					switch (S) {
					case "coop enter":
						s = new protocol.QuickMatch(playerID, false, true).toString();
						break;
					case "coop accept":
						s = new Message(playerID, CoopNum, true).toString();
						break;
					default:
						s = "CoopAction " + CoopNum + " " + playerID + " " + combobox.getSelectedItem() + " " + textarea.getText();
					}
				}
				textarea.setText("");
				label.setText(NetClient.send(s));
			}
		});
		
		toggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (mode) {
					toggle.setText("Toggle: Coop");
					mode = false;
					combobox.removeAllItems();
					combobox.addItem("coop enter");
					combobox.addItem("coop accept");
					combobox.addItem("summon");
					combobox.addItem("skill");
					combobox.addItem("endturn");
					combobox.addItem("ball");
					combobox.addItem("surrender");
				}
				else {
					toggle.setText("Toggle: Quickmatch");
					mode = true;
					combobox.removeAllItems();
					combobox.addItem("quickmatch enter");
					combobox.addItem("quickmatch accept");
					combobox.addItem("endturn");
					combobox.addItem("summon");
					combobox.addItem("skill");
					combobox.addItem("surrender");
				}
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
		constraints.gridy = 5;
		panel.add(toggle, constraints);
		frame.add(panel);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}

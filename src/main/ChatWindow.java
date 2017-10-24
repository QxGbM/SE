package main;

import javax.swing.JTextArea;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class ChatWindow{
	
	private int recipient;
	private String nickname;
	
	private final Display display = Game.display;
	private final Shell shell = new Shell(display);
	
	private JTextArea textDisplay = null;
	private java.awt.Frame TextFrame = null;
	
	private Text input = null;
	private Button send = null;

	public ChatWindow(int Recipient, String Nickname) {
	    this.recipient = Recipient;
	    this.nickname = Nickname;
	    setDisplay();
	}
	
	private void setDisplay() {
		
		shell.setText("Chatting with: " + nickname);
		
		Label separator1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		Composite TextComp = new Composite(shell, SWT.EMBEDDED);
		TextFrame = SWT_AWT.new_Frame(TextComp);
		textDisplay = new JTextArea();
		textDisplay.setEditable(false);
		textDisplay.setText(Game.findFriend(recipient).Message);
		TextFrame.add(textDisplay);
		
		Label separator2 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		Composite comp = new Composite(shell, SWT.EMBEDDED);
		input = new Text(comp, SWT.NONE);
		send = new Button(comp, SWT.PUSH);
		send.setText("Send");
		
		Listener exitListener = e -> {
			MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION);
			dialog.setText("Question");
			dialog.setMessage("Exit?");
			if (e.type == SWT.Close) e.doit = false;
			if (dialog.open() != SWT.OK) return;
			shell.dispose();
		};
		
		Listener sendListener = e -> {
			if (!input.getText().equals("")) {
				Game.sendMessage(input.getText(), recipient);
				textDisplay.setText(Game.findFriend(recipient).Message);
				input.setText("");
			}
		};
		
		send.addListener(SWT.Selection, sendListener);
		shell.addListener(SWT.Close, exitListener);
		
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = layout.marginHeight = 10;
		layout.horizontalSpacing = layout.verticalSpacing = 20;
		shell.setLayout(layout);
		
		GridData data;
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		separator1.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.heightHint = 300;
		TextComp.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		separator2.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.heightHint = 50;
		comp.setLayoutData(data);
		
		layout = new GridLayout(3, true);
		layout.marginWidth = layout.marginHeight = 0;
		layout.horizontalSpacing = layout.verticalSpacing = 20;
		comp.setLayout(layout);
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		input.setLayoutData(data);
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		send.setLayoutData(data);
		
		shell.open();
	}
	
	public void appendMessage(String message) {
		textDisplay.append(message);
	}
	
}

package main;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;  

public class MainWindow {
	
	public static Frame mainwindow = new Frame("Main Window");
	
	public static int selectedFriendID = -1;
	public static String myNickname = "";
	
	public static int cardN = 15;
	public static int selectedCard = -1;
	
	public static int[] deck = {101,101,102,102,103,103,104,105,201,201,202,202,203,204,205};
	public static boolean deckIsReady = true;
	
	public boolean loadMainPanel() {
		
		JPanel mainPanel = new JPanel(new GridBagLayout());
		JLabel nickname = new JLabel(myNickname);
		JButton quickmatch = new JButton("Find Quickmatch");
		JButton viewFriends =new JButton("View My Friends");
		JButton viewCards = new JButton("View My Cards");
		JButton logout = new JButton("Logout");
		
		quickmatch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO Implementation
				JOptionPane.showMessageDialog(mainwindow, "Coming Soon");
			}
		});
		
		viewFriends.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadFriendPanel();
			}
		});
		
		viewCards.addActionListener(new ActionListener() {
  			@Override
  			public void actionPerformed(ActionEvent e) {
  				loadCardPanel();
  			}
  		});
		
		logout.addActionListener(new ActionListener() {
  			@Override
  			public void actionPerformed(ActionEvent e) {
  				Game.Loggedin = false;
  				mainwindow.dispose();
  				Login.main();
  			}
  		});
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		mainPanel.add(nickname, constraints);
		
		constraints.gridy = 1;
		mainPanel.add(quickmatch, constraints);
		
		constraints.gridy = 2;
		mainPanel.add(viewFriends, constraints);
		
		constraints.gridy = 3;
		mainPanel.add(viewCards, constraints);
		
		constraints.gridy = 4;
		mainPanel.add(logout, constraints);
		
		mainwindow.removeAll();
		mainwindow.add(mainPanel);
		mainwindow.pack();
		return mainwindow.isShowing();
	}
	
	public final static ArrayList<Friend> friends = new ArrayList<Friend>();
	
	public final static class Friend {
		
		public int ID;
		public String Nickname;
		public boolean Online;
		public String Message;
		
		public boolean BattleRequest;
		public int matchNum;
		
		public JLabel status;
		public JTextField display;
		
		public ChatWindow window = null;
		
		public Friend(int id, String nickname, boolean online, String message) {
			ID = id; Nickname = nickname; Online = online; Message = message;
			
			display = new JTextField(nickname);
			display.setEditable(false);
	    	display.setBackground(Color.white);
	    	
	    	display.addMouseListener(new MouseListener() {
	    		
	    		@Override
	    		public void mousePressed(MouseEvent e){}
	     	  
	    		@Override
	    		public void  mouseReleased(MouseEvent e){}
	    		
	    		@Override
	    		public void mouseEntered(MouseEvent e){
	    			if (MainWindow.selectedFriendID != ID)
	    				display.setBackground(Color.cyan);
	    		}
	     	  
	    		@Override
	    		public void mouseExited(MouseEvent e){
	    			if (MainWindow.selectedFriendID != ID)
	    				display.setBackground(Color.white);
	    		}

	    		@Override
	    		public void mouseClicked(MouseEvent e) {
	    			MainWindow.selectedFriendID = ID;
	    			display.setBackground(Color.red);
	    		}
	     	
	    	});
	    	
	    	String s = (online? "Online":"Offline") + (message.equals("")? "":", Message");
	    	status = new JLabel(s);
		}
		
		public void startChat() {
			Game.display.syncExec(new Runnable() {
				@Override
				public void run(){
					window = new ChatWindow(ID, Nickname);
				}
			});
		}
		
		public void appendMessage(String message, Date date) {
			String s = date.toString() + " " + Nickname + " Said:\n" + message + "\n";
			Message += s;
			if (window != null) window.appendMessage(s);
		}
		
		public void appendMyMessage(String message, Date date) {
			String s = date.toString() + " " + Game.myNickname + " Said:\n" + message + "\n";
			Message += s;
			if (window != null) window.appendMessage(s);
		}
		
		public void requestBattle() {
			if (!deckIsReady) {
				JOptionPane.showMessageDialog(mainwindow, "Deck is not complete");
				return;
			}
			else if (!Online) {
				JOptionPane.showMessageDialog(mainwindow, "Friend is Offline");
				return;
			}
			else Game.sendBattleRequest(ID);
		}
	}
	
	public void loadFriendPanel() {
		
		JPanel viewMyFriends = new JPanel(new GridBagLayout());
		JScrollPane scrollpane = null;
		JPanel friendList = new JPanel(new GridBagLayout());
		JButton startChat = new JButton("Start Chat");
		JButton requestBattle = new JButton("Request Battle");
		JButton back = new JButton("Back");
		
		for (int i = 0; i < friends.size(); i++) {
			Friend f = friends.get(i);
			
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.BOTH;
			constraints.weightx = 1;
			constraints.weighty = 1;
			constraints.gridx = 0;
			constraints.gridy = i;
			friendList.add(f.status, constraints);
			constraints.gridx = 1;
			friendList.add(f.display, constraints);
		}
		
		friendList.setPreferredSize(new Dimension(200, 300));
		scrollpane = new JScrollPane(friendList);
		
		startChat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedFriendID != -1) {
					Game.findFriend(selectedFriendID).startChat();
					selectedFriendID = -1;
				}
				else
					JOptionPane.showMessageDialog(viewMyFriends, "No Friend Selected");
			}
		});
		
		requestBattle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedFriendID != -1) {
					Game.findFriend(selectedFriendID).requestBattle();
					selectedFriendID = -1;
				}
				else
					JOptionPane.showMessageDialog(viewMyFriends, "No Friend Selected");
			}
		});
		
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadMainPanel();
			}
		});
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridheight = 3;
		viewMyFriends.add(scrollpane, constraints);
		constraints.gridx = 1;
		constraints.gridheight = 1;
		constraints.gridy = 0;
		viewMyFriends.add(startChat, constraints);
		constraints.gridy = 1;
		viewMyFriends.add(requestBattle, constraints);
		constraints.gridy = 2;
		viewMyFriends.add(back, constraints);
		
		for (int i = 0; i < friends.size(); i++) {
			Friend f = friends.get(i);
			if (f.BattleRequest) {
				int confirm = JOptionPane.showConfirmDialog(mainwindow, "Battle With " + f.Nickname + "?");
				if (confirm == 0) {
					Game.sendBattleAccept(f.matchNum);
				}
			}
		}
		
		mainwindow.removeAll();
		mainwindow.add(viewMyFriends);
		mainwindow.pack();
	}
	
	private static DeckCard[] dc = new DeckCard[10];
	
	final static class DeckCard {
		public int id;
		public int n;
		public JLabel status;
		public JLabel image;
		public JButton select;
		
		public DeckCard(Card in, int k) {
			id = Integer.valueOf(in.getID());
			n = 0;
			for (int i = 0; i < 15; i++) {
				if (deck[i] == id) n++;
			}
			String s = n + "/2";
			status = new JLabel(s);
			
			image = new JLabel();
			ImageIcon icon = new ImageIcon();
			icon.setImage(in.getImage().getScaledInstance(100, 100, Image.SCALE_FAST));
			image.setIcon(icon);
			
			image.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					JOptionPane.showMessageDialog(mainwindow, in.toString(), "CardInfo", JOptionPane.DEFAULT_OPTION);
				}
			});
			
			select = new JButton("Select");
			select.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedCard = k;
					JOptionPane.showMessageDialog(mainwindow, "Card Selected");
				}
			});
		}
	}
	
	public void loadCardPanel() {
		
		JPanel viewMyCards = new JPanel(new GridBagLayout());
		JScrollPane scrollpane2 = null;
		JPanel cardList = new JPanel(new GridBagLayout());
		JButton addSelected = new JButton("Add to deck");
		JButton removeSelected = new JButton("Remove from deck");
		JLabel cardNum = new JLabel("15/15");

		JButton back2 = new JButton("Back");
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		
		for (int i = 0; i < 10; i++) {
			constraints.gridwidth = 2;
			cardList.add(dc[i].image, constraints);
			constraints.gridwidth = 1;
			constraints.gridy ++;
			cardList.add(dc[i].status, constraints);
			constraints.gridx ++;
			cardList.add(dc[i].select, constraints);
			if(constraints.gridx == 7) {
				constraints.gridx = 0; constraints.gridy ++;
			}
			else {
				constraints.gridx ++; constraints.gridy --;
			}
		}
		
		cardList.setPreferredSize(new Dimension(500, 400));
		scrollpane2 = new JScrollPane(cardList);
		
		addSelected.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cardN == 15) {
					JOptionPane.showMessageDialog(viewMyCards, "Deck is full");
				}
				else if (dc[selectedCard].n == 2) {
					JOptionPane.showMessageDialog(viewMyCards, "You can't add more");
				}
				else {
					int i;
					for (i = 0; i < 15 && deck[i]!= -1; i++) {}
					deck[i] = dc[selectedCard].id;
					JOptionPane.showMessageDialog(viewMyCards, "Card Added");
					cardN ++;
					cardNum.setText(Integer.toString(cardN) + "/15");
					dc[selectedCard].n++;
					dc[selectedCard].status.setText(dc[selectedCard].n + "/2");
				}
			}
		});
		
		removeSelected.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cardN == 0) {
					JOptionPane.showMessageDialog(viewMyCards, "Deck is Empty");
				}
				else {
					int i;
					for (i = 0; i < 15; i++) {
						if (deck[i] == dc[selectedCard].id) {
							deck[i] = -1;
							JOptionPane.showMessageDialog(viewMyCards, "Card Removed");
							cardN --;
							cardNum.setText(Integer.toString(cardN) + "/15");
							dc[selectedCard].n--;
							dc[selectedCard].status.setText(dc[selectedCard].n + "/2");
							break;
						}
					}
					if (i == 15) {
						JOptionPane.showMessageDialog(viewMyCards, "No Instance Present");
					}
				}
			}
		});
		
		back2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deckIsReady = (cardN == 15);
				if (!deckIsReady)
					JOptionPane.showMessageDialog(viewMyCards, "Your Deck isn't complete");
				loadMainPanel();
			}
		});
		
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridheight = 3;
		viewMyCards.add(scrollpane2, constraints);
		constraints.gridx = 1;
		constraints.gridheight = 1;
		constraints.gridy = 0;
		viewMyCards.add(addSelected, constraints);
		constraints.gridy = 1;
		viewMyCards.add(removeSelected, constraints);
		constraints.gridy = 2;
		viewMyCards.add(back2, constraints);
		
		mainwindow.removeAll();
		mainwindow.add(viewMyCards);
		mainwindow.pack();
	}
  	
	public MainWindow(String myNickname){
	  
  		mainwindow.addWindowListener( new WindowAdapter() {
  			@Override 
  			public void windowClosing(WindowEvent e){
  				Game.Loggedin = false;
  				mainwindow.setVisible(false);
  				mainwindow.removeAll();
  				mainwindow.dispose();
  			}
  		});
  		
  		ArrayList<Card> list = Card.cardLoader();
		for (int i = 0; i < 10; i++) {
			dc[i] = new DeckCard(list.get(i), i);
		}
  		
  		MainWindow.myNickname = myNickname;
  		loadMainPanel();
  		mainwindow.setVisible(true);
  		
  	}
}
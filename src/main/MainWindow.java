package main;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.awt.Image;

import javax.swing.*;
import javax.swing.border.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import protocol.QuickMatch;
import server.DatabaseAccessor;
import server.SecurityTools;
/* 今天要干什么：
 * 扫描矩阵搞定。
 * 但是进化卡片出现问题
 * 需要检查 Card.class 确定图片被正确读取
 * exception 在这个 swapimage里面被触发， 在icon.setimage 里面
 * */
public class MainWindow {
	
	public static Frame mainwindow = new Frame("Main Window");
	
	public static String myNickname = "";
	
	public static int cardN = 15;
	public static int selectedCard = -1;
	
	public static int[] deck = {101,101,102,102,103,103,104,105,201,201,202,202,203,204,205};
	public static boolean deckIsReady = true;
	
	public static boolean matched = false;
	
	public static final int NumCards = 13;
	
	
	private static final long nn = 60293329013L;
	private static final long ee = 11;
	private static final long dd = 1370291771L;
	  
	public void loadMainPanel() {
		
		JPanel mainPanel = new JPanel (new GridBagLayout());
		JLabel nickname = new JLabel (myNickname);
		JButton quickmatch = new JButton ("Find Quickmatch");
		JButton viewFriends =new JButton ("View My Friends");
		JButton viewCards = new JButton ("View My Cards");
		JButton versusAI = new JButton ("Versus against AI");
		JButton exit = new JButton ("Exit");
		
		quickmatch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadQuickMatchPanel();
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
		
		versusAI.addActionListener(new ActionListener() {
  			@Override
  			public void actionPerformed(ActionEvent e) {
  				if (Game.inMatch) {
					JOptionPane.showMessageDialog(mainwindow, "You are in a Game!");
					return;
				}
				if (!deckIsReady) {
					JOptionPane.showMessageDialog(mainwindow, "Finish building your deck first!");
					return;
				}
				Game.inMatch = true;
  				Game.startVersusAI();
  			}
  		});
		
		exit.addActionListener(new ActionListener() {
  			@Override
  			public void actionPerformed(ActionEvent e) {
  				System.exit(0);
  			}
  		});
		
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
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
		mainPanel.add(versusAI, constraints);
		
		constraints.gridy = 5;
		mainPanel.add(exit, constraints);
		
		mainwindow.removeAll();
		mainwindow.add(mainPanel);
		mainwindow.pack();
		
		this.exitChecker(mainwindow);
		
	}
	
	private void exitChecker(Frame monitor) {
		new Thread() {
			@Override
			public void run() {
				try {
					while(monitor.isVisible());
					
					Socket terminate = new Socket(InetAddress.getByName("159.65.180.211"),1777);
					DataOutputStream end = new DataOutputStream(terminate.getOutputStream());
					end.writeUTF(SecurityTools.encrypt("kill,"+new Integer(Game.myID).toString(), ee, nn)+"\n");
					end.flush();
					terminate.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public static class MatchingMonitor {
		public boolean matching = false;
		public String mode = "";
		
		public MatchingMonitor() {}
		public void startQuickmatch() {
			if (matching) {
				if (mode.equals("quickmatch")) return;
				if (mode.equals("coop"))
					NetClient.sendQuickMatch(new QuickMatch(Game.myID, false, false));
			}
			matching = false; mode = "";
			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(500);
						if (!matching) {
							NetClient.sendQuickMatch(new QuickMatch(Game.myID, true, true));
							matching = true; mode = "quickmatch";
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
		
		public void startCoop() {
			if (matching) {
				if (mode.equals("quickmatch"))
					NetClient.sendQuickMatch(new QuickMatch(Game.myID, true, false));
				if (mode.equals("coop")) return;
			}
			matching = false; mode = "";
			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(500);
						if (!matching) {
							NetClient.sendQuickMatch(new QuickMatch(Game.myID, false, true));
							matching = true; mode = "coop";
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
		
		public void dropQueue() {
			if (mode.equals("quickmatch"))
				NetClient.sendQuickMatch(new QuickMatch(Game.myID, true, false));
			if (mode.equals("coop"))
				NetClient.sendQuickMatch(new QuickMatch(Game.myID, true, false));
			reset();
		}
		
		public void reset() {
			matching = false; mode = "";
		}
	}
	
	public static final MatchingMonitor monitor = new MatchingMonitor();
	
	public void loadQuickMatchPanel() {
		
		matched = false;
		
		if (Game.inMatch) {
			JOptionPane.showMessageDialog(mainwindow, "Cannot enter the quick match queue while in a match!");
			return;
		}
		
		if (!deckIsReady) {
			JOptionPane.showMessageDialog(mainwindow, "Your deck isn\'t ready");
			return;
		}
		
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel time = new JLabel("Select the mode you wish to play.");
		JButton quickmatch = new JButton("Quickmatch");
		JButton coop = new JButton("Coop Match");
		JButton back = new JButton("Back");
		
		quickmatch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				monitor.startQuickmatch();
				new Thread() {
					@Override
					public void run() {
						int seconds = 0, minutes = 0;
						while (!matched) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {e.printStackTrace();}
							seconds++;
							if (seconds == 60) {seconds = 0; minutes++;}
							time.setText(monitor.mode + " " + minutes + ":" + seconds);
							if (!monitor.mode.equals("quickmatch")) break;
						}
					}
				}.start();
			}
		});
		
		coop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				monitor.startCoop();
				new Thread() {
					@Override
					public void run() {
						int seconds = 0, minutes = 0;
						while (!matched) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {e.printStackTrace();}
							seconds++;
							if (seconds == 60) {seconds = 0; minutes++;}
							time.setText(monitor.mode + " " + minutes + ":" + seconds);
							if (!monitor.mode.equals("coop")) break;
						}
					}
				}.start();
			}
		});
		
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				monitor.dropQueue();
				loadMainPanel();
			}
		});
		
		new Thread() {
			@Override
			public void run() {
				while (!matched) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {e.printStackTrace();}
				}
				monitor.reset();
				loadMainPanel();
			}
		}.start();
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		panel.add(time, constraints);
		
		constraints.gridy = 1;
		panel.add(quickmatch, constraints);
		constraints.gridy = 2;
		panel.add(coop, constraints);
		
		constraints.gridy = 3;
		panel.add(back, constraints);
		
		mainwindow.removeAll();
		mainwindow.add(panel);
		mainwindow.pack();
		
	}
	
	public final static ArrayList<Friend> friends = new ArrayList<Friend>();
	
	public final static class Friend {
		
		public int ID;
		public String Nickname;
		public boolean Online;
		public String Message;
		
		public ChatWindow window;
		
		public Friend(int id, String nickname, boolean online, String message) {
			ID = id; Nickname = nickname; Online = online; Message = message;
		}
		
		private boolean checkOnline(String id) {
			try {
				Socket checkOnline = new Socket(InetAddress.getByName("159.65.180.211"),1777);	
				DataOutputStream x = new DataOutputStream(checkOnline.getOutputStream());
				x.writeUTF(SecurityTools.encrypt("find,"+id, ee, nn)+"\n");
				x.flush();
				
				InputStream fromKhala = checkOnline.getInputStream();
				while(fromKhala.available() == 0);
				
				DataInputStream theLight = new DataInputStream(fromKhala);
				String oracle = theLight.readUTF().split("\n")[0];
				oracle = SecurityTools.decrypt(oracle, dd, nn);
				
				checkOnline.close();
				return oracle.equals("1");
			}
			catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		public void startChat() {
			Game.display.syncExec(new Runnable() {
				@Override
				public void run(){
					if (window == null || window.isDisposed())
						window = new ChatWindow(ID, Nickname);
					else
						window.forceActive();
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
			}
			else if (!this.checkOnline(new Integer(ID).toString())) {
				JOptionPane.showMessageDialog(mainwindow, "Friend is Offline");
			}
			else NetClient.sendBattleRequest(ID, 1);
			//TODO request coop
		}
	}
	
	public static boolean containsFriend(ArrayList<Friend> list, int id) {
		for(int i =0;i<list.size();i++) {
			if(list.get(i).ID == id)
				return true;
		}
		return false;
	}
	

	
	
	public void loadFriendPanel() {
		
		JPanel viewMyFriends = new JPanel(new GridBagLayout());
		JScrollPane scrollpane = null;
		DefaultListModel<String> list = new DefaultListModel<String>();
		JList<String> friendList = new JList<String>(list);
		JButton startChat = new JButton("Start Chat");
		JButton requestBattle = new JButton("Request Battle");
		JButton back = new JButton("Back");
		JButton add = new JButton("add friend");
		JButton remove = new JButton("remove friend");
		
		String[] friendsInfo = Game.findFriends(new Integer(Game.myID).toString(),false).split(" ");
		
		for(int i = 0;i<friendsInfo.length;i++) {
			
			String[] theFriend = Game.findUser(friendsInfo[i],false).split(" ");
			
			if(theFriend[0].equals("error"))
				continue;
			if(!containsFriend(friends,Integer.parseInt(friendsInfo[i])))
				friends.add(new Friend(Integer.parseInt(friendsInfo[i]),
					theFriend[1],
					false,
					"no history"));
		}
		
		for (int i = 0; i < friends.size(); i++) {
			Friend f = friends.get(i);
			list.addElement(f.Nickname);
		}
		
		friendList.setPreferredSize(new Dimension(200, 300));
		scrollpane = new JScrollPane(friendList);
		
		startChat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!friendList.isSelectionEmpty()) {
					friends.get(friendList.getSelectedIndex()).startChat();
				}
				else {
					JOptionPane.showMessageDialog(viewMyFriends, "No Friend Selected.");
				}
			}
		});
		
		requestBattle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (friendList.isSelectionEmpty()) {
					JOptionPane.showMessageDialog(viewMyFriends, "No Friend Selected.");
				}
				else if (Game.inMatch) {
					JOptionPane.showMessageDialog(viewMyFriends, "Cannot request battle while in match.");
				}
				else {
					friends.get(friendList.getSelectedIndex()).requestBattle();
				}
			}
		});
		
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				loadMainPanel();
			}
		});
		
		
		//new
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					@Override
					public void run() {
						try {
						Game.addFriend(JOptionPane.showInputDialog(viewMyFriends,"please enter id of a person"), false);
						}
						catch(NumberFormatException e) {return;}
					}
				}.start();
			}
		});
		
		remove.addActionListener(new ActionListener() {
			@Override 
			public void actionPerformed(ActionEvent e) {
			new Thread() {
				@Override
				public void run() {
					String id = JOptionPane.showInputDialog(viewMyFriends,"please enter id of a person");
					Game.removeFriend(id, false);
					for(int i =0;i < friends.size();i++) {
						try {
						if(friends.get(i).ID == Integer.parseInt(id))
							friends.remove(i);
						}
						catch(NumberFormatException e) {return;}
					}
				}
			}.start();
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
		viewMyFriends.add(add, constraints);
		constraints.gridy = 3;
		viewMyFriends.add(remove,constraints);
		constraints.gridy = 4;
		viewMyFriends.add(back,constraints);
		constraints.gridx = 0;
		constraints.gridy = 3;
		JTextField id = new JTextField("Your ID: "+Game.myID);
		id.setEditable(false);
		viewMyFriends.add( id ,constraints);
		mainwindow.removeAll();
		mainwindow.add(viewMyFriends);
		mainwindow.pack();
	}
	
	private static DeckCard[] dc = new DeckCard[NumCards];
	
	final static class DeckCard {
		public int id;
		public int n;
		public JLabel status;
		public Image image;
		public JLabel imageLabel;
		public JButton select;
		public boolean specialPracticed;
		public int specialID;
		public Image specialImage;
		public MouseAdapter infoDisplay;
		public MouseAdapter specialInfoDisplay;
		
		public DeckCard (Card in, int k) {
			id = Integer.valueOf(in.getID());
			n = 0;
			for (int i = 0; i < 15; i++) {
				if (deck[i] == id) n++;
			}
			String s = n + "/2";
			status = new JLabel(s);
			
			image = in.getImage();
			
			imageLabel = new JLabel();
			ImageIcon icon = new ImageIcon();
			icon.setImage(image.getScaledInstance(100, 100, Image.SCALE_FAST));
			imageLabel.setIcon(icon);
			
			infoDisplay = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					JOptionPane.showMessageDialog(mainwindow, in.toString(), "CardInfo", JOptionPane.DEFAULT_OPTION);
				}
			};
			
			imageLabel.addMouseListener(infoDisplay);
			
			select = new JButton("Select");
			select.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedCard = k;
					
					JOptionPane.showMessageDialog(mainwindow, "Card Selected");
				}
			});
						
		}
		
		public void checkSpecialPractice() {
			DatabaseAccessor x;
			if (id < 300) {
				x = new DatabaseAccessor();
				String c_id = Integer.toString(id);
				specialPracticed = x.checkSpecialPractice(Integer.toString(Game.myID), c_id);
				
			}
			else {
				specialPracticed = false;
			}
			if (specialPracticed) {
				specialID = id + 1000;
				DeckCard c = searchCard(specialID);
				specialImage = c.image;
				specialInfoDisplay = c.infoDisplay;
			}
		}
		
		//asked both swap image to increase id to tell if it is evolved
		public void swapImage() {
			Image img = image;
			image = specialImage;
			specialImage = img;
			imageLabel.removeAll();
			ImageIcon icon = new ImageIcon();
			
			icon.setImage(image.getScaledInstance(100, 100, Image.SCALE_FAST));
			imageLabel.setIcon(icon);
			imageLabel.removeMouseListener(infoDisplay);
			MouseAdapter info = infoDisplay;
			infoDisplay = specialInfoDisplay;
			specialInfoDisplay = info;
			imageLabel.addMouseListener(infoDisplay);
			DatabaseAccessor x = new DatabaseAccessor();
			x.addCard(new Integer(Game.myID).toString(), new Integer(id+1000).toString());
			id += 1000;
		}
		
		public boolean swapImage(int id, boolean specialPractice) {
			File im;
			int change = 0;
			if(id == 101 && specialPractice) {
				im = new File("cards/pylon.jpg");
				change = 1000;
			}
			else if(id == 204 && specialPractice) {
				im = new File("cards/Archon.jpg");
				change = 1000;
			}
			else if(id == 1101 && !specialPractice) {
				im = new File("cards/farm.jpg");
				change = -1000;
			}
			else if(id == 1204 && !specialPractice) {
				im = new File("cards/wizard.jpg");
				change = -1000;
			}
			else 
				return false;
			
			
			Image img;
			try {
				img= javax.imageio.ImageIO.read(im);
			}
			catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			image = img;
			specialImage = img;
			imageLabel.removeAll();
			ImageIcon icon = new ImageIcon();
			
			icon.setImage(image.getScaledInstance(100, 100, Image.SCALE_FAST));
			imageLabel.setIcon(icon);
			imageLabel.removeMouseListener(infoDisplay);
			MouseAdapter info = infoDisplay;
			infoDisplay = specialInfoDisplay;
			specialInfoDisplay = info;
			imageLabel.addMouseListener(infoDisplay);
			DatabaseAccessor x = new DatabaseAccessor();
			x.addCard(new Integer(Game.myID).toString(), new Integer(id+1000).toString());
			this.id+= change;
			return true;
		}
	}
	
	public static DeckCard searchCard(int id) {
		for(int i = 0; i < NumCards; i++) {
			if(dc[i].id == id) return dc[i];
		}
		return null;
	}
	
	public void loadCardPanel() {
		
		if (Game.inMatch) {
			JOptionPane.showMessageDialog(mainwindow, "Cannot edit deck while in a match!");
			return;
		}
		JPanel viewMyCards = new JPanel(new GridBagLayout());
		
		//new
		DatabaseAccessor x = new DatabaseAccessor();
		
		String[] user = x.findUser( 
				(
						new Integer(Game.myID).toString()
				)
			).split(" ");
		
		if(user[0].equals("error")) {
			JOptionPane.showMessageDialog(viewMyCards, "unknown error");
			loadMainPanel();
			return;
		}
		JTextArea gold = new JTextArea();
		myNickname = user[1];
		gold.setText("gold: "+user[4]+"\ndust: "+ user[5]);
		gold.setEditable(false);
		
		//end
		
		
		JScrollPane scrollpane2 = null;
		JPanel cardList = new JPanel(new GridBagLayout());
		JButton addSelected = new JButton("Add to deck");
		JButton removeSelected = new JButton("Remove from deck");
		JButton specialPractice = new JButton("Special Practice");
		JButton devolve = new JButton("Devolve");
		JLabel cardNum = new JLabel("15/15");

		JButton back = new JButton("Back");
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		
		for (int i = 0; i < NumCards; i++) {
			if(dc[i].id > 300) continue;
			constraints.gridwidth = 2;
			cardList.add(dc[i].imageLabel, constraints);
			constraints.gridwidth = 1;
			constraints.gridy ++;
			cardList.add(dc[i].status, constraints);
			constraints.gridx ++;
			//change
			cardList.add(dc[i].select, constraints);
			if(dc[i].specialPracticed) {				
				dc[i].swapImage(dc[i].id,true);	
				
			}
			cardList.revalidate();
			//end of change
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
				if (selectedCard == -1) {
					JOptionPane.showMessageDialog(viewMyCards, "Please select a card");
				}
				else if (cardN == 15) {
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
				if (selectedCard == -1) {
					JOptionPane.showMessageDialog(viewMyCards, "Please select a card");
				}
				else if (cardN == 0) {
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
		
		specialPractice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (selectedCard == -1) {
					JOptionPane.showMessageDialog(viewMyCards, "Please select a card");
				}
				else if((dc[selectedCard].id != 101 && dc[selectedCard].id != 204) /*|| !dc[selectedCard].specialPracticed*/) {
					JOptionPane.showMessageDialog(viewMyCards, "Card is locked");
				}
				else {
					String[] cardInfo = Game.getCardInfo(new Float(Game.myID).toString(), new Integer(dc[selectedCard].id).toString(), false);
					
					if( Float.parseFloat(user[4]) < 25 || Float.parseFloat(cardInfo[2]) < 10) {
						JOptionPane.showMessageDialog(viewMyCards, "Not Enough Gold or card is not level 10 yet. Curruent level:"+cardInfo[2]);
						return;
					}
					
					/*if(cardInfo == null) {
						JOptionPane.showMessageDialog(viewMyCards, "FATAL ERROR");
						return;
					}*/
					x.updateCurrency(new Integer(Game.myID).toString(), "-25", "1");
					x.addCard(new Integer(Game.myID).toString(),
							new Integer(dc[selectedCard].id + 1000).toString());
					String[] user = x.findUser( 
							(
									new Integer(Game.myID).toString()
							)
						).split(" ");
					gold.setText("gold: "+user[4]+"\ndust: "+user[5]);
					
					x.specialPractice(new Integer(Game.myID).toString(),new Integer(dc[selectedCard].id).toString());
					dc[selectedCard].specialPracticed = dc[selectedCard].swapImage(dc[selectedCard].id,true);
					
					JOptionPane.showMessageDialog(viewMyCards, "Card special practiced");
				}
			}
		});
		
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deckIsReady = (cardN == 15);
				if (!deckIsReady)
					JOptionPane.showMessageDialog(viewMyCards, "Your Deck isn't complete");
				
				
				new Thread() {
					@Override
					public void run() {
						x.deactivateAll(new Integer(Game.myID).toString());
						int length = deck.length;
						for(int i = 0;i<length;i++) {
							x.activate(new Integer(Game.myID).toString(), new Integer(deck[i]).toString());
						}
					}
				}.start();
				//end new
				loadMainPanel();
			}
		});
		
		devolve.addActionListener(new ActionListener() {
			@Override 
			public void actionPerformed(ActionEvent e) {
				if (selectedCard == -1) {
					JOptionPane.showMessageDialog(viewMyCards, "Please select a card");
				}
				else if(dc[selectedCard].id == 1101 || dc[selectedCard].id == 1204) {
					dc[selectedCard].swapImage(dc[selectedCard].id,false);
					DatabaseAccessor x = new DatabaseAccessor();
					x.devolve(new Integer(Game.myID).toString(),new Integer(dc[selectedCard].id).toString());
					String[] user = x.findUser( 
							(
									new Integer(Game.myID).toString()
							)
						).split(" ");
					gold.setText("gold: "+user[4]+"\ndust: "+user[5]);
				}
				else {
					JOptionPane.showMessageDialog(viewMyCards, "Invalid operation");
				}
			}
		});
		
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridheight = 4;
		viewMyCards.add(scrollpane2, constraints);
		constraints.gridx = 1;
		constraints.gridheight = 1;
		constraints.gridy = 0;
		viewMyCards.add(addSelected, constraints);
		constraints.gridy = 1;
		viewMyCards.add(removeSelected, constraints);
		constraints.gridy = 2;
		viewMyCards.add(specialPractice, constraints);
		constraints.gridy = 4;
		viewMyCards.add(back, constraints);
		constraints.gridy = 3;
		viewMyCards.add(devolve,constraints);
		constraints.gridy = 4;
		constraints.gridx = 0;
		viewMyCards.add(gold,constraints);
		
		mainwindow.removeAll();
		mainwindow.add(viewMyCards);
		mainwindow.pack();
	}
  	
	public MainWindow (String myNickname) {
	  
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
		for (int i = 0; i < NumCards; i++) {
			dc[i] = new DeckCard(list.get(i), i);
		}
		
		for (int i = 0; i < NumCards; i++) {
			dc[i].checkSpecialPractice();
			//if(dc[i].specialPracticed) dc[i].swapImage();
		}
  		
  		MainWindow.myNickname = myNickname;
  		loadMainPanel();
  		mainwindow.setVisible(true);
  	}
}
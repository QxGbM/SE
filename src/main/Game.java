package main;

import java.io.IOException;
import java.util.Date;

import org.eclipse.swt.widgets.Display;

public final class Game {
	
	public static Display display = new Display();
	
	public final static UDPClient client = new UDPClient();
	public final static String ServerIP = "localhost";
	public final static int Port = 10010;
	
	public static boolean Loggedin = false;
	public static int myID = -1;
	public static String myNickname = "";
	
	public static boolean inMatch = false;
	
	public static boolean sendInMatchActions(String action) {
		try {
			client.send("4 match " + Match.matchNum + " " + myID + " " + action, ServerIP, Port);
			String response = client.get();
			int n = Integer.valueOf(response.split(" ", 2)[0]);
			String[] args = response.split(" ", n + 1);
			if (args[1].equals("action") && args[2].equals("received")) {
				return true;
			}
			return false;
		} catch (IOException e) {e.printStackTrace(); return false;}
	}
	
	public static boolean sendBattleRequest(int id) {
		try {
			client.send("4 match request " + myID + " " + id, ServerIP, Port);
			String response = client.get();
			int n = Integer.valueOf(response.split(" ", 2)[0]);
			String[] args = response.split(" ", n + 1);
			if (args[1].equals("match") && args[2].equals("pending")) {
				inMatch = true;
				Match.matchNum = Integer.valueOf(args[3]);
				new ActionRetriever().start();
				return true;
			}
			return false;
		} catch (IOException e) {e.printStackTrace(); return false;}
	}
	
	public static boolean sendBattleAccept(int matchid) {
		try {
			client.send("3 match accept " + matchid, ServerIP, Port);
			String response = client.get();
			int n = Integer.valueOf(response.split(" ", 2)[0]);
			String[] args = response.split(" ", n + 1);
			if (args[1].equals("match") && args[2].equals("accepted")) {
				inMatch = true;
				Match.matchNum = Integer.valueOf(args[3]);
				new ActionRetriever().start();
				return true;
			}
			return false;
		} catch (IOException e) {e.printStackTrace(); return false;}
	}
	
	public static void retrieveActionbox() {
		try {
			client.send("4 retrieve action " + Match.matchNum + " " + myID, ServerIP, Port);
			String response = client.get();
			int n = Integer.valueOf(response.split(" ", 2)[0]);
			String[] args = response.split(" ", n + 1);
			if (args[1].equals("actionbox") && !args[2].equals("0")) {
				int l = Integer.valueOf(args[2]);
				String s = args[3];
				for (int i = 0; i < l; i++) {
					n = Integer.valueOf(s.split(" ", 2)[0]);
					String[] action = s.split(" ", n + 2);
					if (action[1].equals("start")) {
						display.syncExec(new Runnable () {
							public void run(){
								Match.opponentID = Integer.valueOf(action[2]);
								boolean movefirst= Boolean.valueOf(action[3]);
								new Match(movefirst);
							}
						});
					} else if(action[1].equals("endturn")) {
						Match.display.syncExec(new Runnable () {
							public void run(){
								Match.opponentAftTurn();
							}
						});
						if(!Match.shell.isDisposed()) {
							Match.endTurn = false;
							Match.display.syncExec(new Runnable () {
								public void run(){
									Match.myPreTurn();
								}
							});
						}
					} else if (action[1].equals("surrender")) {
						Match.win();
					} else if (action[1].equals("skill")) {
						int p = Integer.valueOf(action[2]);
						int x = p / 4, y = p % 4;
						boolean b = Boolean.valueOf(action[3]);
						if (b) {
							int p2 = Integer.valueOf(action[4]);
							Match.coordinatesTemp[0] = p2 / 4;
							Match.coordinatesTemp[1] = p2 % 4;
							Match.display.syncExec(new Runnable () {
								public void run(){
									Match.board[x][y].spellCheckWithSelectedCoordinates();
								}
							});
						} else {
							Match.display.syncExec(new Runnable () {
								public void run(){
									Match.board[x][y].spellCheck();
								}
							});
						}
					} else if (action[1].equals("summon")) {
						int cid = Integer.valueOf(action[2]), p = Integer.valueOf(action[3]);
						int[] coordinates = new int[2];
						coordinates[0] = p / 4; coordinates[1] = p % 4;
						Match.display.syncExec(new Runnable () {
							public void run(){
								Match.summon(Match.findCardByID(cid), coordinates);
							}
						});
					}
					if(i != l-1) s = action[n+1];
				}
			}
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	public static MainWindow.Friend findFriend (int id) {
		for (int i = 0; i < MainWindow.friends.size(); i++){
			if (MainWindow.friends.get(i).ID == id) return MainWindow.friends.get(i);
		}
		return null;
	}
	
	public static void retrieveFriendbox() {
		try {
			client.send("3 retrieve message " + myID, ServerIP, Port);
			String response = client.get();
			int n = Integer.valueOf(response.split(" ", 2)[0]);
			String[] args = response.split(" ", n + 1);
			if (args[1].equals("friendbox") && !args[2].equals("0")) {
				int l = Integer.valueOf(args[2]);
				String s = args[3];
				for (int i = 0; i < l; i++) {
					n = Integer.valueOf(s.split(" ", 2)[0]);
					String[] m = s.split(" ", n + 1);
					if(m[3].equals("br")) {
						findFriend(Integer.valueOf(m[1])).BattleRequest = true;
						findFriend(Integer.valueOf(m[1])).matchNum = Integer.valueOf(m[4].split(" ", 2)[0]);
						if(i != l-1) s = m[4].split(" ", 2)[1];
					}
					else {
						int k = Integer.valueOf(m[3]);
						findFriend(Integer.valueOf(m[1])).appendMessage(m[4].substring(0, k), new Date());
						if(i != l-1) s = m[4].substring(k + 1);
					}
				}
			}
		} catch (IOException e) {e.printStackTrace();}
	}

	public static void main(String[] args) throws IOException {
		Login.main();
		
		new MessageRetriever().start();
		
		MainWindow.friends.add(new MainWindow.Friend(0, "server", true, "Chatting with server:\n"));
		MainWindow.friends.add(new MainWindow.Friend(100, "Tester0", true, ""));
		
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				new MainWindow(myNickname);
			}
		});
		
		while(Loggedin) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
		client.close();
	}
	
	public final static class MessageRetriever extends Thread {
		public void run () {
			while (Loggedin) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {e.printStackTrace();}
				retrieveMessagebox();
			}
		}
	}
	
	public final static class ActionRetriever extends Thread {
		public void run () {
			while (inMatch) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {e.printStackTrace();}
				retrieveActionbox();
			}
		}
	}
}



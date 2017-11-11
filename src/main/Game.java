package main;

import org.eclipse.swt.widgets.Display;

import protocol.*;

public final class Game {
	
	public static Display display = new Display();
	
	public final static String ServerIP = "localhost";
	public final static int Port = 10010;
	
	public static boolean Loggedin = false;
	public static int myID = -1;
	public static String myNickname = "";
	
	public static boolean inMatch = false;

	public static void startMatch (int oID, int mID, boolean moveFirst) {
		display.syncExec(new Runnable () {
			public void run(){
				Match.startMatch(oID, mID, moveFirst);
			}
		});
	}
	
	public static MainWindow.Friend findFriend (int id) {
		for (int i = 0; i < MainWindow.friends.size(); i++){
			if (MainWindow.friends.get(i).ID == id) return MainWindow.friends.get(i);
		}
		return null;
	}

	public static void main(String[] args) {
		NetClient.startNetClient();
		/*LoginWindow.main();
		
		MainWindow.friends.add(new MainWindow.Friend(0, "server", true, "Chatting with server:\n"));
		MainWindow.friends.add(new MainWindow.Friend(100, "Tester0", true, ""));
		
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				new MainWindow(myNickname);
			}
		});
		
		new MessageRetriever().start();*/
		Loggedin = true;
		inMatch = true;
		myID = 100;
		new Action("Action 1000 101 start false").clientParse();
		new ActionRetriever(1000).start();
		
		while(Loggedin) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
		display.dispose();
		NetClient.close();
		
	}
	
	public final static class MessageRetriever extends Thread {
		
		public void run () {
			while (Loggedin) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {e.printStackTrace();}
				Retrieve r = new Retrieve(myID);
				NetClient.send(r.toString());
				String response = NetClient.get();
				Message.MessageBox mb = new Message.MessageBox(response);
				mb.clientParse();
			}
		}
	}
	
	public final static class ActionRetriever extends Thread {
		public int matchNum;
		
		public ActionRetriever(int n) {
			matchNum = n;
		}
		
		public void run () {
			while (inMatch) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {e.printStackTrace();}
				Retrieve r = new Retrieve(matchNum, myID);
				NetClient.send(r.toString());
				Action.ActionBox ab = new Action.ActionBox(NetClient.get());
				display.syncExec(new Runnable () {
					@Override
					public void run(){
						ab.clientParse();
					}
				});
			}
		}
	}
}



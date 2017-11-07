package main;

import java.io.*;
import java.net.*;
import java.util.Date;

import javax.swing.JOptionPane;

import main.Game.ActionRetriever;
import protocol.*;

public final class NetClient {
	
	public static DatagramSocket ds = null;
	public static DatagramPacket sendDp = null;
	public static DatagramPacket receiveDp = null;
	
	public static void startNetClient() {
		try {
			ds = new DatagramSocket();
		} catch (SocketException e) {e.printStackTrace();}
	}
	
	public static void sendLogin(String username, String password) {
		Login l = new Login(username, password);
		send(l.toString());
		String response = get();
		new LoginResult(response).clientParse();
	}
	
	public static void sendMessage(String message, int recipient) {
		Message m = new Message(Game.myID, recipient, message);
		Game.findFriend(recipient).appendMyMessage(message, new Date());
		send(m.toString());
		String response = get();
		new ACK(response).clientParse();
	}
	
	public static void sendBattleRequest(int recipient) {
		Message m = new Message(Game.myID, recipient);
		send(m.toString());
		new Thread () {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(MainWindow.mainwindow, "Request Sent");
			}
		}.start();
		String response = get();
		ACK ack = new ACK(response);
		ack.clientParse();
		if(ack.acked) {
			Game.inMatch = true;
			Match.matchNum = ack.intField;
			new ActionRetriever(ack.intField).start();
		}
	}
	
	public static void sendBattleAccept(int matchid, boolean accept) {
		Message m = new Message(Game.myID, matchid, accept);
		send(m.toString());
		String response = get();
		ACK ack = new ACK(response);
		ack.clientParse();
		if(ack.acked) {
			Game.inMatch = true;
			Match.matchNum = ack.intField;
			new ActionRetriever(ack.intField).start();
		}
	}
	
	public static void sendAction(Action a) {
		send(a.toString());
		String response = get();
		new ACK(response).clientParse();
	}
	
	public static void send(String s) {
		byte[] data = new byte[1024];
		data = s.getBytes();
		try {
			InetAddress address = InetAddress.getByName(Game.ServerIP);
			sendDp = new DatagramPacket(data, data.length, address, Game.Port);
			ds.send(sendDp);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public static String get() {
		byte[] b = new byte[1024];
		receiveDp = new DatagramPacket(b,b.length);
		try {
			ds.receive(receiveDp);
		} catch (IOException e) {e.printStackTrace();}
		byte[] response = receiveDp.getData();
		int len = receiveDp.getLength();
		return new String(response, 0, len);
	}
	
	public static void close() {ds.close();}
}
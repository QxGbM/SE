package main;

import java.io.*;
import java.net.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;

import main.Game.ActionRetriever;
import protocol.*;

public final class NetClient {
	
	public static DatagramSocket ds = null;
	public static DatagramPacket sendDp = null;
	public static DatagramPacket receiveDp = null;
	
	public static Lock lock = null;
	
	public static void startNetClient() {
		try {
			ds = new DatagramSocket();
			lock = new ReentrantLock();
		} catch (SocketException e) {e.printStackTrace();}
	}
	
	public static void sendLogin(String username, String password) {
		Login l = new Login(username, password);
		String response = send(l.toString());
		new LoginResult(response).clientParse();
	}
	
	public static void sendMessage(String message, int recipient) {
		Message m = new Message(Game.myID, recipient, message);
		String response = send(m.toString());
		new ACK(response).clientParse();
	}
	
	public static void sendBattleRequest(int recipient) {
		Message m = new Message(Game.myID, recipient);
		new Thread () {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(MainWindow.mainwindow, "Request Sent");
			}
		}.start();
		String response = send(m.toString());
		ACK ack = new ACK(response);
		ack.clientParse();
		if(ack.acked) {
			m = new Message(Game.myID, ack.intField, true);
			response = send(m.toString());
			ack = new ACK(response);
			ack.clientParse();
			if(ack.acked) {
				Game.inMatch = true;
				Match.matchNum = ack.intField;
				Game.actionRetriever = new ActionRetriever(ack.intField);
				Game.actionRetriever.start();
			}
		}
	}
	
	public static void sendBattleAccept(int matchid, boolean accept) {
		Message m = new Message(Game.myID, matchid, accept);
		String response = send(m.toString());
		ACK ack = new ACK(response);
		ack.clientParse();
		if(ack.acked) {
			Game.inMatch = true;
			Match.matchNum = ack.intField;
			Game.actionRetriever = new ActionRetriever(ack.intField);
			Game.actionRetriever.start();
		}
	}
	
	public static void sendAction(Action a) {
		String response = send(a.toString());
		new ACK(response).clientParse();
	}
	
	public static void sendQuickMatch(QuickMatch m) {
		
		String response = send(m.toString());
		new ACK(response).clientParse();
	}
	
	public static String send(String s) {
		lock.lock();
		try {
			byte[] data = new byte[1024];
			data = s.getBytes();
			try {
				InetAddress address = InetAddress.getByName(Game.ServerIP);
				sendDp = new DatagramPacket(data, data.length, address, Game.Port);
				ds.send(sendDp);
			} catch (IOException e) {e.printStackTrace();}
			
			byte[] b = new byte[1024];
			receiveDp = new DatagramPacket(b,b.length);
			try {
				ds.receive(receiveDp);
			} catch (IOException e) {e.printStackTrace();}
			byte[] response = receiveDp.getData();
			int len = receiveDp.getLength();
			s = new String(response, 0, len);
		} finally {
			lock.unlock();
		}
		return s;
	}
	
	public static void close() {ds.close();}
}
package main;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;

import main.Game.ActionRetriever;
import protocol.*;

public final class NetClient {
	
	public static DatagramSocket ds = null;
	public static DatagramPacket sendDp = null;
	public static DatagramPacket receiveDp = null;
	public static Socket sock;
	public static DataOutputStream toKhala;
	public static InputStream in;
	
	public static Lock lock = null;
	
	  private static final long nn = 60293329013L;
	  private static final long ee = 11;
	  private static final long dd = 1370291771L;
	
	
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
			long[] rawSignature = SecurityTools.sign(s);
			String signature = new Long(rawSignature[0]).toString() + "," + new Long(rawSignature[1]).toString() + "," + new Long(rawSignature[2]).toString();
			try {
				//sendDp = new DatagramPacket(data, data.length, Game.ServerIP, Game.Port);
				//ds.send(sendDp);
				sock =  new Socket(Game.ServerIP,Game.Port);
				toKhala = new DataOutputStream(sock.getOutputStream());
				String outgoing = SecurityTools.encrypt(s, ee, nn);
				
				toKhala.writeUTF(outgoing +'\n' + signature + '\n');
				toKhala.flush();
				in = sock.getInputStream();
				
			} catch (IOException e) {e.printStackTrace();}
			
			//byte[] b = new byte[1024];
			//receiveDp = new DatagramPacket(b,b.length);
			
			try {
				//ds.receive(receiveDp);
				while(in.available() == 0);
				//InputStreamReader oracle= new InputStreamReader(in,Charset.forName("utf-8"));
				//BufferedReader info = new BufferedReader(oracle);
				DataInputStream info = new DataInputStream(in);
				String[] content = info.readUTF().split("\n");
				
				StringBuilder x = new StringBuilder();
				String decrypted = SecurityTools.decrypt(content[0], dd, nn);
				
				x.append(decrypted);
				String[] signs = content[1].split(",");
				long[] rawSign = {Long.parseLong(signs[0]),Long.parseLong(signs[1]),Long.parseLong(signs[2])};
				//System.out.println("testing signature verification:"+SecurityTools.verify(x.toString(),rawSign[0],rawSign[1],rawSign[2]));
				s = x.toString();
				
			} catch (IOException e) {e.printStackTrace();}
			//byte[] response = receiveDp.getData();
			//int len = receiveDp.getLength();
			//s = new String(response, 0, len);
		} finally {
			lock.unlock();
		}
		return s;
	}
	
	
	public static void close() {try{sock.close();}catch(IOException e) {System.out.println("io at NetClient last line");;}}
}
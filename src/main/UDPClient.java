package main;

import java.io.*;
import java.net.*;
import java.util.Date;

import protocol.Message;

public final class UDPClient {
	
	public static DatagramSocket ds = null;
	public static DatagramPacket sendDp = null;
	public static DatagramPacket receiveDp = null;
	
	public UDPClient() {
		try {
			ds = new DatagramSocket();
		} catch (SocketException e) {e.printStackTrace();}
	}
	
	public static class Info {
		public int length;
		public String[] args;
		public Info(String response) {
			length = Integer.valueOf(response.split(" ", 2)[0]);
			args = response.split(" ", length + 1);
		}
		public boolean parse() {
			return false;
		}
	}
	
	
	public static class LoginInfo extends Info {
		public LoginInfo(String response) {
			super(response);
		}
		public boolean parse() {
			if (args[1].equals("login") && args[2].equals("successful")) {
				Game.Loggedin = true; 
				Game.myID = Integer.valueOf(args[3]);
				Game.myNickname = args[4];
				return true;
			}
			else return false;
		}
	}
	
	public static class MessageInfo extends Info {
		public MessageInfo(String response) {
			super(response);
		}
		public boolean parse() {
			if (args[1].equals("message") && args[2].equals("received")) {
				return true;
			}
			else return false;
		}
	}
	
	public boolean sendLogin(String username, String password) {
		String s = "3 login " + username + " " + password;
		send(s);
		String response = get();
		return new LoginInfo(response).parse();
	}
	
	public boolean sendMessage(String message, int recipient) {
		String s = "5 message " + Game.myID + " " + recipient + " " + message.length() + " " + message;
		Game.findFriend(recipient).appendMyMessage(message, new Date());
		send(s);
		String response = get();
		return new MessageInfo(response).parse();
	}
	
	public void send(String s) {
		byte[] data = new byte[1024];
		data = s.getBytes();
		try {
			InetAddress address = InetAddress.getByName(Game.ServerIP);
			sendDp = new DatagramPacket(data, data.length, address, Game.Port);
			ds.send(sendDp);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public String get() {
		byte[] b = new byte[1024];
		receiveDp = new DatagramPacket(b,b.length);
		try {
			ds.receive(receiveDp);
		} catch (IOException e) {e.printStackTrace();}
		byte[] response = receiveDp.getData();
		int len = receiveDp.getLength();
		return new String(response, 0, len);
	}
	
	public void close() {ds.close();}
}
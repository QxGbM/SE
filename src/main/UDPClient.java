package main;

import java.io.*;
import java.net.*;

public final class UDPClient {
	
	private DatagramSocket ds = null;
	private DatagramPacket sendDp = null;
	private DatagramPacket receiveDp = null;
	
	public UDPClient() {
		try {
			ds = new DatagramSocket();
		} catch (SocketException e) {e.printStackTrace();}
	}
	
	public void send(String message, String ServerIP, int Port) throws IOException{
		byte[] data = new byte[1024];
		data = message.getBytes();
		InetAddress address = InetAddress.getByName(ServerIP);
		sendDp = new DatagramPacket(data, data.length, address, Port);
		ds.send(sendDp);
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
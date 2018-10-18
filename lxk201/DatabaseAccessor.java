package main;
import java.net.*;
import java.io.*;
import java.util.*;
public class DatabaseAccessor {

	private Socket toKhala;
	
	public DatabaseAccessor() {
		try {
			toKhala = new Socket(InetAddress.getByName("159.65.180.211"),4500);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String findFriends(String id) {
		try {
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			message.writeBytes("find_friends,"+id+"," + "\n");
			InputStream oracle = toKhala.getInputStream();
			while(oracle.available() == 0);//blocks when there is no message
			InputStreamReader response = new InputStreamReader(oracle);
			return new BufferedReader(response).readLine();
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public boolean loginCheck(String act, String pw) {
		try {
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			message.writeBytes("verify_login,"+act+","+pw + "\n");
			InputStream oracle = toKhala.getInputStream();
			while(oracle.available() == 0);//blocks when there is no message
			InputStreamReader response = new InputStreamReader(oracle);
			return new BufferedReader(response).readLine().equals("1");
		}
		catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String loadAllUsers() {
		try {
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			message.writeBytes("get_all\n");
			InputStream oracle = toKhala.getInputStream();
			
			while(oracle.available() == 0);
			
			InputStreamReader response = new InputStreamReader(oracle);
			return new BufferedReader(response).readLine();
		}
		catch(Exception e) {
			e.printStackTrace();
			return "error";
		}
		
	}
	
	
	public boolean checkSpecialPractice(String id, String c_id) {
		try {
			//System.out.println(id +" "+ c_id);
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			message.writeBytes("check_evolution,"+ id+"\n");
			InputStream oracle = toKhala.getInputStream();
			while(oracle.available() == 0);
			InputStreamReader response = new InputStreamReader(oracle);
			String[] cards = new BufferedReader(response).readLine().split(",");//parse the response
			String[] cardsInfo = new String[4];//table for card has id, c_id, level, and special_practiced
			for(int i = 0;i<cards.length;i++) {
				cardsInfo = cards[i].split(" ");
				
				if(cardsInfo[1].equals(c_id)) {//if the card is found, check special practice status
					
					return cardsInfo[3].equals("1");
				}
			}
			return false;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public void terminate() {
		try {
			DataOutputStream termination = new DataOutputStream(toKhala.getOutputStream());
			termination.writeBytes("kill");
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean win(String id) {
		try {
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			message.writeBytes("win,"+id+"\n");
			InputStream oracle =  toKhala.getInputStream();
			while(oracle.available() == 0);
			InputStreamReader response = new InputStreamReader(oracle);
			return new BufferedReader(response).readLine().equals("1");
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public boolean loss(String id) {
		try {
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			
			message.writeBytes("loss,"+id+"\n");
			InputStream oracle =  toKhala.getInputStream();
			System.out.print("loss called and meessage  sent");
			while(oracle.available() == 0);
			System.out.println("oracle read");
			InputStreamReader response = new InputStreamReader(oracle);
			System.out.println("responses read");
			return new BufferedReader(response).readLine().equals("1");
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static void main(String[] abs) {
		
	}
	
}

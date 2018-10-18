package server;
import java.net.*;
import java.io.*;

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
			message.flush();
			InputStream oracle = toKhala.getInputStream();
			while(oracle.available() == 0);//blocks when there is no message
			InputStreamReader response = new InputStreamReader(oracle);
			return new BufferedReader(response).readLine();
		}
		catch(IOException e) {
			e.printStackTrace();
			return "error";
		}
		
	}
	
	public String findUser(String id) {
		try {
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			message.writeBytes("get,"+id + "\n");
			message.flush();
			InputStream oracle = toKhala.getInputStream();
			while(oracle.available() == 0);//blocks when there is no message
			InputStreamReader response = new InputStreamReader(oracle);
			return new BufferedReader(response).readLine();
		}
		catch(IOException e) {
			e.printStackTrace();
			return "error";
		}
	}
	
	public boolean loginCheck(String act, String pw) {
		try {
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			message.writeBytes("verify_login,"+act+","+pw + "\n");
			message.flush();
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
			message.flush();
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
			message.flush();
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
			message.flush();
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
	
	public boolean activate(String id, String c_id) {
		try {
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			message.writeBytes("activate,"+id +"," +c_id +"\n");
			message.flush();
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
	
	public boolean deactivate(String id, String c_id) {
		try {
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			message.writeBytes("deactivate,"+id+","+c_id+"\n");
			message.flush();
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
	
	public boolean activateAll(String id) {
		try {
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			message.writeBytes("activate,"+id+"\n");
			message.flush();
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
	
	
	public boolean deactivateAll(String id) {
		try {
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			message.writeBytes("deactivate,"+id+"\n");
			message.flush();
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
	
	public boolean updateCurrency(String id, String gold, String dust) {
		
		try {
			DataOutputStream message =new DataOutputStream(toKhala.getOutputStream());
			
			message.writeBytes("money," + id + "," + gold+","+dust+"\n");
			message.flush();
			InputStream oracle = toKhala.getInputStream();
			while(oracle.available() == 0);
			InputStreamReader response  = new InputStreamReader(oracle);
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
			message.flush();
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
	
	public boolean addCard(String id, String c_id) {
		try {
DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			
			message.writeBytes("add_card,"+id+"," + c_id +"\n");
			message.flush();
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
	
	public boolean specialPractice(String id, String c_id) {
		try {
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
						
			message.writeBytes("evolve,"+id+"," + c_id +"\n");
			message.flush();
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
	
	public boolean devolve(String id, String c_id) {
		try {
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
						
			message.writeBytes("devolve,"+id+"," + c_id +"\n");
			message.flush();
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
	
	public String getAllCardInfo(String id) {
		try {
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			message.writeBytes("get_card_info,"+id+"\n");
			message.flush();
			InputStream oracle = toKhala.getInputStream();
			
			while(oracle.available() == 0);
			
			InputStreamReader response = new InputStreamReader(oracle);
			return new BufferedReader(response).readLine();
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String[] getCardInfo(String id, String c_id) {
		try {
			String test = this.getAllCardInfo(id);
			
			String[] cards = test.split(",");
			String details[] = new String[5];
			
			for(int i = 0;i<cards.length;i++) {
				details = cards[i].split(" ");
				
				if(details[1].equals(c_id))
					return details;
			}
			return null;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void addFriend(String id,String f_id) {
		try {
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			message.writeBytes("add_friend,"+id+","+f_id+"\n");
			message.flush();						
		}
		catch(Exception e) {
			e.printStackTrace();
			
		}
	}	
	
	public void removeFriend(String id,String f_id) {
		try {
			
			DataOutputStream message = new DataOutputStream(toKhala.getOutputStream());
			message.writeBytes("unadd_friend,"+id+","+f_id+"\n");
			message.flush();						
		}
		catch(Exception e) {
			e.printStackTrace();
			
		}
		
	}
	public static void main(String[] abs) {
		
	}
	
}

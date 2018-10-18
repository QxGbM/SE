import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.*;

public class DBCommandReceive{
	
	private volatile ServerSocket khala;
	
	public DBCommandReceive(int port){
		try{
		khala = new ServerSocket(port);
		}
		catch(Exception e){
				e.printStackTrace();
		}
	}
	
	private void handler(Socket session){
		new Thread(){
			@Override 
			public void run(){
				try{
					InputStream in= session.getInputStream();
					FetchUserInfo x = new FetchUserInfo();
					UpdateInfo y = new UpdateInfo();
					String response = new String();
					System.out.println("started");
					while(!session.isClosed()){
						while(in.available() == 0);//block if no message
						BufferedReader oracle= new BufferedReader(new InputStreamReader(in));
						String[] commands = oracle.readLine().split(",");
						if(commands[0].equals("kill")){
							session.close();
							return;
						}
						else if(commands[0].equals("verify_login")){
							if(x.verifyLogin(commands[1],commands[2]))
								response = "1\n";
							else
								response = "0\n";
						}
						else if(commands[0].equals("get_all")){
							System.out.println("get all confirm");
							response = x.loadAllUser() + "\n";
						}
						else if(commands[0].equals("check_evolution")){
							response = x.getCardInfo(Float.parseFloat(commands[1])) + "\n";
						}
						else if(commands[0].equals("win")){
							if(y.win(Float.parseFloat(commands[1])))
								response = "1\n";
							else 
								response = "0\n";
						}
						else if(commands[0].equals("loss")){
							
							if(y.lose(Float.parseFloat(commands[1])))
								response = "1\n";
							else 
								response = "0\n";
						}
						else if(commands[0].equals("find_friends")){
							
						}
						DataOutputStream toClient = new DataOutputStream(session.getOutputStream());
						toClient.writeBytes(response);
						toClient.flush();
					}
				}
				catch(Exception e){
					
					e.printStackTrace();
					return;
				}
			}
			
		}.start();
		
	}
	
	
	
	public static void main(String[] abs){
		try{
			DBCommandReceive y = new DBCommandReceive(4500);
		while(1==1){
			Socket x = y.khala.accept();
			System.out.println("Khala hears you!");
			y.handler(x);
		}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
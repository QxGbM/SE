import java.sql.*;


public class FetchUserInfo{
 
 private static Connection con;
 private static String driver ="com.mysql.jdbc.Driver" ;

 
 public FetchUserInfo(){
    try{
   //System.out.println("exceptin\n");
   Class.forName(driver);  
   con = DriverManager.getConnection("jdbc:mysql://localhost:3306/SP","root","20!8Case20!8");      
    }
   catch(Exception e){
    e.printStackTrace();
   }
    
  }
   
 /*Finish this connection*/
 public void close(){
  try{
   con.close();
  }
  catch(Exception e){
   e.printStackTrace();
  }
  
 }
 
 /* takes an incoming login request to see if this person exists
 @param act  a string representation of the requester's account name
 @param pw  a string representation of the requester's password
 @return true if this person exist. False if this person does not exist
 */
 public boolean verifyLogin(String act, String pw){
  ResultSet r;
  try{
   String q = "select 'name', password "+
   "from Account "+
   "where Account.name = " + "'" + act + "'" + " and " + "'" + pw + "'" + "= Account.password;";
   Statement i = con.createStatement();
   r = i.executeQuery(q);
   if(!r.next())
    return false;
  
  }
  catch(SQLException e){
   e.printStackTrace();
   return false;
  }
  return true;
 }
 
 /* find a user's full information based on an account given
 @param act  a string representation of an user's account name
 @return an error message if this person does not exist. Or a user's full information
 */
 public String getUserInfo(String act){
  ResultSet r;
  String s = new String();;
  try{
  String q = "select * from Account "+
  "where " + "'" + act + "'" +" = name;";
  Statement i = con.createStatement();
  r = i.executeQuery(q);
  
  if(!r.next())
   return "no such player exist\r";
	System.out.println(r.getRow());
	System.out.println(r.first());
	System.out.println(r.getRow());
  s = new String(r.getString(1) + " " + r.getString(2) +
  " " + r.getString(3) + " " + r.getString(4) + " " + r.getString(5) +
  " " + r.getString(6) + " " + r.getString(7));
  }
  catch(SQLException e){
   e.printStackTrace();
  }
  return s;
 }
 
  /* find a user's full information based on an account given
 @param act  a string representation of an user's account name
 @return an error message if this person does not exist. Or a user's full information
 */
 public String getUserInfo(float id){
  ResultSet r;
  String s = new String();;
  try{
  String q = "select * from Account "+
  "where " + "'" + id + "'" +" = id;";
  Statement i = con.createStatement();
  r = i.executeQuery(q);
  
  if(!r.next())
   return "no such player exist\r";
	
  s = new String(r.getString(1) + " " + r.getString(2) +
  " " + r.getString(3) + " " + r.getString(4) + " " + r.getString(5) +
  " " + r.getString(6) + " " + r.getString(7));
  }
  catch(SQLException e){
   e.printStackTrace();
  }
  return s;
 }
 
 public String loadAllUser(){
	 
	 ResultSet r;
	 StringBuilder s = new StringBuilder();
	 try{
		 String q = "select * from Account;";
		 Statement i = con.createStatement();
		 r = i.executeQuery(q);
		 
		 while(r.next()){
			 s.append(r.getString(1) + " ");
			 s.append(r.getString(2) + " ");
			 s.append(r.getString(3) + " ");
			 s.append(r.getString(4) + " ");
			 s.append(r.getString(5) + " ");
			 s.append(r.getString(6) + " ");
			 s.append(r.getString(7) + ",");
			 System.out.println("executing");
		 }
		 
	 }
	 catch(Exception e){
		 e.printStackTrace();
		 return "error";
	 }
	 return s.toString();
 }
 
 
 /* find a user's friends
 @param act  a string representation of an user's account
 @return  this user's friends. Or an error message if this person does not exist, or this person has no friends
 */
 public String getFriendList(String act){
   ResultSet r;
   StringBuilder x = new StringBuilder();;
   try{
     String q = "select FriendList.f_id from Account, FriendList "+
       "where " + "'" +act+"'" + " = Account.name and Account.id = FriendList.id";
     Statement i = con.createStatement();
     r = i.executeQuery(q);
     if(!r.next())
       return "no such player exist. Or this player has no friends";     
     System.out.println(r.first());
     do {
       
         x.append(r.getString(1) + " ");
       
     }
	 while(r.next());
   }
   catch(SQLException e){
     e.printStackTrace();
   }
   return x.toString();
 }
 
  /* find a user's friends
 @param id a string representation of an user's id
 @return  this user's friends. Or an error message if this person does not exist, or this person has no friends
 */
 public String getFriendList(float id){
   ResultSet r;
   StringBuilder x = new StringBuilder();;
   try{
     String q = "select FriendList.f_id from Account, FriendList "+
       "where " +id + " = Account.id and Account.id = FriendList.id";
     Statement i = con.createStatement();
     r = i.executeQuery(q);
     if(!r.next())
       return "no such player exist. Or this player has no friends";     
     System.out.println(r.first());
     do {
       
         x.append(r.getString(1) + " ");
       
     }
	 while(r.next());
   }
   catch(SQLException e){
     e.printStackTrace();
   }
   return x.toString();
 }
 
 /* find a user's card information
 @param id  the float is this user's id
 @return  card information of this user. Or returns an error message if this person does not exist,
 or this person has no cards
 */
 public String getCardInfo(float id){
  ResultSet r;
  StringBuilder x = new StringBuilder();
  try{
   String q = "select * from CardInfo "+
   "where " + "'" + id + "'" +" = CardInfo.id";
   Statement i = con.createStatement();
   r = i.executeQuery(q);
   if(!r.next())
    return "no such player exist, or this player has no deck";
  r.first();
	
   do{
   
  x.append(r.getString(1) + " "+ r.getString(2) + " " + r.getString(3) + " " + r.getString(4) +",");
   
   }
   while(r.next());
 
  }
  catch(SQLException e){
   e.printStackTrace();
  }
  return x.toString();
 }
 
 public static void main(String[] abs){
   FetchUserInfo test = new FetchUserInfo();
   System.out.println(test.getCardInfo(100));
	// 65347 test
	// 12805 testor
 }
}
 

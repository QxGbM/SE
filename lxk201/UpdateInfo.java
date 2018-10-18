import java.sql.*;

public class UpdateInfo{
	 private static Connection con;
	private static String driver ="com.mysql.jdbc.Driver" ;
	private FetchUserInfo tester;
	public UpdateInfo(){
    try{
	//System.out.println("exceptin\n");
	Class.forName(driver);  
	con = DriverManager.getConnection("jdbc:mysql://localhost:3306/SP","root","20!8Case20!8");      
    }
   catch(Exception e){
    e.printStackTrace();
   }
    
  }
  
  /* update a player's match history
  @param act string representation of the player's account name
  @return true if update went through. false if update failed for any reason
  */
  public boolean win(String act){

	  try{
		  String q = "update Account "+
		  "set win = win + 1 "+
		  "where name = " +"'" + act +"'";
		  Statement i = con.createStatement();
		  i.executeUpdate(q);
		  return true;
	  }
	  catch(SQLException e){
		  e.printStackTrace();
		  return false;
	  }
	
  }
  
  public boolean win(float id){

	  try{
		  String q = "update Account "+
		  "set win = win + 1 "+
		  "where id = " +"'" + id +"'";
		  Statement i = con.createStatement();
		  i.executeUpdate(q);
		  return true;
	  }
	  catch(SQLException e){
		  e.printStackTrace();
		  return false;
	  }
	
  }
  
  /* update a player's match history
  @param act string representation of the player's account name
  @return true if update went through. false if update failed for any reason
  */
  public boolean lose(String act){

	  try{
		  String q = "update Account "+
		  "set loss = loss + 1 "+
		  "where name = " +"'" + act +"'";
		  Statement i = con.createStatement();
		  i.executeUpdate(q);
		  return true;
	  }
	  catch(SQLException e){
		  e.printStackTrace();
		  return false;
	  }
	  	  
  }
  
  
  public boolean lose(float id){
	System.out.println("loss called on id: "+id);
	  try{
		  String q = "update Account "+
		  "set loss = loss + 1 "+
		  "where id = " +"'" + id +"'";
		  Statement i = con.createStatement();
		  i.executeUpdate(q);
		  return true;
	  }
	  catch(SQLException e){
		  e.printStackTrace();
		  return false;
	  }
	  	  
  }
  
  /* update this person's friend list
  @param id  this player's id
  @param f_id  this player's friend's id
  @return true if added. False otherwise
  */
  public boolean addFriend(float id, float f_id){
	  try{
		  String q = "insert into FriendList (id, f_id) values ('" +id+ "','" + f_id+"')";
		  Statement i = con.createStatement();
		  i.executeUpdate(q);
		  return true;
	  }
	  catch(SQLException e){
		  e.printStackTrace();
		return false;		  
	  }
	  
  }
  
  /*set a card of a player to certain level
  @param id  this player's card's id
  @param c_id  this player's card's id 
  @param level  the level to set this card to
  @return true if successful. False otherwise
  */
  public boolean setCardLevel(float id, float c_id ,float level){
	try{
		String q = "update CardInfo " +
		"set level =" +"'" + level + "' " +
		"where id = '"  + id+ "' and c_id ='"+ c_id+"'";
		Statement i = con.createStatement();
		i.executeUpdate(q);
		return true;
	}
	catch(SQLException e){
		e.printStackTrace();
		return false;
	}
	 
  }
  
  /*add  a card into a player's inventory
  @param id  target player's id
  @param c_id  target card id
  @return true if added. False otherwise
  */
  public boolean addCard(float id, float c_id){
	  try{
		  String q = "insert into CardInfo (id, c_id, level, special_practiced) "+
		  "values ('" + id + "','" + c_id + "', 1, false)";    
		  Statement i = con.createStatement();
		  i.executeUpdate(q);
		  return true;
	  }
	  catch(SQLException e){
		  e.printStackTrace();
		  return false;
	  }
	 
  }
  
  /*remove a card  from a player's inventory
  @param id  target player's id
  @param c_id  target card's id
  @return true if removed. False otherwise
  */
  public boolean removeCard(float id, float c_id){
	  try{
		  String q="delete from CardInfo "+
		  "where id=" + "'" + id +"' and c_id ='" + c_id + "'";
		  Statement i = con.createStatement();
		  i.executeUpdate(q);
		  return true;
	  }
	  catch(SQLException e){
		  e.printStackTrace();
		  return false;
	  }
	 
  }
  
  /* unlock card's upgrade effect
  @param id  target player's id
  @param c_id  target card's id
  @return true if upgraded. False otherwise
  */
  public boolean evoleCard(float id, float c_id){
	  try{
		  String q = "update CardInfo " +
		  "set special_practiced = true "+
		  "where id = '" + id + "' and c_id = '" + c_id + "'";
		  Statement i = con.createStatement();
		  i.executeUpdate(q);
		  return true;
	  }
	  catch(SQLException e){
		  e.printStackTrace();
		  return false;
	  }
	  
  }
  
  /* remove a person from a target player's  friendlist
  @param id target player's id
  @param f_id target player's friend's id
  @return true if removed. False otherwise
  */
  public boolean removeFriend(float id, float f_id){
	  try{
		  String q = "delete from FriendList "
		  +"where id=" +"'" + id + "'" + " and f_id = " + "'" + f_id + "'";
		  Statement i = con.createStatement();
		  i.executeUpdate(q);
		  return true;
	  }
	  catch(SQLException e){
			e.printStackTrace();
			return false;
	  }
	  
  }
  
  /* remove a player
  @param act  account name of the target player
  @param id  id of the target player
  @return true if removed. False otherwise
  */
  public boolean removeAccount(String act, float id){
	  try{
		  String q = "delete from Account "+
		  "where name = " + "'" + act+"'" + " and id= " +"'" + id + "'";
		  Statement i = con.createStatement();
		  i.executeUpdate(q);
		  return true;
	  }
	  catch(SQLException e){
		  e.printStackTrace();
		  return false;
	  }
  }
  
  /* update this player's currency
  @param act  account name of this player
  @param gold amount of gold earned/lost
  @param dust amount of dust earned/lost
  @return true if updated. False otherwise
  */
  public boolean updateCurrency( String act, float gold, float dust){
	  try{
		  String q = "update Account " +
		  "set gold = gold + " +"'" + gold + "'"+
		  " where name = " + "'" + act + "'";
		  Statement i = con.createStatement();
		  if(gold != 0){
			  i.executeUpdate(q);
		  }
		  q = "update Account " +
		  "set dust = dust + " +"'" + dust + "'"+
		  " where name = " + "'" + act + "'";
		  i = con.createStatement();
		  if(dust != 0){
			  i.executeUpdate(q);
		  }
		  return true;
	  }
	  catch(SQLException e){
		  e.printStackTrace();
		  return false;
	  }
  }
  
  public static void main(String[] abs){
	  UpdateInfo x = new  UpdateInfo();
	  x.tester = new FetchUserInfo();
	  String[] info = x.tester.getUserInfo(100).split(" ");
	  for(int i = 0;i<info.length;i++){
		  System.out.println(info[i]);
	  }
	  System.out.println(x.win(info[1]));
	  
  }
  
}
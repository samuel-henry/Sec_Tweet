package sec;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

//represents a user and contains static methods for user actions
@SuppressWarnings("rawtypes")
public class User {
	private String currUsername = "";
	private int currId;
	
	//constructor
	protected User (String username, int id) {
		this.currUsername = username;
		this.currId = id;
	}
	
	//getter for username
	protected String getUsername() {
		return currUsername;
	}
	
	//getter for id
	protected int getId() {
		return currId;
	}
	
	//allow a user to sign in
	//TODO: zeroOut pword[]
	protected static int signIn(String username, char[] pword, Connection cnxn) {
		//make sure username, password, and cnxn were passed in
		if (username != null && pword != null && cnxn != null) {
			Statement stmnt;
			ResultSet rs;
			
			try {
				stmnt = cnxn.createStatement();
				
				//get user info from db to compare with entered password
				String query = "SELECT id, password, salt FROM Users WHERE name = '" + username + "'";
				rs = stmnt.executeQuery(query);
				
				//if we got a result the user exists in the db
				if (rs.next()) {
					//get the id from the resultset
					int id = rs.getInt("id");
					
					//get the hashcode from the resultset
					String hashCode = rs.getString("password");
					
					//get the salt from the resultset
					long salt = rs.getLong("salt");
					
					//verify that the user entered the right password
					if (Password.checkPassword(hashCode, salt, pword)) {
						//return the user id if so
						return id;
					} else {
						//return -1 if not
						return -1;
					}
					
				} else {
					//user not in database. TODO: alert user
					return -1;
				}
			} catch (NumberFormatException ex) {
				//resultset didn't return data in the right format. signin failed.
				return -1;
			} catch (SQLException ex) {				
				//problem connecting to db. signin failed.
				return -1;
			}	
		} else {
			//missing necessary input. signin failed.
			return -1;
		}
	}
	
	//get a user from the db by username
	protected static int findUser(String username, Connection cnxn) {
		Statement stmnt;
		ResultSet rs = null;
		
			try {
				stmnt = cnxn.createStatement();
				
				//get user id
				String query = "SELECT id FROM Users WHERE name = '" + username + "'";
				rs = stmnt.executeQuery(query);
				
				//see if user exists in db. if so, return id, else indicate that requested user wasn't foundd
				if (rs.next()) {
					return rs.getInt("id");
				} else {
					return -1;
				}
				
			} catch (SQLException ex){
				//sql error, indicate that requested user wasn't found
				return -1;
			}
		
	}
	
	//create an account
	public static Tuple createAccount(String username, char[] chPassword, Connection cnxn) {	
		ResultSet rs = null;
		Statement stmnt = null;
			
		//verify the username fits the requested pattern before trying to create the user	
		if (validateUsername(username)) {
			//TODO: storedproc to check username avail
			//see if we already have a tuple with this username in the db
			try {
				stmnt = cnxn.createStatement();
				String query = "SELECT id FROM Users WHERE name = '" + username + "'";
				rs = stmnt.executeQuery(query);
			} catch (SQLException e) {
				//problem checking if we already have this username
				return new Tuple(false, e.getMessage());
			}
			
			try {				
				//if resultset has at least one row, the username has already been created
				if (rs.next()) {
					return new Tuple(false, username + " is already taken. Please try another.");
				} else {
					//continue
				}
			} catch (SQLException e) {
				//problem with getting first result from resultset
				return new Tuple(false, e.getMessage());
			}
		} else {
			//username doesnt fit basic requirements
			return new Tuple(false, "The requested username does not meet the requirements. It must be at least five characters and may only contain alphanumeric characters or underscores");
		}
		
		
		//see if password meets requirements
		if (String.valueOf(chPassword).indexOf(" ") > -1) {
			return new Tuple(false, "Your password may not contain spaces.");
		} else if (chPassword.length < 8) {
			return new Tuple(false, "Your password must be at least 8 characters.");
		} else {
			//create hash of password/salt
			Password pass = Password.crtePassword(chPassword);
			
			//get hash/salt values to store
			String hash = pass.getHash();
			long salt = pass.getSalt();
			
			//TODO: write stored proc to insert user
			try {
				stmnt = cnxn.createStatement();
				
				//put the user in the db
				String query = "INSERT INTO Users(name, password, salt) Values ('"+ username + "', '" + pass.getHash() + "','" + salt + "')"; 
				stmnt.execute(query);
				
				return new Tuple(true, null);
				
			} catch (SQLException e) {
				return new Tuple(false, e.getMessage());
			}
		}
	}

	//do a quick validation that the selected username follows the required pattern
	protected static boolean validateUsername(String username) {
		return username != null && username.length() >= 5 && username.replaceAll("[^A-Za-z0-9_]", "").equals(username);
	}
	
	//helper method: convert array of chars to string
	protected static String convertCharsToString(char[] inptChars) {
		String rtnString = "";
		
		for (char c : inptChars) {
			rtnString += c;
		}
		
		return rtnString;
	}
	
}

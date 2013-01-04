package sec;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//utilities for follow acctions
public class FollowTools {
	
	//request to follow
	public static Tuple initializeFollow(int idFollower, int idFollowed, Connection cnxn) {
		//make sure we have aceptable inputs
		if (idFollower >= 1 && idFollowed >= 1 && cnxn != null) {
			Statement stmnt;
			ResultSet rs;
			String query = "";
			
			try {
				//first see if follower is already following or attempting to follow the proposed followed user
				stmnt = cnxn.createStatement();
				query = "SELECT confirmed FROM Follows WHERE user_id_follower = '" + idFollower + "' AND user_id_followed = '" + idFollowed + "'";
				rs = stmnt.executeQuery(query);
				
				//if the query returned a result, there is already a follow relation of follower --> followed
				if (rs.next()) {
					//see if it's been approved or not
					if (rs.getBoolean("confirmed")) {
						//already following
						return new Tuple(true, "You are already following this user");
					} else {
						//follow request already sent but not yet confirmed
						return new Tuple(true, "You have a pending request to follow this user");
					}
				} else {
					//add a follow request
					try {
						stmnt = cnxn.createStatement();
						query = "INSERT INTO Follows(user_id_follower, user_id_followed, confirmed) Values ('"+ idFollower + "', '" + idFollowed + "','0')"; 
						stmnt.execute(query);
						return new Tuple(true, "Follow request sent");
					} catch (SQLException ex){
						return new Tuple(false, ex.getMessage());
					}
				}
			} catch (SQLException ex) {
				
				return new Tuple(false, ex.getMessage());
			}
		} else {
			return new Tuple(false, "Invalid inputs");
		}
	}
	
	//approve a follow request
	public static Tuple approveFollow(int idFollower, int idFollowed, Connection cnxn) {
		//make sure we have aceptable inputs
		if (idFollower >= 1 && idFollowed >= 1 && cnxn != null) {
			Statement stmnt;
			ResultSet rs;
			String query = "";
			
			try {
				stmnt = cnxn.createStatement();
				
				//verify that a follow request has been sent
				query = "SELECT confirmed FROM Follows WHERE user_id_follower = '" + idFollower + "' AND user_id_followed = '" + idFollowed + "'";
				rs = stmnt.executeQuery(query);
				
				if (rs.next()) {
					if (rs.getBoolean("confirmed")) {
						//already following
						return new Tuple(false, "This user is already following you");
					} else {
						//follow request has been sent. approve it.
						query = "UPDATE Follows SET confirmed = true WHERE user_id_follower = '" + idFollower + "' AND user_id_followed = '" + idFollowed + "'";
						stmnt.execute(query);
						return new Tuple(true, "Approved");
					}
				} else {
					//follow relation hasn't been submitted
					return new Tuple(false, "User hasn't requested to follow you");
				}
			} catch (SQLException ex) {
				return new Tuple(false, ex.getMessage());
			}
		} else {
			return new Tuple(false, "Invalid inputs");
		}
	}
	
	//delete follow relation (deny request or revoke permission)
	public static Tuple deleteFollow(int idFollower, int idFollowed, Connection cnxn) {
		//make sure we have aceptable inputs
		if (idFollower >= 1 && idFollowed >= 1 && cnxn != null) {
			Statement stmnt;
			ResultSet rs;
			String query = "";
			
			try {
				stmnt = cnxn.createStatement();
				
				//verify that a follow request has been sent
				query = "SELECT confirmed FROM Follows WHERE user_id_follower = '" + idFollower + "' AND user_id_followed = '" + idFollowed + "'";
				rs = stmnt.executeQuery(query);
				
				//TODO: remove dup code
				if (rs.next()) {
					if (rs.getBoolean("confirmed")) {
						//already following
						query = "DELETE FROM Follows WHERE user_id_follower = '" + idFollower + "' AND user_id_followed = '" + idFollowed + "'";
						stmnt.execute(query);
						return new Tuple(true, "Follow deleted");
					} else {
						//follow request has been sent. approve it.
						query = "DELETE FROM Follows WHERE user_id_follower = '" + idFollower + "' AND user_id_followed = '" + idFollowed + "'";
						stmnt.execute(query);
						return new Tuple(true, "Denied");
					}
				} else {
					//follow relation hasn't been submitted
					return new Tuple(false, "Follow relation has not been established");
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				return new Tuple(false, ex.getMessage());
			}
		} else {
			return new Tuple(false, "Invalid inputs");
		}
	}
	
	//followers or requested followers of a user
	public static ResultSet getFollowers(int idFollowed, Connection cnxn, boolean confirmedVal) {
		//make sure we have aceptable inputs
		if (idFollowed >= 1 && cnxn != null) {
			Statement stmnt;
			ResultSet rs = null;
			String query = "";
			
			try {
				stmnt = cnxn.createStatement();
				
				query = "SELECT B.name FROM Follows A, Users B WHERE A.user_id_followed = '" + idFollowed + "' AND B.id = A.user_id_follower AND A.confirmed = " + confirmedVal + " ORDER BY B.name";
				rs = stmnt.executeQuery(query);
				int a = 1;
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			
			return rs;
		} else {
			return null;
		}
	}
	
	//get users followed by a user
	public static ResultSet getFollowing(int idFollower, Connection cnxn) {
		//make sure we have aceptable inputs
		if (idFollower >= 1 && cnxn != null) {
			Statement stmnt;
			ResultSet rs = null;
			String query = "";
			
			try {
				stmnt = cnxn.createStatement();
				
				//TODO: Get followed already
				query = "SELECT B.name FROM Follows A, Users B WHERE A.user_id_follower = '" + idFollower + "' AND A.user_id_followed =  B.id AND A.confirmed = true ORDER BY B.name";
				rs = stmnt.executeQuery(query);
			} catch (SQLException ex) {
				
			}
			
			return rs;
		} else {
			return  null;
		}
	}
}

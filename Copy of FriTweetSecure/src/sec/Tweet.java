package sec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Tweet {
	
	//TODO: use transactions
	public static Tuple addTweet(int userId, byte[] encTweetText, Connection cnxn) {
		Statement stmnt = null;
		String query = "";
		ResultSet rs = null;
		
		//insert tweet
		//TODO: implement stored procedure that returns id so I don't have to get the id in the next step
		try {
			PreparedStatement pstmt = cnxn.prepareStatement("INSERT INTO Tweets(text) Values (?)"); 
                   
			pstmt.setBytes(1, encTweetText);
			pstmt.execute();
		} catch (SQLException ex){
			return new Tuple(false, ex.getMessage());
		}
		
		//get tweet id to associate with user in Sends (see above comment about stored procedure for insert that returns id)
		try {
			PreparedStatement pstmt = cnxn.prepareStatement("SELECT id FROM Tweets WHERE text = ?");
			pstmt.setBytes(1, encTweetText);
			rs = pstmt.executeQuery();
			
			
			if (rs.next()) {
				int tweetId = rs.getInt("id");
				stmnt = cnxn.createStatement();
				query = "INSERT INTO Sends(user_id, tweet_id) Values("+ userId + ", " + tweetId + ")"; 
				stmnt.execute(query);
			}
		} catch (SQLException ex) {
			try {
				stmnt = cnxn.createStatement();
			
				//TODO: use transactions so as to avoid this hacky rollback
				query = "DELETE FROM Tweets WHERE text = '"+ encTweetText + "'"; 
				stmnt.execute(query);
			} catch (SQLException e) {
				return new Tuple(false, e.getMessage());
			}
			return new Tuple(false, ex.getMessage());
		}
		
		return new Tuple(true, "Tweet added");
			
	}
	
	//get a tweet from the database
	public static ResultSet getTweet(int id, Connection cnxn) {
		Statement stmnt = null;
		String query = "";
		ResultSet rs = null;
		
		try {
			stmnt = cnxn.createStatement();
			//TODO: write stored procedure
			query = "SELECT text FROM Tweets WHERE id = " + id;
			rs = stmnt.executeQuery(query);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return rs;
	}
	
	//get a user's feed results
	public static ResultSet getFeed(int userId, Connection cnxn) {
		Statement stmnt = null;
		String query = "";
		ResultSet rs = null;
		
		try {
			stmnt = cnxn.createStatement();
			
			//TODO: write stored procedure
			/*TODO: get user's tweets + follows'query = "SELECT * FROM ( " +
						"SELECT E.name, C.id, C.tweeted_at AS tweettime " +
						"FROM Users A, Follows B, Tweets C, Sends D, Users E " +
						"WHERE A.id = " + userId +
						" AND A.id = B.user_id_follower " + 
						"AND B.confirmed = true " +
						"AND B.user_id_followed = D.user_id " + 
						"AND C.id = D.tweet_id " +
						"AND E.id = D.user_id " +
						
						" UNION " +
						
						"SELECT A.name, D.id, D.tweeted_at AS tweettime "+
						"FROM Users A, Sends C, Tweets D " +
						"WHERE A.id = " + userId +
						" AND C.tweet_id = D.id) a " +
						
						"ORDER BY tweettime";*/
			query = 
					"SELECT E.name, C.id, C.tweeted_at " +
					"FROM Users A, Follows B, Tweets C, Sends D, Users E " +
					"WHERE A.id = " + userId +
					" AND A.id = B.user_id_follower " + 
					"AND B.confirmed = true " +
					"AND B.user_id_followed = D.user_id " + 
					"AND C.id = D.tweet_id " +
					"AND E.id = D.user_id " +
					"ORDER BY C.tweeted_at DESC";
						
			rs = stmnt.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rs;
	}
	
	//decrypt a tweet (TODO: needs to be modified to decrypt with private key when implementing Direct Messaging)
	public static String decrypt(String text, String pathToKeys) {
		String decryptedTxt = text;
		
		try {
			//get the public key
			PublicKey userPubKey = RSATools.loadRSAPublicKey(pathToKeys);
			
			//decrypt the tweet 
			decryptedTxt = RSATools.bytesToHex(RSATools.decrypt(text.getBytes(), userPubKey));
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decryptedTxt;
	}
	
	//TODO: put an encrypt() method here rather than calling RSATools from GUI

}

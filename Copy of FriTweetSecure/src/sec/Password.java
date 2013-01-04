package sec;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

//represents a password and contains static methods
public class Password {
	private long salt = 0;
	private String hash = "";
	
	//constructor
	public Password(long salt, String hash) {
		this.salt = salt;
		this.hash = hash;
	}
	
	//getter for salt
	public long getSalt() {
		return salt;
	}
	
	//getter for hashcode
	public String getHash() {
		return hash;
	}
	
	//transform a password to a hash
	public static Password crtePassword(char[] password) {
		//make sure password was passed in
		if (password != null) {
			//initialize variables for creating hash + salt out of entered password
			long lSalt = 0;
			double dSalt = 0.0;
			
			Random r = null;
			
			//get a salt value TODO: make work on first try rather than continuing to try creating one
			while (lSalt == 0) {
				r = new SecureRandom();
				dSalt = r.nextDouble();
				if (String.valueOf(dSalt).length() == 18) {
					lSalt = Long.parseLong(String.valueOf(dSalt).substring(2));
				}
			}
			
			//create instance of hash algorithm
			MessageDigest msgDig = null;	
			
			try {
				msgDig = MessageDigest.getInstance("SHA-512");
			} catch (NoSuchAlgorithmException e) {
				return null;
			}
			
			//compute hash code
			String hash = new HexBinaryAdapter().marshal(msgDig.digest((lSalt+ String.valueOf(password)).getBytes()));
			
			return new Password(lSalt, hash);
		} else {
			//no password passed in
			return null;
		}
	}
	
	//check that an entered password matches the computed hash
	public static boolean checkPassword(String checkHash, long checkSalt, char[] pword) {
		MessageDigest msgDig = null;
		
		try {
			msgDig = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			// 
			e.printStackTrace();
		}
		
		String computedHash = new HexBinaryAdapter().marshal(msgDig.digest((checkSalt + String.valueOf(pword)).getBytes()));
		
		return computedHash.equals(checkHash);
	}
	
	//set every char in a password to 0
	public static char[] zeroOut(char[] entered_pword) {
		for (int i=0; i < entered_pword.length; i++) {
			entered_pword[i] = 0;
		}
		return entered_pword;
	}	

}

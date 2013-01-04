package sec;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

//based on http://stackoverflow.com/questions/2839321/java-connectivity-with-mysql/2840358#2840358
public class DBConnector {
	
	//get a connection to the database
	protected static Connection getConnection() {
		Connection connection = null;
		
		try {
		    Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
		    throw new RuntimeException("Cannot find the driver in the classpath", e);
		}
		
		//database location
		String url = "jdbc:mysql://localhost:3306/sectweet";
		
		//get input from user
		//Scanner scn = new Scanner(System.in);
		//System.out.println("Please enter your username:");
		//String username = scn.nextLine();
		//System.out.println("Please enter your password:");
		//String password = scn.nextLine();
		
		String username = "tester";
		String password = "test";
		
		//close scanner
		//scn.close();
		
		//get a connection
		try {
		    connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
		    throw new RuntimeException("Cannot connect the database", e);
		} 
		
		return connection;
		
	}
}

package sec;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.event.MouseEvent;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.SwingConstants;
import javax.swing.JTabbedPane;
import javax.swing.JPasswordField;
import java.awt.Color;
import javax.swing.border.LineBorder;
import java.awt.event.KeyEvent;
import java.io.IOException;


public class GUI implements ActionListener, MouseListener, KeyListener {

	private static final String DFLT_SIGNIN_ERROR_TEXT = "Invalid userame or password";
	private static final GridLayout FEED_CONTENT_PAN_DFLT_LAYOUT = new GridLayout(0, 3, 0, 25);
	private static final int NUM_SIGNIN_ATTEMPTS_PERMITTED = 5;
	private JFrame strframe;
	private JTextField btextField;
	private JTextField fldUserName;
	private JPasswordField fldPassword;
	private Connection cnxn = null;
	private User currUser;
	private JTextField fldSearchForUser;
	private JLabel lblFollowMessage;
	private JLabel lblAccessMessage;
	private JPanel followUsersDetailPanel;
	private JLabel lblCharRemaining;
	private JTextArea txtAreaTwet;
	private JTextArea txtAreaKeyPath;
	private JPanel feedPane;
	private JPanel feedContentPanel;	
	private GridLayout followsGrid = new GridLayout(2, 1);
	private int nSigninAttempts = 0;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			private GUI window;

			public void run() {
				try {
					
					window = new GUI();
					window.strframe.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//get a database connection
		cnxn = DBConnector.getConnection();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//creaet the frame
		strframe = new JFrame();
		strframe.setTitle("SecTweet");
		strframe.setBounds(100, 100, 1200, 900);
		strframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		strframe.getContentPane().setLayout(new BorderLayout(0, 0));
		
		//create the row of tabs
		JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP);
		tabPane.setBorder(null);
		strframe.getContentPane().add(tabPane, BorderLayout.NORTH);
		
		//create the account pane: used for login/register, TODO: implement delete account 
		JPanel accountPane = new JPanel();
		accountPane.setBorder(null);
		tabPane.addTab("Account", null, accountPane, null);
		accountPane.setLayout(new BorderLayout(0, 0));
		accountPane.setIgnoreRepaint(true);
		
		//create the login area
		JPanel loginPanel = new JPanel();
		loginPanel.setPreferredSize(new Dimension(125, 155));
		loginPanel.setMinimumSize(new Dimension(125, 155));
		loginPanel.setIgnoreRepaint(true);
		accountPane.add(loginPanel);
		loginPanel.setLayout(new GridLayout(5, 3, 0, 0));
		
		//create the username label
		JLabel lblUserName = new JLabel("Username: ");
		lblUserName.setMinimumSize(new Dimension(75, 15));
		lblUserName.setIgnoreRepaint(true);
		lblUserName.setPreferredSize(new Dimension(75, 15));
		lblUserName.setMaximumSize(new Dimension(75, 15));
		lblUserName.setHorizontalAlignment(SwingConstants.TRAILING);
		loginPanel.add(lblUserName);
		
		//create the username text field
		fldUserName = new JTextField();
		fldUserName.setMinimumSize(new Dimension(75, 15));
		fldUserName.setIgnoreRepaint(true);
		fldUserName.setPreferredSize(new Dimension(75, 15));
		fldUserName.setMaximumSize(new Dimension(75, 15));
		loginPanel.add(fldUserName);
		fldUserName.setColumns(10);
		
		//fill spot in grid
		JLabel lblPlaceHolder1 = new JLabel("");
		lblPlaceHolder1.setMinimumSize(new Dimension(75, 15));
		lblPlaceHolder1.setIgnoreRepaint(true);
		lblPlaceHolder1.setPreferredSize(new Dimension(75, 15));
		lblPlaceHolder1.setMaximumSize(new Dimension(75, 15));
		loginPanel.add(lblPlaceHolder1);
		
		//create the password label
		JLabel lblPassword = new JLabel("Password: ");
		lblPassword.setMinimumSize(new Dimension(75, 15));
		lblPassword.setIgnoreRepaint(true);
		lblPassword.setPreferredSize(new Dimension(75, 15));
		lblPassword.setMaximumSize(new Dimension(75, 15));
		lblPassword.setHorizontalAlignment(SwingConstants.TRAILING);
		loginPanel.add(lblPassword);
		
		//create the password text field
		fldPassword = new JPasswordField();
		fldPassword.setMinimumSize(new Dimension(75, 15));
		fldPassword.setIgnoreRepaint(true);
		fldPassword.setPreferredSize(new Dimension(75, 15));
		fldPassword.setMaximumSize(new Dimension(75, 15));
		fldPassword.setColumns(10);
		loginPanel.add(fldPassword);
		
		//fill the grid
		JLabel lblPlaceHolder2 = new JLabel("");
		lblPlaceHolder2.setMinimumSize(new Dimension(75, 15));
		lblPlaceHolder2.setIgnoreRepaint(true);
		lblPlaceHolder2.setPreferredSize(new Dimension(75, 15));
		lblPlaceHolder2.setMaximumSize(new Dimension(75, 15));
		loginPanel.add(lblPlaceHolder2);
		
		//fill the grid
		JLabel lblPlaceHolder3 = new JLabel("");
		lblPlaceHolder3.setMinimumSize(new Dimension(75, 15));
		lblPlaceHolder3.setIgnoreRepaint(true);
		lblPlaceHolder3.setPreferredSize(new Dimension(75, 15));
		lblPlaceHolder3.setMaximumSize(new Dimension(75, 15));
		loginPanel.add(lblPlaceHolder3);
		
		//label to hold access success/failure message
		lblAccessMessage = new JLabel();
		lblAccessMessage.setMinimumSize(new Dimension(75, 15));
		lblAccessMessage.setIgnoreRepaint(true);
		lblAccessMessage.setPreferredSize(new Dimension(75, 15));
		lblAccessMessage.setMaximumSize(new Dimension(75, 15));
		lblAccessMessage.setEnabled(false);
		lblAccessMessage.setHorizontalAlignment(SwingConstants.CENTER);
		loginPanel.add(lblAccessMessage);
		
		//fill the grid
		JLabel lblPlaceHolder4 = new JLabel("");
		lblPlaceHolder4.setMinimumSize(new Dimension(75, 15));
		lblPlaceHolder4.setIgnoreRepaint(true);
		lblPlaceHolder4.setPreferredSize(new Dimension(75, 15));
		lblPlaceHolder4.setMaximumSize(new Dimension(75, 15));
		loginPanel.add(lblPlaceHolder4);
		
		//fill the grid
		JLabel lblPlaceHOlder5 = new JLabel("");
		lblPlaceHOlder5.setMinimumSize(new Dimension(75, 15));
		lblPlaceHOlder5.setIgnoreRepaint(true);
		lblPlaceHOlder5.setPreferredSize(new Dimension(75, 15));
		lblPlaceHOlder5.setMaximumSize(new Dimension(75, 15));
		loginPanel.add(lblPlaceHOlder5);
		
		//create the login button
		JButton btnLogin = new JButton("Login");
		btnLogin.setMinimumSize(new Dimension(75, 15));
		btnLogin.setIgnoreRepaint(true);
		btnLogin.setPreferredSize(new Dimension(75, 15));
		btnLogin.setMaximumSize(new Dimension(75, 15));
		loginPanel.add(btnLogin);
		btnLogin.addMouseListener(this);
		
		//fill the grid
		JLabel lblPlaceHolder6 = new JLabel("");
		lblPlaceHolder6.setMinimumSize(new Dimension(75, 15));
		lblPlaceHolder6.setIgnoreRepaint(true);
		lblPlaceHolder6.setPreferredSize(new Dimension(75, 15));
		lblPlaceHolder6.setMaximumSize(new Dimension(75, 15));
		loginPanel.add(lblPlaceHolder6);
		
		//create the register button
		JButton btnRegButton = new JButton("Register");
		btnRegButton.setMinimumSize(new Dimension(75, 15));
		btnRegButton.setIgnoreRepaint(true);
		btnRegButton.setPreferredSize(new Dimension(75, 15));
		btnRegButton.setMaximumSize(new Dimension(75, 15));
		btnRegButton.addMouseListener(this);
		
		//fill the grid
		JLabel lblPlaceHolder7 = new JLabel("");
		lblPlaceHolder7.setMinimumSize(new Dimension(75, 15));
		lblPlaceHolder7.setIgnoreRepaint(true);
		lblPlaceHolder7.setPreferredSize(new Dimension(75, 15));
		lblPlaceHolder7.setMaximumSize(new Dimension(75, 15));
		loginPanel.add(lblPlaceHolder7);
		loginPanel.add(btnRegButton);
		
		//fill the grid
		JLabel lblPlaceHolder8 = new JLabel("");
		lblPlaceHolder8.setMinimumSize(new Dimension(75, 15));
		lblPlaceHolder8.setIgnoreRepaint(true);
		lblPlaceHolder8.setPreferredSize(new Dimension(75, 15));
		lblPlaceHolder8.setMaximumSize(new Dimension(75, 15));
		loginPanel.add(lblPlaceHolder8);
		btnRegButton.addActionListener(this);
		
		//create the feed pane
		feedPane = new JPanel();
		feedPane.setBorder(null);
		tabPane.addTab("Feed", null, feedPane, null);
		feedPane.setLayout(new GridLayout(1, 0, 0, 0));
		
		//create the panel that will hold the populated feed
		feedContentPanel = new JPanel();
		feedPane.setAutoscrolls(true);
		feedPane.add(feedContentPanel);
		feedContentPanel.setLayout(FEED_CONTENT_PAN_DFLT_LAYOUT);
		
		//create the add tweet pane
		JPanel tweetPane = new JPanel();
		tweetPane.setBorder(null);
		tweetPane.setIgnoreRepaint(true);
		tabPane.addTab("Tweet", null, tweetPane, null);
		tweetPane.setLayout(new BorderLayout(5, 0));
		
		//create the panel that holds the compose label and char counter
		JPanel panCounter = new JPanel();
		tweetPane.add(panCounter, BorderLayout.WEST);
		panCounter.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		//create the label that tells you to compose a tweet
		JLabel lblComposeTweet = new JLabel("Compose Tweet");
		panCounter.add(lblComposeTweet);
		lblComposeTweet.setHorizontalAlignment(SwingConstants.TRAILING);
		lblComposeTweet.setVerticalAlignment(SwingConstants.BOTTOM);
		
		//create the label that tells you how many characters you have remaining
		lblCharRemaining = new JLabel("140");
		lblCharRemaining.setHorizontalAlignment(SwingConstants.TRAILING);
		panCounter.add(lblCharRemaining);
		lblCharRemaining.setVerticalAlignment(SwingConstants.TOP);
		
		//create the text entry panel (tweet + path)
		JPanel panEntry = new JPanel();
		tweetPane.add(panEntry);
		panEntry.setLayout(new GridLayout(1, 2, 5, 0));
		
		//create the tweet text entry area
		txtAreaTwet = new JTextArea();
		panEntry.add(txtAreaTwet);
		txtAreaTwet.setMaximumSize(new Dimension(2147483647, 100));
		txtAreaTwet.setAutoscrolls(false);
		txtAreaTwet.setTabSize(0);
		txtAreaTwet.setWrapStyleWord(true);
		txtAreaTwet.setLineWrap(true);
		txtAreaTwet.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		txtAreaTwet.setRows(4);
		txtAreaTwet.setColumns(35);
		
		//create the key path entry area
		txtAreaKeyPath = new JTextArea();
		panEntry.add(txtAreaKeyPath);
		txtAreaKeyPath.setMaximumSize(new Dimension(2147483647, 100));
		txtAreaKeyPath.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		txtAreaKeyPath.setLineWrap(true);
		txtAreaKeyPath.setRows(4);
		txtAreaKeyPath.setColumns(35);
		txtAreaTwet.addKeyListener(this);
		
		//create the label describing what to put in the key path area
		JTextArea lblEnterPrivateKey = new JTextArea("Enter path to your private key. \n\nExample: \n\nC:\\\\Users\\\\Sam\\\\Desktop\\\\keys");
		lblEnterPrivateKey.setOpaque(false);
		lblEnterPrivateKey.setEditable(false);
		lblEnterPrivateKey.setWrapStyleWord(true);
		tweetPane.add(lblEnterPrivateKey, BorderLayout.EAST);
		
		//create the add tweet button
		JButton btnAddTweet = new JButton("Add Tweet");
		btnAddTweet.addMouseListener(this);
		tweetPane.add(btnAddTweet, BorderLayout.NORTH);
		
		//create the follows pane		
		JPanel followPane = new JPanel();
		followPane.setBorder(null);
		tabPane.addTab("Follows", null, followPane, null);
		
		//create the panel that holds the populated content
		JPanel followUserPanel = new JPanel();
		followUserPanel.setPreferredSize(new Dimension(500, 50));
		followUserPanel.setLayout(new GridLayout(2, 3, 0, 0));
		
		//create the label instructing you to search for a user to follow
		JLabel lblSearchForUser = new JLabel("Search for user to follow");
		lblSearchForUser.setHorizontalAlignment(SwingConstants.TRAILING);
		lblSearchForUser.setMaximumSize(new Dimension(50, 25));
		lblSearchForUser.setPreferredSize(new Dimension(50, 25));
		lblSearchForUser.setMinimumSize(new Dimension(50, 25));
		followUserPanel.add(lblSearchForUser);
		
		//create the username entry field for search
		fldSearchForUser = new JTextField();
		fldSearchForUser.setMaximumSize(new Dimension(50, 25));
		fldSearchForUser.setPreferredSize(new Dimension(50, 25));
		fldSearchForUser.setMinimumSize(new Dimension(50, 25));
		followUserPanel.add(fldSearchForUser);
		fldSearchForUser.setColumns(50);
		
		//create the follow button
		JButton btnSearchFollow = new JButton("Follow");
		btnSearchFollow.setMaximumSize(new Dimension(50, 25));
		btnSearchFollow.setPreferredSize(new Dimension(50, 25));
		btnSearchFollow.setMinimumSize(new Dimension(50, 25));
		btnSearchFollow.addMouseListener(this);
		followPane.setLayout(new BorderLayout(0, 0));
		followUserPanel.add(btnSearchFollow);
		
		//fill the grid
		JLabel lblPlaceHolder9 = new JLabel("");
		lblPlaceHolder9.setMinimumSize(new Dimension(50, 25));
		lblPlaceHolder9.setMaximumSize(new Dimension(50, 25));
		lblPlaceHolder9.setPreferredSize(new Dimension(50, 25));
		followUserPanel.add(lblPlaceHolder9);
		
		//create the follow request success/failure message
		lblFollowMessage = new JLabel("");
		lblFollowMessage.setMinimumSize(new Dimension(50, 25));
		lblFollowMessage.setMaximumSize(new Dimension(50, 25));
		lblFollowMessage.setPreferredSize(new Dimension(50, 25));
		lblFollowMessage.setHorizontalAlignment(SwingConstants.CENTER);
		followUserPanel.add(lblFollowMessage);
		
		//fill the grid
		JLabel lblPlaceHolder10 = new JLabel("");
		lblPlaceHolder10.setMinimumSize(new Dimension(50, 25));
		lblPlaceHolder10.setMaximumSize(new Dimension(50, 25));
		lblPlaceHolder10.setPreferredSize(new Dimension(50, 25));
		followUserPanel.add(lblPlaceHolder10);
		followPane.add(followUserPanel, BorderLayout.NORTH);
		
		//create the pane that will hold detail about users followed, following, and requesting to follow
		followUsersDetailPanel = new JPanel();
		followPane.add(followUsersDetailPanel, BorderLayout.SOUTH);
		//panel_1.setLayout(new BorderLayout());
		
		
	}
	
	//populate the followers
	private void getFollowers() {
		JPanel followersPan = new JPanel(followsGrid);
		
		
		JLabel lblNewLabel = new JLabel("Followers");
		followersPan.add(lblNewLabel);
		
		//get followers
		ResultSet rs = FollowTools.getFollowers(currUser.getId(), cnxn, true);
		followersPan.add(FollowerList.createAndShowFollowerList(currUser, rs, cnxn, true, this));		
		
		followUsersDetailPanel.add(followersPan);
	}
	
	//populate the requested followers
	private void getFollowRequesters() {
		JPanel followRequestersPan = new JPanel(followsGrid);
		
		JLabel lblNewLabel = new JLabel("Follow Requests");
		followRequestersPan.add(lblNewLabel);
		
		//get follow requesters
		ResultSet rs = FollowTools.getFollowers(currUser.getId(), cnxn, false);
		followRequestersPan.add(FollowerList.createAndShowFollowerList(currUser, rs, cnxn, false, this));		
		
		followUsersDetailPanel.add(followRequestersPan);
	}
	
	//populate the following
	private void getFollowing() {
		JPanel followingPan = new JPanel(followsGrid);
		
		JLabel lblNewLabel = new JLabel("Following");
		followingPan.add(lblNewLabel);
		
		//get following
		ResultSet rs = FollowTools.getFollowing(currUser.getId(), cnxn);
		followingPan.add(FollowingList.createAndShowFollowingList(currUser, rs, cnxn));		
		
		followUsersDetailPanel.add(followingPan);
	}
	
	//get the feed
	private void getFeed() {
		try {
			ResultSet rs = Tweet.getFeed(currUser.getId(), cnxn);
			JPanel tweetContainer;
			JTextArea tweetText;
			JTextArea tweetInfo;
			GridLayout feedContentPanelLayout = (GridLayout)feedContentPanel.getLayout();

			while (rs.next()) {
				feedContentPanelLayout.setRows(feedContentPanelLayout.getRows() + 1);
				feedContentPanel.setLayout(feedContentPanelLayout);
				tweetContainer = new JPanel(new BorderLayout(5, 5));
				tweetContainer.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
				tweetInfo = new JTextArea(rs.getString("name") + "\n\n " + rs.getString("tweeted_at") + " ");
				tweetInfo.setOpaque(false);
				tweetInfo.setEditable(false);
				tweetInfo.setWrapStyleWord(true);
				tweetContainer.add(tweetInfo, BorderLayout.WEST);
				
				tweetText = new JTextArea(String.valueOf(rs.getInt("id")));
				tweetText.setWrapStyleWord(true);
				tweetText.setLineWrap(true);
				tweetText.setOpaque(false);
				
				tweetContainer.add(tweetText, BorderLayout.CENTER);
				MyJButton decrypt = new MyJButton("decrypt", tweetText);
				decrypt.addMouseListener(new MyMouseListener(decrypt.getRefersTo()));
				tweetContainer.add(decrypt, BorderLayout.EAST);
				feedContentPanel.add(tweetContainer);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(strframe, e.getMessage());
		}
	}
	
	//refresh the feed
	private void refreshFeed() {
		feedContentPanel.removeAll();
		feedContentPanel.setLayout(FEED_CONTENT_PAN_DFLT_LAYOUT);
		getFeed();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		String compntName = "";
		Tuple t = null;
		
		if (e.getSource().getClass().getName().indexOf("JButton") > -1) {
			compntName = ((JButton) e.getSource()).getLabel();
			char[] entered_pword;
			
			String username = "";
			
			//see what JButton was clicked on
			if (compntName.equalsIgnoreCase("register")) {
				//user clicked register
				//register();
				entered_pword = fldPassword.getPassword();
				fldPassword.setText("");
				username = fldUserName.getText();
				t = User.createAccount(username, entered_pword, cnxn);
				if (((Boolean) t.getTup1()) == false) {
					JOptionPane.showMessageDialog(strframe, (String) t.getTup2());
					
					//zero out the password after use
					entered_pword = Password.zeroOut(entered_pword);
				} else {
					int rtnId = User.signIn(username, entered_pword, cnxn);
					
					//zero out the password after use
					entered_pword = Password.zeroOut(entered_pword);
					currUser = new User(username, rtnId);
					//JOptionPane.showMessageDialog(strframe, "Registered + Signed in");
					String pathToKeys = JOptionPane.showInputDialog(strframe, "Registered + Signed in. Please enter a directory to save your application-specific keys. Example format: C:\\Users\\Sam\\Desktop\\keys");
					try {
						RSATools.generateRSAKeyPair(pathToKeys);
						JOptionPane.showMessageDialog(strframe, "public.key and private.key successfully created at " + pathToKeys + 
														" You may share your public key with your followers. Please keep your private key secure and use " +
														"it to encrypt your tweets and decrypt direct messages from other users.");
						
					} catch (GeneralSecurityException e1) {
						JOptionPane.showMessageDialog(strframe, e1.getMessage());
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(strframe, e1.getMessage());
					}
					strframe.setTitle("SecTweet - " + username);
					
					//get users following this user 
					getFollowers();
					
					//get requested followers
					getFollowRequesters();				
					
					//get users this user is following
					getFollowing();
				}
			} else if (compntName.equalsIgnoreCase("Login")) {
				
				if (nSigninAttempts > NUM_SIGNIN_ATTEMPTS_PERMITTED) {
					JOptionPane.showMessageDialog(strframe, "Too many failed signin attempts. Please try again later");
					System.exit(0);
				}
				entered_pword = fldPassword.getPassword();
				//passwordField.setText("");
				username = fldUserName.getText();
				int rtnId = User.signIn(username, entered_pword, cnxn);
				
				//zero out the password array after use
				entered_pword = Password.zeroOut(entered_pword);
				
				fldPassword.setText("");
				
				if (rtnId > -1) {
					//signIn();
					currUser = new User(fldUserName.getText(), rtnId);
				
					//make sure the fail label is hidden if login was successful
					//accessMessageLbl.setText("Signed in");
					//accessMessageLbl.setVisible(true);
					JOptionPane.showMessageDialog(strframe, "Login successful");
					
					strframe.setTitle("SecTweet - " + username);
					
					//get users following this user 
					getFollowers();
					
					//get requested followers
					getFollowRequesters();				
					
					//get users this user is following
					getFollowing();
					
					//get this user's feed
					getFeed();
				} else {
					nSigninAttempts++;
					JOptionPane.showMessageDialog(strframe, DFLT_SIGNIN_ERROR_TEXT);
				}
				
			} else if (compntName.equalsIgnoreCase("Follow")) {
				if (currUser == null) {
					lblFollowMessage.setText("You must be signed in to follow users");
				} else {
					int rtnId = User.findUser(fldSearchForUser.getText(), cnxn);
					
					if (rtnId > -1) {
						Tuple rsltTuple = FollowTools.initializeFollow(currUser.getId(), rtnId, cnxn);
						lblFollowMessage.setText((String)rsltTuple.getTup2());
					} else {
						lblFollowMessage.setText("User not found");
					}
				}
				
				lblFollowMessage.setVisible(true);
			} else if (compntName.equalsIgnoreCase("Add Tweet")) {
				String tweetText = txtAreaTwet.getText();
				String privKeyText = txtAreaKeyPath.getText();
				
				//make sure we have our inputs 
				if (tweetText.length() > 140) {
					JOptionPane.showMessageDialog(strframe, "You may only tweet up to 140 characters");
				} else if (privKeyText.length() == 0){
					JOptionPane.showMessageDialog(strframe, "Please enter the path to your private key");
				} else {
					//add the tweet
					Key privKey;
					Tuple rslt = null;
					
					try {
						privKey = RSATools.loadRSAPrivateKey(txtAreaKeyPath.getText());
						byte[] encTweetText = RSATools.encrypt(RSATools.hexToBytes(tweetText), privKey);
						txtAreaTwet.setText(RSATools.bytesToHex(encTweetText));
						rslt = Tweet.addTweet(currUser.getId(), encTweetText, cnxn);
						refreshFeed();
					} catch (NoSuchAlgorithmException ex) {
						JOptionPane.showMessageDialog(strframe, ex.getMessage());
					} catch (InvalidKeySpecException ex) {
						JOptionPane.showMessageDialog(strframe, ex.getMessage());
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(strframe, ex.getMessage());
					} catch (GeneralSecurityException ex) {
						JOptionPane.showMessageDialog(strframe, ex.getMessage());
					}
				}
				
				
			}
		}
		
	}

	private void register() {
		//make sure all the necessary objects have been instantiated
		if (fldPassword != null && fldUserName != null && strframe != null && currUser != null && strframe != null && cnxn != null) {
			
			String username = fldUserName.getText();
			
			char[] entered_pword = fldPassword.getPassword();
			
			
			Tuple t = User.createAccount(username, entered_pword, cnxn);
			
			//if login failed, show the message
			try {
				if (((Boolean) t.getTup1()) == false) {
					JOptionPane.showMessageDialog(strframe, (String) t.getTup2());
				} else {
					signIn();
					/*
					entered_pword = passwordField.getPassword();
					String username = fldUserName.getText();
					int rtnId = User.signIn(username, entered_pword, cnxn);
					
					currUser = new User(username, rtnId);
					JOptionPane.showMessageDialog(strframe, "Registered + Signed in");
					strframe.setTitle("SecTweet - " + username);
					passwordField.setText("");
					//get info for Follows tab
					populateFollows();*/
				}
			} catch (ClassCastException ex) {
				JOptionPane.showMessageDialog(strframe, "Account created but there was a problem signing in");
				
				//problem signing in, kill the current user
				currUser = null;
			}
		}
		
	}

	private void signIn() {
		
		//check that all the required fields have been instantiated
		if (fldUserName != null && fldPassword != null && cnxn != null && strframe != null) {
			//get the entered username
			String username = fldUserName.getText();
			
			//check that the user entered a valid username in terms of length and characters
			if (!User.validateUsername(username)) {
				JOptionPane.showMessageDialog(strframe, "Login failed. Userame bust be at least 5 characters and contain only alphanumeric characters or underscores");
				return;
			}
			
			//get the entered password
			char[] entered_pword = fldPassword.getPassword();
			
			//sign in the requested user
			int rtnId = User.signIn(username, entered_pword, cnxn);
			
			//clear out the password field
			fldPassword.setText("");
			
			if (rtnId > -1) {
				//update current user to signed in user
				currUser = new User(fldUserName.getText(), rtnId);
				
				//let the user know that the login was successful
				JOptionPane.showMessageDialog(strframe, "Login successful");
				
				//update frame to reflect signed in user's name
				strframe.setTitle("SecTweet - " + username);
				
				//populate the Follows tab
				populateFollows();
				
			} else {
				//signin error: alert user, kill off the current user field
				JOptionPane.showMessageDialog(strframe, DFLT_SIGNIN_ERROR_TEXT);
				currUser = null;
				
			}
		} else {
			//signin error: alert user, kill off the current user field
			JOptionPane.showMessageDialog(strframe, DFLT_SIGNIN_ERROR_TEXT);
			currUser = null;
		}
		
	}
	
	//clear the data-driven areas of the Follows tab
	protected void clearFollows() {
		followUsersDetailPanel.removeAll();
	}
	
	//populate all the data-driven areas of the Follows tab
	protected void populateFollows() {
		//get users following this user 
		getFollowers();
		
		//get requested followers
		getFollowRequesters();				
		
		//get users this user is following
		getFollowing();
		
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) {
		//make sure the tweet entry textarea and # of characters remaining labels have been instantiated
		if (txtAreaTwet != null && lblCharRemaining != null) {
			//update characters remaining counter
			int currLength = txtAreaTwet.getText().length();		
			lblCharRemaining.setText(String.valueOf(140 - currLength));	
			
			//if the tweet is more than 140 characters, set the text to red
			//else set it back to black
			if (currLength > 140) {
				lblCharRemaining.setForeground(Color.RED);
			} else {
				lblCharRemaining.setForeground(Color.BLACK);
			}
		}
	}
	

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		//stub required for interface implementation
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		//stub required for interface implementation
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		//stub required for interface implementation
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		//stub required for interface implementation
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		//stub required for interface implementation
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		//stub required for interface implementation
		
	} 
	
	private class MyJButton extends JButton {
		JTextArea refersTo;
		
		public MyJButton(String text, JTextArea refersTo) {
			super(text);
			this.refersTo = refersTo;
		}
		
		public JTextArea getRefersTo() {
			return refersTo;
		}
		
	}
	
	private class MyMouseListener implements MouseListener {
		private JTextArea tweetTxtArea;
		
		public MyMouseListener(JTextArea tweetTxtArea) {
			super();
			this.tweetTxtArea = tweetTxtArea;
		}
		
		public JTextArea getTweetTxtArea() {
			return this.tweetTxtArea;
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			String pathToKeys = JOptionPane.showInputDialog(strframe, "Please enter the directory containing this user's public key. Example format: C:\\Users\\Sam\\Desktop\\keys");
			//tweetTextArea.setText(Tweet.decrypt(tweetTxtArea.getText(), pathToKeys));
			PrivateKey privKey;
	 		PublicKey publicKey;
			try {
				privKey = RSATools.loadRSAPrivateKey(pathToKeys);
				publicKey = RSATools.loadRSAPublicKey(pathToKeys);
			    //byte[] plaintext = RSATools.hexToBytes("Go ravens");
			    //byte[] ciphertext = RSATools.encrypt(plaintext, privKey);
				int id = Integer.valueOf(getTweetTxtArea().getText());
				ResultSet rs = Tweet.getTweet(id, cnxn);
			    byte[] ciphertext;
			    byte[] recovered = null;
			    
				try {
					rs.next();
					ciphertext = rs.getBytes("text");
				    recovered = RSATools.decrypt(ciphertext, publicKey);
				    getTweetTxtArea().setText(new String(recovered));
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(strframe, e.getMessage());
				}			    		
			    
			    String output = new String(recovered, "UTF-8");
			    
			    //disable button
			    MyJButton clickedButton = ((MyJButton)arg0.getSource());
			    clickedButton.setEnabled(false);
			    clickedButton.removeMouseListener(this);
			} catch (NoSuchAlgorithmException e) {
				JOptionPane.showMessageDialog(strframe, e.getMessage());
			} catch (InvalidKeySpecException e) {
				JOptionPane.showMessageDialog(strframe, e.getMessage());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(strframe, e.getMessage());
			} catch (GeneralSecurityException e) {
				JOptionPane.showMessageDialog(strframe, e.getMessage());
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			
		}
		
	}
}

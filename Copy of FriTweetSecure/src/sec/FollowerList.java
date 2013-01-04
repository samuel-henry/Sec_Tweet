//permitted modification of demo class from 
//http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/uiswing/examples/components/ListDemoProject/src/components/ListDemo.java

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
package sec;
 
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.event.*;
 
//creates a list of followers (either requested or approved based on boolean confirmed value passed in
public class FollowerList extends JPanel implements ListSelectionListener {
    private static final String APPROVE_STRING = "Approve";
    private static final String DENY_STRING = "Deny";
    private static final String FOLLOW_BACK_STRING = "Follow";
    private static final String REVOKE_STRING = "Revoke";
    private static final String FOLLOWING_STRING = "Following";
	
	private JList list;
    private DefaultListModel listModel;
    private Connection cnxn;
    private User currUser;
    private boolean confirmedVal;
    private JButton approveButton;
	private JButton denyButton;
	private JButton followBackButton;
	private int rsltCnt;
	private GUI currGUI;
	
	//constructor
    public FollowerList(User currUser, ResultSet rs, Connection cnxn, boolean confirmedVal, GUI currGUI) {
        super(new BorderLayout());
        this.currGUI = currGUI;
        this.currUser = currUser;
        this.cnxn = cnxn;
        this.confirmedVal = confirmedVal;
        this.listModel = new DefaultListModel();
        
        //keep track of number of results returned
        int rslCnt = 0;
        
        //get follower names
        try {
        	//add followers' names to list model
			while (rs.next()) {
				listModel.addElement(rs.getString("name"));
				rslCnt++;
			}
		} catch (SQLException e) {
			// TODO suppress stack trace
			e.printStackTrace();
		}
         
        //Create the list
        list = new JList(listModel);
        
        //only allow one follower to be selected at a time
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        //setup listener for when a list item is selected
        list.addListSelectionListener(this);
        
        //show five rows of followers, scroll through rest
        list.setVisibleRowCount(5);
        
        //create pane for list
        JScrollPane listScrollPane = new JScrollPane(list);
        
        //if at least one follower was returned
        if (rslCnt > 0) {
	        //add buttons based on whether we're getting followers or requested followers
	        if (confirmedVal) {
	        	//create button to revoke follow permission from follower
		        denyButton = new JButton(REVOKE_STRING);
		        denyButton.setActionCommand(REVOKE_STRING);
		        denyButton.addActionListener(new DenyListener());
	        } else {
	        	//add approve + deny follow request buttons
	        	denyButton = new JButton(DENY_STRING);
		        denyButton.setActionCommand(DENY_STRING);
		        denyButton.addActionListener(new DenyListener());
		        approveButton = new JButton(APPROVE_STRING);
		        approveButton.setActionCommand(APPROVE_STRING);
		        approveButton.addActionListener(new ApproveListener());
		        
	        }
	        //TODO: implement followback
	        /*
	        followBackButton = new JButton(FOLLOW_BACK_STRING);
	        followBackButton.setActionCommand(FOLLOW_BACK_STRING);
	        followBackButton.addActionListener(new FollowBackListener());
	        */
        }
       
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        
        buttonPane.add(Box.createHorizontalStrut(5));
        
        //set border
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        //add button(s) if at least one result was returned
        if (rslCnt > 0) {
        	//if we're dealing with follow requests, add the approve button
	        if (!confirmedVal) {
	        	buttonPane.add(approveButton);
	    	}
	        
	        //add the revoke/deny button
	        buttonPane.add(denyButton);
	        
	        //TODO: followback not yet implemented
	        //buttonPane.add(followBackButton);
        }
 
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }
    
    //create the gui object (TODO: rename createFollowerList since GUI displays it)
    protected static FollowerList createAndShowFollowerList(User currUser, ResultSet rs, Connection cnxn, boolean confirmedVal, GUI currGUI) { 
        //Create and set up the content pane.
        FollowerList newContentPane = new FollowerList(currUser, rs, cnxn, confirmedVal, currGUI);
        newContentPane.setOpaque(true); //content panes must be opaque
        return newContentPane;
    }
    
    //handle clicking on deny button
    class DenyListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	//get index of selected name
            int index = list.getSelectedIndex();
            
            //if something was selected
            if (index > -1) {
            	//get the selected username
	            String name = (String)list.getSelectedValue().toString();
	            
	            try {
		            //delete the user
		            Tuple t = FollowTools.deleteFollow(User.findUser(name, cnxn), currUser.getId(), cnxn);
		            
		            if ((Boolean)t.getTup1()) {
			            listModel.remove(index);
			 
			            int size = listModel.getSize();
			 
			            if (size == 0) { //Nobody's left, disable approving.
			            	if (!confirmedVal) {
			            		approveButton.setEnabled(false);
			            	}
			            	
			            	denyButton.setEnabled(false);
			                followBackButton.setEnabled(false);
			 
			            } else { //Select an index.
			                if (index == listModel.getSize()) {
			                    //removed item in last position
			                    index--;
			                }
			 
			                list.setSelectedIndex(index);
			                list.ensureIndexIsVisible(index);
			            }
		            } else {
		            	//TODO: alert user of problem
		            }
	            } catch (ClassCastException ex) {
	            	//TODO: handle problem getting boolean out of tup1
	            } 
            }
        }
    }
    
    //handle clicking approve follower button
    class ApproveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	//get the selected username
            int index = list.getSelectedIndex();
            
            //make sure a username was selected
            if (index > -1) {
            	//get the username
	            String name = (String)list.getSelectedValue().toString();
	            
	            //approve the follower
	            Tuple t = FollowTools.approveFollow(User.findUser(name, cnxn), currUser.getId(), cnxn);
	            
	            currGUI.clearFollows();
	            currGUI.populateFollows();
	            
	            try {
		            if ((Boolean)t.getTup1()) {
			            listModel.remove(index);
			 
			            int size = listModel.getSize();
			 
			            if (size == 0) { //Nobody's left, disable approving.
			                approveButton.setEnabled(false);
			                denyButton.setEnabled(false);
			                //followBackButton.setEnabled(false);
			 
			            } else { //Select an index.
			                if (index == listModel.getSize()) {
			                    //removed item in last position
			                    index--;
			                }
			 
			                list.setSelectedIndex(index);
			                list.ensureIndexIsVisible(index);
			            }
		            } else {
		            	//TODO: alert user of problem
		            }
	            } catch (ClassCastException ex) {
	            	//TODO: handle problem casting tup1 to boolean
	            }
            }
        }
    }
    
    /*TODO: followback not yet implemented
    class FollowBackListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.
            int index = list.getSelectedIndex(); 
            if (index > -1) {
	            String name = (String)list.getSelectedValue().toString();
	            int id = User.findUser(name, cnxn);
	            
	            Tuple rsltTup = FollowTools.initializeFollow(currUser.getId(), id, cnxn);
	            
	            if ((Boolean)rsltTup.getTup1() == true) {
	            	followBackButton.setEnabled(false);
	            } else {
	            	//TODO: alert user that failure
	            }
            }
        }
    }*/
 
    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
        	int selectedIdx = list.getSelectedIndex();
        	
            if (selectedIdx == -1) {
               // denyButton.setEnabled(false);
 
            } else {
            	 if (rsltCnt > 0) {
	    	        //Selection, enable the fire button.
	                denyButton.setEnabled(true);
	                
	                if (!confirmedVal) {
	                	approveButton.setEnabled(true);
	                }
                }
	        }
	        
            //TODO: followback not yet implemented
            //followBackButton.setEnabled(true);
        }
    }

 
}

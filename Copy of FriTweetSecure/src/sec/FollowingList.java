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
 
/* ListDemo.java requires no other files. */
public class FollowingList extends JPanel implements ListSelectionListener {
    private static final String UNFOLLOW_STRING = "Unfollow";
	
	private JList list;
    private DefaultListModel listModel;
    private Connection cnxn;
    private User currUser;
	private JButton unfollowButton;
	private int rsltCnt;
 
	//constructor
    protected FollowingList(User currUser, ResultSet rs, Connection cnxn) {
        super(new BorderLayout());
        this.currUser = currUser;
        this.cnxn = cnxn;
 
        listModel = new DefaultListModel();
        
        //get usernames followed out of result set
        try {
			while (rs.next()) {
				listModel.addElement(rs.getString("name"));
				rsltCnt++;
			}
		} catch (SQLException e) {
			// TODO fail gracefully
		}
         
        //Create the list and put it in a scroll pane.
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        //handle selecting item on list
        list.addListSelectionListener(this);
        
        //show first five followed, scroll for rest
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);        
 
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));

        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        //add button if following at least one user
        if (rsltCnt > 0) {
        	//create unfollow button
        	unfollowButton = new JButton(UNFOLLOW_STRING);
	        unfollowButton.setActionCommand(UNFOLLOW_STRING);
	        unfollowButton.addActionListener(new UnfollowListener());
        	buttonPane.add(unfollowButton);
        }
        
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }
    
    //handle clicking on unfollow
    class UnfollowListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //get index of selected name
        	int index = list.getSelectedIndex();
            
        	//if a name is selected, unfollow
            if (index > -1) {
            	//get username to unfollow
	            String name = (String)list.getSelectedValue().toString();
	            
	            //unfollow in db
	            Tuple t = FollowTools.deleteFollow(currUser.getId(), User.findUser(name, cnxn), cnxn);
	            
	            try {
		            if ((Boolean)t.getTup1()) {
			            listModel.remove(index);
			 
			            int size = listModel.getSize();
			 
			            if (size == 0) { //Nobody's left, disable approving.
			                unfollowButton.setEnabled(false);
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
    
    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
        	int selectedIdx = list.getSelectedIndex();
        	
            if (selectedIdx == -1) {
            	//No selection, disable fire button.
                unfollowButton.setEnabled(false);
 
            } else {
            	if (rsltCnt > 0) {
	    	        //Selection, enable the fire button.
	                unfollowButton.setEnabled(true);
            	}
	        }
        }
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static FollowingList createAndShowFollowingList(User currUser, ResultSet rs, Connection cnxn) { 
    	if (currUser != null && rs != null && cnxn != null) {
	        //Create and set up the content pane.
	        FollowingList newContentPane = new FollowingList(currUser, rs, cnxn);
	        newContentPane.setOpaque(true); //content panes must be opaque
	        return newContentPane;
    	} else {
    		return null;
    	}
    }
 
}

/*
 * TouchGraph LLC. Apache-Style Software License
 *
 *
 * Copyright (c) 2002 Alexander Shapiro. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by 
 *        TouchGraph LLC (http://www.touchgraph.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "TouchGraph" or "TouchGraph LLC" must not be used to endorse 
 *    or promote products derived from this software without prior written 
 *    permission.  For written permission, please contact 
 *    alex@touchgraph.com
 *
 * 5. Products derived from this software may not be called "TouchGraph",
 *    nor may "TouchGraph" appear in their name, without prior written
 *    permission of alex@touchgraph.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL TOUCHGRAPH OR ITS CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 *
 */

/**  LBNavigateUI. User interface for moving around the graph, as opposed
  *  to editing.
  *   
  *  @author   Alexander Shapiro                                        
  *  @version  1.20
  */

package com.touchgraph.linkbrowser;

import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGPanel;
import com.touchgraph.graphlayout.interaction.DragNodeUI;
import com.touchgraph.graphlayout.interaction.LocalityScroll;
import com.touchgraph.graphlayout.interaction.TGAbstractClickUI;
import com.touchgraph.graphlayout.interaction.TGAbstractDragUI;
import com.touchgraph.graphlayout.interaction.TGUserInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;


public class LBNavigateUI extends TGUserInterface {

	TGPanel tgPanel;	
	TGLinkBrowser tgLinkBrowser;
	LBNavigateMouseListener ml;
		
	TGAbstractDragUI hvDragUI;
	
	TGAbstractClickUI hvScrollToCenterUI;
	LBNodeHintUI lbNodeHintUI;
	DragNodeUI dragNodeUI;
	TGAbstractDragUI rotateDragUI;
	LocalityScroll localityScroll;
	
	JPopupMenu nodePopup;	
	JPopupMenu edgePopup;	
	LBNode popupNode;
	LBEdge popupEdge;

	public LBNavigateUI(TGLinkBrowser tglb) {
		tgLinkBrowser = tglb;
		tgPanel = tgLinkBrowser.getTGPanel();		
		
		localityScroll=tgLinkBrowser.localityScroll;
		
		hvDragUI = tgLinkBrowser.hvScroll.getHVDragUI();
		rotateDragUI = tgLinkBrowser.rotateScroll.getRotateDragUI();
		
		hvScrollToCenterUI = tgLinkBrowser.hvScroll.getHVScrollToCenterUI();
		
		dragNodeUI = new DragNodeUI(tgPanel);					
		
		lbNodeHintUI = new LBNodeHintUI(tglb);
		
		ml = new LBNavigateMouseListener();
		
		setUpNodePopup();
		setUpEdgePopup();
	}
	
	public void activate() {		
		tgPanel.addMouseListener(ml);
		lbNodeHintUI.activate();
	}
	
	public void deactivate() {
		tgPanel.removeMouseListener(ml);
		lbNodeHintUI.deactivate();
	}
	
	class LBNavigateMouseListener extends MouseAdapter {
	
		public void mousePressed(MouseEvent e) {
			LBNode mouseOverN = (LBNode) tgPanel.getMouseOverN();
			
			if (e.getModifiers() == MouseEvent.BUTTON1_MASK) { 
				if (mouseOverN == null) 
					hvDragUI.activate(e);
				else 
					dragNodeUI.activate(e);					
			}	
		}

		public void mouseClicked(MouseEvent e) {
			LBNode mouseOverN = (LBNode) tgPanel.getMouseOverN();
			LBNode select = (LBNode) tgPanel.getSelect();
			if ((e.getModifiers() & MouseEvent.BUTTON1_MASK)!=0) { 
				if (mouseOverN != null) {
					if (mouseOverN!=select) {						
						tgPanel.setSelect(mouseOverN);
            // ToDo: investigate this original was localityScroll.getRadius()
            // ToDo: replaced with localityScroll.getLocalityRadius()
						// tgPanel.setLocale(mouseOverN, localityScroll.getRadius());
            try
            {
              tgPanel.setLocale(mouseOverN, localityScroll.getLocalityRadius());
            }
            catch (com.touchgraph.graphlayout.TGException ignore)
            {
                // ToDo: was forced to add checked exception verify the proper intervention
            }
            //hvScrollToCenterUI.activate(e);
					}
					else {
				 		tgLinkBrowser.processNodeUrl(mouseOverN);
				 	}
				}
			}	
		}	
		
		public void mouseReleased(MouseEvent e) {
       		if (e.isPopupTrigger()) {
       			popupNode = (LBNode) tgPanel.getMouseOverN();
       			popupEdge = (LBEdge) tgPanel.getMouseOverE();
       			if (popupNode!=null) {
       				tgPanel.setMaintainMouseOver(true);
                    lbNodeHintUI.deactivate();       				
    	    		nodePopup.show(e.getComponent(), e.getX(), e.getY());
            	}
            	else if (popupEdge!=null) {
            		tgPanel.setMaintainMouseOver(true);
                    lbNodeHintUI.deactivate();
            		edgePopup.show(e.getComponent(), e.getX(), e.getY());
            	}
            	else {
            		tgLinkBrowser.lbPopup.show(e.getComponent(), e.getX(), e.getY());
            	}
           	}
        }	
	}
	
	private void setUpNodePopup() {	    
	    nodePopup = new JPopupMenu();
	    JMenuItem menuItem;
		
		menuItem = new JMenuItem("Expand Node");
        ActionListener expandAction = new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
					if(popupNode!=null) {
						tgPanel.expandNode(popupNode);
					}
    			}
			};
			
        menuItem.addActionListener(expandAction);
     	nodePopup.add(menuItem);

      // ToDo: determine if the tgPanel.hideNode method ought to be set on the
      // ToDo: selectedNode: select or on the popupNode, as the original code won't compile
     	menuItem = new JMenuItem("Hide Node");
        ActionListener hideAction = new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
					Node select = tgPanel.getSelect();
					if(popupNode!=null) {
						// tgPanel.hideNode(popupNode, select);
            tgPanel.hideNode(popupNode);
					}
    			}
			};
			
        menuItem.addActionListener(hideAction);
     	nodePopup.add(menuItem);
   	
     	menuItem = new JMenuItem("Select Node");
        ActionListener selectAction = new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
					if(popupNode!=null) {
						tgPanel.setSelect(popupNode);
					}
    			}
			};
        menuItem.addActionListener(selectAction);
     	nodePopup.add(menuItem);

     	nodePopup.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				tgPanel.setMaintainMouseOver(false);                
				tgPanel.setMouseOverN(null);
				tgPanel.repaint();		
                lbNodeHintUI.activate();
			}
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
		});
		
	}

	private void setUpEdgePopup() {	    
	    edgePopup = new JPopupMenu();
	    JMenuItem menuItem;
		     	
     	menuItem = new JMenuItem("Hide Edge");
        ActionListener hideAction = new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
					if(popupEdge!=null) {
						tgPanel.hideEdge(popupEdge);
					}
    			}
			};
			
        menuItem.addActionListener(hideAction);
     	edgePopup.add(menuItem);		
     	
     	edgePopup.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				tgPanel.setMaintainMouseOver(false);
				tgPanel.setMouseOverE(null);
				tgPanel.repaint();		
                lbNodeHintUI.activate();
			}
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
		});
	}

}
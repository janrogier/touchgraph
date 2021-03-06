/*
 * TouchGraph LLC. Apache-Style Software License
 *
 *
 * Copyright (c) 2001-2002 Alexander Shapiro. All rights reserved.
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

/**  WikiNavigateUI. User interface for moving around the graph.
  *   
  *  @author   Alexander Shapiro                                        
  *  @version  1.02 $Id: WikiNavigateUI.java,v 1.10 2002/06/11 16:29:07 x_ander Exp $
  */

package com.touchgraph.wikibrowser;

import com.touchgraph.graphlayout.TGPanel;
import com.touchgraph.graphlayout.interaction.DragNodeUI;
import com.touchgraph.graphlayout.interaction.TGAbstractClickUI;
import com.touchgraph.graphlayout.interaction.TGAbstractDragUI;
import com.touchgraph.graphlayout.interaction.TGUserInterface;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class WikiNavigateUI extends TGUserInterface
{

    TGPanel tgPanel;
    TGWikiBrowser tgWikiBrowser;
    WikiNavigateMouseListener ml;
    WikiNavigateMouseMotionListener mml;
        
    TGAbstractDragUI hvDragUI;
    
    TGAbstractClickUI hvScrollToCenterUI;
//  WikiNodeHintUI wikiNodeHintUI;
    DragNodeUI dragNodeUI;
    //LocalityScroll localityScroll;
    
    JPopupMenu nodePopup;   
    JPopupMenu edgePopup;   
    WikiNode popupNode;
    WikiEdge popupEdge;

    public WikiNavigateUI(TGWikiBrowser tgwb) {
        tgWikiBrowser = tgwb;
        tgPanel = tgWikiBrowser.getTGPanel();       
        
        //localityScroll=tgWikiBrowser.localityScroll;
        
        hvDragUI = tgWikiBrowser.hvScroll.getHVDragUI();
        
        hvScrollToCenterUI = tgWikiBrowser.hvScroll.getHVScrollToCenterUI();
        
        dragNodeUI = new DragNodeUI(tgPanel);                   
        
//      wikiNodeHintUI = tgWikiBrowser.wikiNodeHintUI;
        
        ml = new WikiNavigateMouseListener();
        mml = new WikiNavigateMouseMotionListener();
        
        setUpNodePopup();
        setUpEdgePopup();
    }
    
    public void activate() {        
        tgPanel.addMouseListener(ml);
        tgPanel.addMouseMotionListener(mml);
    }
    
    public void deactivate() {
        tgPanel.removeMouseListener(ml);
        tgPanel.removeMouseMotionListener(mml);
    }
    
    class WikiNavigateMouseListener extends MouseAdapter {
    
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
               triggerPopup(e);
            }
            else {
                WikiNode mouseOverN = (WikiNode) tgPanel.getMouseOverN();            
                if (e.getModifiers() == MouseEvent.BUTTON1_MASK) { 
                    if (mouseOverN == null) 
                        hvDragUI.activate(e);
                    else 
                        dragNodeUI.activate(e);                 
                }
            }
        }    

        public void mouseClicked(MouseEvent e) {
            WikiNode mouseOverN = (WikiNode) tgPanel.getMouseOverN();
            WikiNode select = (WikiNode) tgPanel.getSelect();
            if ((e.getModifiers() & MouseEvent.BUTTON1_MASK)!=0) { 
                if (mouseOverN != null) {                    
                    if (e.getClickCount()==1) {                        
                        tgWikiBrowser.setWikiTextPane(mouseOverN);
                        tgPanel.setSelect(mouseOverN);                        
                    }
                    else {                   
                        tgPanel.setSelect(mouseOverN);                    
                        tgWikiBrowser.setLocale(mouseOverN);                                                        
                    }
                }
            }   
        }   
        
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
               triggerPopup(e);
            }
        }   

        public void triggerPopup(MouseEvent e) {
            popupNode = (WikiNode) tgPanel.getMouseOverN();
            popupEdge = (WikiEdge) tgPanel.getMouseOverE();
            if (popupNode!=null) {
                tgPanel.setMaintainMouseOver(true);
                nodePopup.show(e.getComponent(), e.getX(), e.getY());
            }
            else if (popupEdge!=null) {
                tgPanel.setMaintainMouseOver(true);
                edgePopup.show(e.getComponent(), e.getX(), e.getY());
            }
            else {
                tgWikiBrowser.wikiPopup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
    
    class WikiNavigateMouseMotionListener extends MouseMotionAdapter {
        public void mouseMoved( MouseEvent e ) {
            WikiNode mouseOverN = (WikiNode) tgPanel.getMouseOverN();         
            if(mouseOverN!=null) {
                tgPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                tgWikiBrowser.statusButton.setText(mouseOverN.getURL());             
            }
            else {
                tgPanel.setCursor(null);
                tgWikiBrowser.statusButton.setText(tgWikiBrowser.textPaneURL);             
            }
        }
        
        public void mouseDragged( MouseEvent e ) {
            WikiNode mouseOverN = (WikiNode) tgPanel.getMouseOverN();         
            if(mouseOverN!=null) {
                tgPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                tgWikiBrowser.statusButton.setText(mouseOverN.getURL());             
            }
            else {
                tgPanel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                tgWikiBrowser.statusButton.setText(tgWikiBrowser.textPaneURL);             
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
        
        menuItem = new JMenuItem("Collapse Node");
        ActionListener collapseAction = new ActionListener() {
                public void actionPerformed(ActionEvent e) {                    
                    if(popupNode!=null) {
                        tgPanel.collapseNode(popupNode );
                    }
                }
            };
            
        menuItem.addActionListener(collapseAction);
        nodePopup.add(menuItem);

        menuItem = new JMenuItem("Hide Node");
        ActionListener hideAction = new ActionListener() {
                public void actionPerformed(ActionEvent e) {                    
                    if(popupNode!=null) {
                        tgPanel.hideNode(popupNode);
                    }
                }
            };
            
        menuItem.addActionListener(hideAction);
        nodePopup.add(menuItem);
    

        nodePopup.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuCanceled(PopupMenuEvent e) {}
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                tgPanel.setMaintainMouseOver(false);                
                tgPanel.setMouseOverN(null);
                tgPanel.repaint();      
//                wikiNodeHintUI.activate();
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
//                wikiNodeHintUI.activate();
            }
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
        });
    }

}
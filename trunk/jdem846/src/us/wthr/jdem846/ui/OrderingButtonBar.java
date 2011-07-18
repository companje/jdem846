/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JToolBar;

@SuppressWarnings("serial")
public class OrderingButtonBar extends JToolBar
{
	public static final int BTN_MOVE_TOP = 0;
	public static final int BTN_MOVE_UP = 1;
	public static final int BTN_MOVE_DOWN = 2;
	public static final int BTN_MOVE_BOTTOM = 3;
	
	private List<OrderingButtonClickedListener> orderingButtonClickedListeners = new LinkedList<OrderingButtonClickedListener>();
	
	private ToolbarButton jbtnMoveTop;
	private ToolbarButton jbtnMoveUp;
	private ToolbarButton jbtnMoveDown;
	private ToolbarButton jbtnMoveBottom;
	
	
	public OrderingButtonBar()
	{
		// Set Properties
		
		this.setFloatable(false);
		
		// Create buttons
		jbtnMoveTop = new ToolbarButton("Move to Top", "/us/wthr/jdem846/ui/icons/go-top.png", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireOrderingButtonClickedListeners(BTN_MOVE_TOP);
			}
		});
		
		jbtnMoveUp = new ToolbarButton("Move Up", "/us/wthr/jdem846/ui/icons/go-up.png", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireOrderingButtonClickedListeners(BTN_MOVE_UP);
			}
		});
		
		jbtnMoveDown = new ToolbarButton("Move Down", "/us/wthr/jdem846/ui/icons/go-down.png", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireOrderingButtonClickedListeners(BTN_MOVE_DOWN);
			}
		});
		
		jbtnMoveBottom = new ToolbarButton("Move to Bottom", "/us/wthr/jdem846/ui/icons/go-bottom.png", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireOrderingButtonClickedListeners(BTN_MOVE_BOTTOM);
			}
		});
		
		// Set button properties
		jbtnMoveTop.setToolTipText("Move to top");
		jbtnMoveUp.setToolTipText("Move up");
		jbtnMoveDown.setToolTipText("Move down");
		jbtnMoveBottom.setToolTipText("Move to bottom");
		
		jbtnMoveTop.setTextDisplayed(false);
		jbtnMoveUp.setTextDisplayed(false);
		jbtnMoveDown.setTextDisplayed(false);
		jbtnMoveBottom.setTextDisplayed(false);
		
		// Set layout
		add(jbtnMoveTop);
		add(jbtnMoveUp);
		add(jbtnMoveDown);
		add(jbtnMoveBottom);
		
	}
	
	public void setButtonEnabled(int button, boolean enabled)
	{
		
		switch(button) {
		case BTN_MOVE_TOP:
			jbtnMoveTop.setEnabled(enabled);
			break;
		case BTN_MOVE_UP:
			jbtnMoveUp.setEnabled(enabled);
			break;
		case BTN_MOVE_DOWN:
			jbtnMoveDown.setEnabled(enabled);
			break;
		case BTN_MOVE_BOTTOM:
			jbtnMoveBottom.setEnabled(enabled);
			break;
		}
	}
	
	public void addOrderingButtonClickedListener(OrderingButtonClickedListener listener)
	{
		orderingButtonClickedListeners.add(listener);
	}
	
	public boolean removeOrderingButtonClickedListener(OrderingButtonClickedListener listener)
	{
		return orderingButtonClickedListeners.remove(listener);
	}
	
	protected void fireOrderingButtonClickedListeners(int button)
	{
		for (OrderingButtonClickedListener listener : orderingButtonClickedListeners) {
			
			switch(button) {
			case BTN_MOVE_TOP:
				listener.onMoveTop();
				break;
			case BTN_MOVE_UP:
				listener.onMoveUp();
				break;
			case BTN_MOVE_DOWN:
				listener.onMoveDown();
				break;
			case BTN_MOVE_BOTTOM:
				listener.onMoveBottom();
				break;
			}
			
		}
	}
	
	public interface OrderingButtonClickedListener
	{
		public void onMoveTop();
		public void onMoveUp();
		public void onMoveDown();
		public void onMoveBottom();
	}
	
}

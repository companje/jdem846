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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import us.wthr.jdem846.ui.base.TextField;

@SuppressWarnings("serial")
public class NumberTextField extends TextField
{
	private boolean decimal = false;
	
	
	public NumberTextField(boolean decimal)
	{
		this.decimal = decimal;
		this.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) 
			{
				
			}
			public void keyReleased(KeyEvent e) 
			{
				
			}
			public void keyTyped(KeyEvent e) 
			{
				try {
					if (e.getKeyChar() == '.' && isDecimal() && !(getText().contains("."))) {
						return;
					} else {
						Integer.parseInt(""+e.getKeyChar());
					}
				} catch(NumberFormatException ex) {
					e.consume();
				}
				
			}
			
		});
	}

	@Override
	public String getText()
	{
		String text = super.getText();
		if (text != null && text.length() > 0) {
			return text;
		} else {
			return "0";
		}
	}
	
	public int getInteger()
	{
		return Integer.valueOf(this.getText());
	}
	
	public double getDouble()
	{
		return Double.valueOf(this.getText());
	}
	
	public float getFloat()
	{
		return Float.valueOf(this.getText());
	}
	
	public boolean isDecimal() {
		return decimal;
	}


	public void setDecimal(boolean decimal) {
		this.decimal = decimal;
	}
	
	
	
	
	
}

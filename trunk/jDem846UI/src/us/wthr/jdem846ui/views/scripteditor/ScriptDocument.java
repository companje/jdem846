package us.wthr.jdem846ui.views.scripteditor;

import org.eclipse.jface.text.AbstractDocument;

public class ScriptDocument extends AbstractDocument
{
	
	public ScriptDocument()
	{
		//this.setTextStore(new ScriptTextStore());
		this.set("Hi There");
	}
//	
//	class ScriptTextStore implements ITextStore
//	{
//		String text;
//		
//		@Override
//		public char get(int i)
//		{
//			if (text != null) {
//				return text.charAt(i);
//			} else {
//				return 0x0;
//			}
//		}
//
//		@Override
//		public String get(int s, int e)
//		{
//			if (text != null) {
//				return text.substring(s, e);
//			} else {
//				return null;
//			}
//		}
//
//		@Override
//		public int getLength()
//		{
//			if (text != null) {
//				return text.length();
//			} else {
//				return 0;
//			}
//		}
//
//		@Override
//		public void replace(int arg0, int arg1, String arg2)
//		{
//			if (text != null) {
//				//text.replace(oldChar, newChar)
//			}
//		}
//
//		@Override
//		public void set(String t)
//		{
//			this.text = t;
//		}
//		
//	}

}

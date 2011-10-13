package us.wthr.jdem846.scripting;

public enum ScriptLanguageEnum
{
	JYTHON("jython"),
	GROOVY("groovy");
	
	private final String text;
	
	ScriptLanguageEnum(String text)
	{
		this.text = text;
	}
	
	public String text() { return text; }

}

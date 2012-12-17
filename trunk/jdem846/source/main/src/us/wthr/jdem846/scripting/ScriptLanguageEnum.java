package us.wthr.jdem846.scripting;

public enum ScriptLanguageEnum
{
	JYTHON("jython", "text/jython"),
	GROOVY("groovy", "text/groovy"),
	SCALA("scala", "text/scala"),
	JAVASCRIPT("javascript", "text/javascript");
	
	private final String text;
	private final String mime;
	
	ScriptLanguageEnum(String text, String mime)
	{
		this.text = text;
		this.mime = mime;
	}
	
	public String text() { return text; }
	public String mime() { return mime; }
	
	public static ScriptLanguageEnum getLanguageFromString(String langString)
	{
		for (ScriptLanguageEnum value : ScriptLanguageEnum.values()) {
			if (value.text().equalsIgnoreCase(langString))
				return value;
		}
		return null;
	}
	
}

package us.wthr.jdem846.prompt;

public class FilePathPrompt {
	
	private static FilePathPromptCallback filePathPromptCallback = null;
	
	
	
	public static void setFilePathPromptCallback(FilePathPromptCallback callback)
	{
		FilePathPrompt.filePathPromptCallback = callback;
	}
	
	public static String prompt(FilePathPromptMode mode, String previous) // More options to come
	{
		if (filePathPromptCallback != null) {
			return filePathPromptCallback.prompt(mode, previous);
		} else {
			return null;
		}
	}
	
	
}

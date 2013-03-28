package us.wthr.jdem846.ui;

public class SplashNotifyList
{
	public String[] notifications = new String[3];
	public int totalAdded = 0;
	
	public SplashNotifyList()
	{
		notifications[0] = notifications[1] = notifications[2] = null;
	}
	
	public void add(String notification)
	{
		notifications[0] = notifications[1];
		notifications[1] = notifications[2];
		notifications[2] = notification;
		totalAdded++;
	}
}

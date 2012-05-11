package us.wthr.jdem846;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StartupLoadNotifyQueue
{
	
	private static Queue<String> notifications = new ConcurrentLinkedQueue<String>();
	private static List<NotificationAddedListener> addListeners = new LinkedList<NotificationAddedListener>();
	
	protected StartupLoadNotifyQueue()
	{
		
	}
	
	
	public static void add(String notify)
	{
		notifications.add(notify);
		fireNotificationAddedListeners(notify);
	}
	
	
	public static int queueSize()
	{
		return notifications.size();
	}
	
	public static String poll()
	{
		return notifications.poll();
	}
	
	public static String peek()
	{
		return notifications.peek();
	}
	
	
	public static void addNotificationAddedListener(NotificationAddedListener listener)
	{
		synchronized(addListeners) {
			addListeners.add(listener);
		}
	}
	
	public static void removeNotificationAddedListener(NotificationAddedListener listener)
	{
		synchronized(addListeners) {
			addListeners.remove(listener);
		}
	}
	
	protected static void fireNotificationAddedListeners(String notification)
	{
		synchronized(addListeners) {
			for (NotificationAddedListener listener : addListeners) {
				listener.onNotificationAdded(notification);
			}
		}
	}
	
	public interface NotificationAddedListener {
		public void onNotificationAdded(String notification);
	}
	
}

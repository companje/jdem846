package us.wthr.jdem846.swtui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.MenuItem;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ShellMenu
{
	private static Log log = Logging.getLog(ShellMenu.class);
	
	private static Menu instance;
	

	public static Menu createShellMenu(Shell shell)
	{
		if (instance != null)
			return instance;
		
		instance = new Menu(shell, SWT.BAR);
		
		

		return instance;
	}
	
}

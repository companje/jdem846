package us.wthr.jdem846.ui.usage;

import java.net.NetworkInterface;
import java.util.Enumeration;

import us.wthr.jdem846.math.MathExt;

public abstract class Notify
{
	
	public abstract String getNotifyType(); 
	
	
	/** Creates an installation id string. Uses a network MAC address (if available) to generate
	 * the string. This is not intended for anti-piracy protection (this _is_ open source code, after all)
	 * so this should be unique enough for our purposes. 
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getInstallationId() throws Exception
	{
		// MAC Address as installation ID
		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
		
		String installationId = "unresolved";
		
		while (e.hasMoreElements()) {
			
			NetworkInterface netInterface = e.nextElement();
			byte[] mac = netInterface.getHardwareAddress();
			
			if (mac != null && mac.length > 0) {
				
				installationId = "";
				
				for (int i = 0; i < mac.length; i++) {
					if (mac[i] != 0x0) {
						installationId += "" + ((int)MathExt.abs(mac[i])) + "-";
					}
				}
				
				installationId = installationId.substring(0, installationId.length() - 1);
				break;
			}
			
		}
		
		return installationId;
	}
	
	
}

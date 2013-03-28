package us.wthr.jdem846.ui.usage;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class SingleRunNotifyFactory
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(SingleRunNotifyFactory.class);
	
	protected SingleRunNotifyFactory()
	{
		
	}
	
	
	public static SingleRunNotify create() throws Exception
	{
		
		SingleRunNotify notify = new SingleRunNotify();

		notify.setSoftwareVersion(JDem846Properties.getProperty("us.wthr.jdem846.version"));
		notify.setDevelopmentTestRun(JDem846Properties.getProperty("us.wthr.jdem846.developmentTestRun"));
		
		
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		
		notify.setRuntimeName(System.getProperty("java.runtime.name"));
		notify.setRuntimeVersion(System.getProperty("java.runtime.version"));


		notify.setVmName(runtime.getVmName());
		notify.setVmVendor(runtime.getVmVendor());
		notify.setVmVersion(runtime.getVmVersion());
		

		MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
		
		notify.setMemXms(getJvmFlag("-Xms"));
		notify.setMemXmx(getJvmFlag("-Xmx"));
		
		MemoryUsage heapMemoryUsage = memory.getHeapMemoryUsage();
		MemoryUsage nonHeapMemoryUsage = memory.getNonHeapMemoryUsage();

		notify.setMemHeapInit(""+heapMemoryUsage.getInit());
		notify.setMemHeapCommitted(""+heapMemoryUsage.getCommitted());
		notify.setMemHeapMax(""+heapMemoryUsage.getMax());
		notify.setMemHeapUsed(""+heapMemoryUsage.getUsed());
		
		notify.setMemNonHeapInit(""+nonHeapMemoryUsage.getInit());
		notify.setMemNonHeapCommitted(""+nonHeapMemoryUsage.getCommitted());
		notify.setMemNonHeapMax(""+nonHeapMemoryUsage.getMax());
		notify.setMemNonHeapUsed(""+nonHeapMemoryUsage.getUsed());
		
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		
		notify.setOsArchitecture(os.getArch());
		notify.setOsName(os.getName());
		notify.setOsVersion(os.getVersion());
		notify.setOsDesktop(System.getProperty("sun.desktop"));
		notify.setOsPatchLevel(System.getProperty("sun.os.patch.level"));
		

		notify.setCpuEndian(System.getProperty("sun.cpu.endian"));
		notify.setCpuName(System.getProperty("sun.cpu.isalist"));
		notify.setCpuAvailableProcessors(""+os.getAvailableProcessors());

		notify.setUserCountry(System.getProperty("user.country"));
		notify.setUserLanguage(System.getProperty("user.language"));
		notify.setUserTimezone(System.getProperty("user.timezone"));
		
		return notify;
	}
	
	
	
	protected static String getJvmFlag(String flag)
	{
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		List<String> args = runtime.getInputArguments();
		
		for (String arg : args) {
			if (arg.startsWith(flag)) {
				String value = arg.substring(flag.length());
				return value;
			}
		}
		
		return null;
	}
	

}

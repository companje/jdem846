package us.wthr.jdem846.ui.usage;

public class SingleRunNotify extends Notify
{
	
	
	private String softwareVersion;
	private String developmentTestRun;
	
	private String runtimeName;
	private String runtimeVersion;
	
	private String vmName;
	private String vmVersion;
	private String vmVendor;
	
	private String memXms;
	private String memXmx;
	private String memHeapInit;
	private String memHeapCommitted;
	private String memHeapMax;
	private String memHeapUsed;
	private String memNonHeapInit;
	private String memNonHeapCommitted;
	private String memNonHeapMax;
	private String memNonHeapUsed;
	
	private String osArchitecture;
	private String osName;
	private String osVersion;
	private String osDesktop;
	private String osPatchLevel;
	
	private String cpuEndian;
	private String cpuName;
	private String cpuAvailableProcessors;
	
	private String userCountry;
	private String userLanguage;
	private String userTimezone;
	
	
	public SingleRunNotify()
	{
		
	}

	@Override
	public String getNotifyType()
	{
		return "single-run";
	}
	
	


	public String getSoftwareVersion()
	{
		return softwareVersion;
	}


	public void setSoftwareVersion(String softwareVersion)
	{
		this.softwareVersion = softwareVersion;
	}


	public String getDevelopmentTestRun()
	{
		return developmentTestRun;
	}


	public void setDevelopmentTestRun(String developmentTestRun)
	{
		this.developmentTestRun = developmentTestRun;
	}


	public String getRuntimeName()
	{
		return runtimeName;
	}


	public void setRuntimeName(String runtimeName)
	{
		this.runtimeName = runtimeName;
	}


	public String getRuntimeVersion()
	{
		return runtimeVersion;
	}


	public void setRuntimeVersion(String runtimeVersion)
	{
		this.runtimeVersion = runtimeVersion;
	}


	public String getVmName()
	{
		return vmName;
	}


	public void setVmName(String vmName)
	{
		this.vmName = vmName;
	}


	public String getVmVersion()
	{
		return vmVersion;
	}


	public void setVmVersion(String vmVersion)
	{
		this.vmVersion = vmVersion;
	}


	public String getVmVendor()
	{
		return vmVendor;
	}


	public void setVmVendor(String vmVendor)
	{
		this.vmVendor = vmVendor;
	}





	public String getOsArchitecture()
	{
		return osArchitecture;
	}


	public void setOsArchitecture(String osArchitecture)
	{
		this.osArchitecture = osArchitecture;
	}


	public String getOsName()
	{
		return osName;
	}


	public void setOsName(String osName)
	{
		this.osName = osName;
	}


	public String getOsVersion()
	{
		return osVersion;
	}


	public void setOsVersion(String osVersion)
	{
		this.osVersion = osVersion;
	}


	public String getOsDesktop()
	{
		return osDesktop;
	}


	public void setOsDesktop(String osDesktop)
	{
		this.osDesktop = osDesktop;
	}


	public String getOsPatchLevel()
	{
		return osPatchLevel;
	}


	public void setOsPatchLevel(String osPatchLevel)
	{
		this.osPatchLevel = osPatchLevel;
	}


	public String getCpuEndian()
	{
		return cpuEndian;
	}


	public void setCpuEndian(String cpuEndian)
	{
		this.cpuEndian = cpuEndian;
	}


	public String getCpuName()
	{
		return cpuName;
	}


	public void setCpuName(String cpuName)
	{
		this.cpuName = cpuName;
	}

	
	
	public String getCpuAvailableProcessors()
	{
		return cpuAvailableProcessors;
	}


	public void setCpuAvailableProcessors(String cpuAvailableProcessors)
	{
		this.cpuAvailableProcessors = cpuAvailableProcessors;
	}


	public String getUserCountry()
	{
		return userCountry;
	}


	public void setUserCountry(String userCountry)
	{
		this.userCountry = userCountry;
	}


	public String getUserLanguage()
	{
		return userLanguage;
	}


	public void setUserLanguage(String userLanguage)
	{
		this.userLanguage = userLanguage;
	}


	public String getUserTimezone()
	{
		return userTimezone;
	}


	public void setUserTimezone(String userTimezone)
	{
		this.userTimezone = userTimezone;
	}


	public String getMemXms()
	{
		return memXms;
	}


	public void setMemXms(String memXms)
	{
		this.memXms = memXms;
	}


	public String getMemXmx()
	{
		return memXmx;
	}


	public void setMemXmx(String memXmx)
	{
		this.memXmx = memXmx;
	}


	public String getMemHeapInit()
	{
		return memHeapInit;
	}


	public void setMemHeapInit(String memHeapInit)
	{
		this.memHeapInit = memHeapInit;
	}


	public String getMemHeapCommitted()
	{
		return memHeapCommitted;
	}


	public void setMemHeapCommitted(String memHeapCommitted)
	{
		this.memHeapCommitted = memHeapCommitted;
	}


	public String getMemHeapMax()
	{
		return memHeapMax;
	}


	public void setMemHeapMax(String memHeapMax)
	{
		this.memHeapMax = memHeapMax;
	}


	public String getMemHeapUsed()
	{
		return memHeapUsed;
	}


	public void setMemHeapUsed(String memHeapUsed)
	{
		this.memHeapUsed = memHeapUsed;
	}


	public String getMemNonHeapInit()
	{
		return memNonHeapInit;
	}


	public void setMemNonHeapInit(String memNonHeapInit)
	{
		this.memNonHeapInit = memNonHeapInit;
	}


	public String getMemNonHeapCommitted()
	{
		return memNonHeapCommitted;
	}


	public void setMemNonHeapCommitted(String memNonHeapCommitted)
	{
		this.memNonHeapCommitted = memNonHeapCommitted;
	}


	public String getMemNonHeapMax()
	{
		return memNonHeapMax;
	}


	public void setMemNonHeapMax(String memNonHeapMax)
	{
		this.memNonHeapMax = memNonHeapMax;
	}


	public String getMemNonHeapUsed()
	{
		return memNonHeapUsed;
	}


	public void setMemNonHeapUsed(String memNonHeapUsed)
	{
		this.memNonHeapUsed = memNonHeapUsed;
	}


	
	
	
	
}

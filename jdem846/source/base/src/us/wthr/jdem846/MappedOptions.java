package us.wthr.jdem846;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.wthr.jdem846.project.ProjectModel;
import us.wthr.jdem846.util.NumberFormattingUtil;

public abstract class MappedOptions
{
	private List<String> optionPrefixes = new LinkedList<String>();
	private Map<String, String> optionsMap = new HashMap<String, String>();
	
	private List<OptionChangeListener> optionChangeListeners = new LinkedList<OptionChangeListener>();
	private boolean listenersActive = true;
	
	public MappedOptions()
	{
		
	}
	

	public Set<String> getOptionNames()
	{
		return optionsMap.keySet();
	}
	
	public void setOption(String name, Object value)
	{
		String sValue = null;
		
		if (value == null) {
			return;
		}
		
		
		
		if (value instanceof String) {
			sValue = (String) value;
		} else if (value instanceof Integer ||
					value instanceof Double ||
					value instanceof Long ||
					value instanceof Float) {
			sValue = NumberFormattingUtil.format(value);
		} else if (value instanceof Boolean){
			sValue = Boolean.toString((Boolean)value);
		} else {
			throw new InvalidParameterException("Invalid parameter type: " + value.getClass().getName());
		}
		
		fireOptionChangeListeners(name, value);
		optionsMap.put(name, sValue);
	}
	
	
	public String getOption(String name)
	{
		return optionsMap.get(name);
	}
	
	public boolean hasOption(String name)
	{
		return (optionsMap.containsKey(name));
	}
	
	public String removeOption(String name)
	{
		return optionsMap.remove(name);
	}
	
	public boolean getBooleanOption(String name)
	{
		if (hasOption(name))
			return Boolean.parseBoolean(getOption(name));
		else
			return false;
	}
	
	public int getIntegerOption(String name)
	{
		if (hasOption(name))
			return Integer.parseInt(getOption(name));
		else
			return 0;
	}
	
	public double getDoubleOption(String name)
	{
		if (hasOption(name))
			return Double.parseDouble(getOption(name));
		else
			return 0.0;
	}
	
	public float getFloatOption(String name)
	{
		if (hasOption(name))
			return Float.parseFloat(getOption(name));
		else
			return 0.0f;
	}
	
	public long getLongOption(String name)
	{
		if (hasOption(name))
			return Long.parseLong(getOption(name));
		else
			return 0;
	}
	
	public void syncToProjectModel(ProjectModel projectModel)
	{
		for (String optionName : getOptionNames()) {
			projectModel.setOption(optionName, optionsMap.get(optionName).toString());
		}
		
	}
	
	/** Synchronizes options from a project model object. Does not 
	 * fire option change listeners during this process.
	 * 
	 * @param projectModel
	 */
	public void syncFromProjectModel(ProjectModel projectModel)
	{
		setListenersActive(false);
		
		for (String optionName : projectModel.getOptionKeys()) {
			if (!isCoveredOption(optionName)) {
				continue;
			}
			
			if (projectModel.hasOption(optionName)) {
				String optionValue = projectModel.getOption(optionName);
				if (optionValue != null) {
					this.setOption(optionName, optionValue);
				}
			}
		}
		
		setListenersActive(true);
	}
	
	protected void addOptionPrefix(String prefix)
	{
		optionPrefixes.add(prefix);
	}
	
	protected boolean isCoveredOption(String name)
	{
		for (String prefix : this.optionPrefixes) {
			if (name.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}
	
	protected void setListenersActive(boolean listenersActive) 
	{
		this.listenersActive = listenersActive;
	}
	
	protected boolean arelistenersActive()
	{
		return listenersActive;
	}
	
	protected void fireOptionChangeListeners(String key, Object newValue)
	{
		Object oldValue;
		
		if (!hasOption(key)) {
			oldValue = null;
		} else if (newValue instanceof String) {
			oldValue = getOption(key);
		} else if (newValue instanceof Integer) {
			oldValue = getIntegerOption(key);
		} else if (newValue instanceof Double) {
			oldValue = getDoubleOption(key);
		} else if (newValue instanceof Float) {
			oldValue = getFloatOption(key);
		} else if (newValue instanceof Long) {
			oldValue = getLongOption(key);
		} else if (newValue instanceof Boolean) {
			oldValue = getBooleanOption(key);
		} else {
			oldValue = getOption(key);
		}
		
		fireOptionChangeListeners(key, oldValue, newValue);
		
	}
	
	protected void fireOptionChangeListeners(String key, Object oldValue, Object newValue)
	{
		if (listenersActive) {
			for (OptionChangeListener listener : optionChangeListeners) {
				listener.onOptionChanged(key, oldValue, newValue);
			}
		}
	}
	
	public void addOptionChangeListener(OptionChangeListener listener)
	{
		optionChangeListeners.add(listener);
	}
	
	public boolean remove(OptionChangeListener listener)
	{
		return optionChangeListeners.remove(listener);
	}
	
}

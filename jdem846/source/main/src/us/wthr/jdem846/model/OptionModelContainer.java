package us.wthr.jdem846.model;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.annotations.ProcessOption;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;

public class OptionModelContainer
{
	private static Log log = Logging.getLog(OptionModelContainer.class);
	
	
	private OptionModel optionModel;
	
	private Map<String, Method> allMethods;
	private Map<String, Method> annotatedMethods;
	private Map<String, OptionModelMethodContainer> getterSetterMethods;
	private Map<String, OptionModelPropertyContainer> propertyContainers;
	
	public OptionModelContainer(OptionModel optionModel) throws InvalidProcessOptionException
	{
		this.optionModel = optionModel;
		
		log.info("Loading option model for " + optionModel.getClass().getName());
		
		allMethods = getAllMethods();
		annotatedMethods = getAnnotatedMethods();
		getterSetterMethods = getGetterSetterMethods();
		propertyContainers = getPropertyContainers();
	}
	
	private Map<String, Method> getAllMethods()
	{
		Map<String, Method> methods = new HashMap<String, Method>();
		
		for (Method method : optionModel.getClass().getMethods()) {
			methods.put(method.getName(), method);
		}

		return methods;
	}
	
	private Map<String, Method> getAnnotatedMethods()
	{
		Map<String, Method> methods = new HashMap<String, Method>();
		
		for (Method method : optionModel.getClass().getMethods()) {
			if (method.isAnnotationPresent(ProcessOption.class)) {
				methods.put(method.getName(), method);
			}
		}

		return methods;
	}
	
	
	private Map<String, OptionModelMethodContainer> getGetterSetterMethods() throws InvalidProcessOptionException
	{
		Map<String, OptionModelMethodContainer> methods = new HashMap<String, OptionModelMethodContainer>();
		
		for (Method method : optionModel.getClass().getMethods()) {
			
			boolean isValid = false;
			try {
				isValid = OptionModelMethodContainer.validateAsGetterOrSetter(method);
			} catch (Exception ex) {
				
			}

			if (isValid) {
				OptionModelMethodContainer container = new OptionModelMethodContainer(optionModel, method);
				methods.put(method.getName(), container);
			}

		}
		
		return methods;
	}
	
	private Map<String, OptionModelPropertyContainer> getPropertyContainers() throws InvalidProcessOptionException
	{
		Map<String, OptionModelPropertyContainer> containers = new HashMap<String, OptionModelPropertyContainer>();
		

		for (Method method : optionModel.getClass().getMethods()) {
			if (method.isAnnotationPresent(ProcessOption.class)) {
				OptionModelMethodContainer m0 = new OptionModelMethodContainer(optionModel, method);
				String propertyName = OptionModelMethodContainer.determinePropertyName(method.getName());
				
				Method other = getMethodByPropertyName(propertyName, method);
				OptionModelMethodContainer m1 = new OptionModelMethodContainer(optionModel, other);

				OptionModelPropertyContainer propertyContainer = new OptionModelPropertyContainer(m0, m1);

				containers.put(propertyName, propertyContainer);
				
			}
		}
		
		return containers;
	}
	
	
	private Method getMethodByPropertyName(String findPropertyName, Method doesntEqual)
	{
		Method findMethod = null;
		
		for (Method method : optionModel.getClass().getMethods()) {
			String methodPropertyName = OptionModelMethodContainer.determinePropertyName(method.getName());
			
			if (findPropertyName.equals(methodPropertyName) && !method.equals(doesntEqual)) {
				findMethod = method;
				break;
			}
			
		}
		
		return findMethod;
	}
	
	
	public OptionModelMethodContainer getMethodByName(String methodName)
	{
		return this.getterSetterMethods.get(methodName);
	}
	
	public OptionModelMethodContainer getGetMethodByPropertyName(String propertyName)
	{
		for (OptionModelMethodContainer method : this.getterSetterMethods.values()) {
			if (method.isGetter() && method.getPropertyName().equals(propertyName)) {
				return method;
			}
		}
		
		return null;
	}
	
	public OptionModelMethodContainer getSetMethodByPropertyName(String propertyName)
	{
		for (OptionModelMethodContainer method : this.getterSetterMethods.values()) {
			if (method.isSetter() && method.getPropertyName().equals(propertyName)) {
				return method;
			}
		}
		
		return null;
	}
	
	public String getPropertyNameById(String id)
	{
		for (OptionModelMethodContainer method : this.getterSetterMethods.values()) {
			if (method.hasAnnotation() && method.getId().equals(id)) {
				return method.getPropertyName();
			}
		}
		
		return null;
	}
	
	public OptionModelMethodContainer getGetMethodById(String id)
	{
		String propertyName = getPropertyNameById(id);
		if (propertyName == null) {
			return null;
		}
		
		return getGetMethodByPropertyName(propertyName);
	}
	
	public OptionModelMethodContainer getSetMethodById(String id)
	{
		String propertyName = getPropertyNameById(id);
		if (propertyName == null) {
			return null;
		}
		
		return getSetMethodByPropertyName(propertyName);
	}
	
	
	public OptionModelPropertyContainer getPropertyByName(String propertyName)
	{
		return this.propertyContainers.get(propertyName);
	}
	
	public OptionModelPropertyContainer getPropertyById(String id)
	{
		String propertyName = getPropertyNameById(id);
		
		if (propertyName == null) {
			return null;
		}
		
		return getPropertyByName(propertyName);
	}
	
	
	public int getPropertyCount()
	{
		return this.propertyContainers.size();
	}
	
	public List<String> getPropertyNames()
	{
		List<String> propertyNames = new LinkedList<String>();
		for (String propertyName : propertyContainers.keySet()) {
			propertyNames.add(propertyName);
		}
		//propertyNames.addAll(propertyContainers.keySet());
		return propertyNames;
	}
	
}

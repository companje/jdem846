package us.wthr.jdem846.model;

import java.lang.reflect.Method;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.exceptions.InvalidProcessOptionException;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;

public class OptionModelPropertyContainer
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(OptionModelPropertyContainer.class);
	
	private OptionModelMethodContainer annotated;
	private OptionModelMethodContainer setter;
	private OptionModelMethodContainer getter;
	
	public OptionModelPropertyContainer(Object declaringObject, Method m0, Method m1) throws InvalidProcessOptionException
	{
		this(new OptionModelMethodContainer(declaringObject, m0), new OptionModelMethodContainer(declaringObject, m1));
	}
	
	public OptionModelPropertyContainer(OptionModelMethodContainer m0, OptionModelMethodContainer m1) throws InvalidProcessOptionException
	{
		if (m0.isSetter() && m1.isSetter()) {
			throw new InvalidProcessOptionException("Both methods cannot be setters");
		}
		
		if (m0.isGetter() && m1.isGetter()) {
			throw new InvalidProcessOptionException("Both methods cannot be getters");
		}
		
		if (!m0.hasAnnotation() && !m1.hasAnnotation()) {
			throw new InvalidProcessOptionException("Neither method has required annotation");
		}
		
		if (!m0.getPropertyName().equals(m1.getPropertyName())) {
			throw new InvalidProcessOptionException("Property names do not match");
		}
		
		if (!m0.getType().equals(m1.getType())) {
			throw new InvalidProcessOptionException("Property types do not match");
		}
		
		if (m0.isSetter()) {
			setter = m0;
		} else {
			getter = m0;
		}
		
		if (m1.isSetter()) {
			setter = m1;
		} else {
			getter = m1;
		}
		
		if (m0.hasAnnotation()) {
			annotated = m0;
		}
		
		if (m1.hasAnnotation()) {
			annotated = m1;
		}
		
		
	}
	
	
	public String getId()
	{
		return annotated.getId();
	}
	
	public String getLabel()
	{
		return annotated.getLabel();
	}
	
	public String getTooltip()
	{
		return annotated.getTooltip();
	}
	
	public String getOptionGroup()
	{
		return annotated.getOptionGroup();
	}
	
	public Class<?> getListModelClass()
	{
		return annotated.getListModelClass();
	}
	
	public Class<?> getValidatorClass()
	{
		return annotated.getValidatorClass();
	}
	
	public boolean isEnabled()
	{
		return annotated.isEnabled();
	}
	
	public String getPropertyName()
	{
		return getter.getPropertyName();
	}
	
	public Object getValue() throws MethodContainerInvokeException
	{
		return getter.getValue();
	}
	
	public void setValue(Object object) throws MethodContainerInvokeException
	{
		setter.setValue(object);
	}
	
	public Class<?> getType()
	{
		return getter.getType();
	}
	
}

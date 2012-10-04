package us.wthr.jdem846.model.processing;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GridMethodStack
{
	private List<InstanceMethodContainer> methodList = new ArrayList<InstanceMethodContainer>();
	
	
	public GridMethodStack()
	{
		
	}
	
	
	public void addMethod(Object instance, Method method)
	{
		InstanceMethodContainer container = new InstanceMethodContainer(instance, method);
		methodList.add(container);
	}
	
	public void invoke() throws Exception
	{
		invoke(null);
	}
	
	public void invoke(Object...params) throws Exception
	{
		
		for (InstanceMethodContainer container : methodList) {
			if (container.instance != null && container.method != null) {
				container.method.invoke(container.instance, params);
			}
		}
		
	}
	
	
	
	private class InstanceMethodContainer
	{
		public Object instance;
		public Method method;
		
		
		public InstanceMethodContainer(Object instance, Method method)
		{
			this.instance = instance;
			this.method = method;
		}
	}
	
}

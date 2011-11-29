package us.wthr.jdem846;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AnnotatedClassMap
{
	private Map<String, List<String>> classListMap = new HashMap<String, List<String>>();
	
	public AnnotatedClassMap()
	{
		
	}
	
	public void addAnnotatedClass(String annotationClassName, String className)
	{
		List<String> annotatedClassList = classListMap.get(annotationClassName);
		if (annotatedClassList == null) {
			annotatedClassList = new LinkedList<String>();
			classListMap.put(annotationClassName, annotatedClassList);
		}
		
		if (!annotatedClassList.contains(className)) {
			annotatedClassList.add(className);
		}
		
		
	}
	
	
	public List<String> getAnnotatedClassList(String annotationClassName)
	{
		return classListMap.get(annotationClassName);
	}
	
	public List<Class<?>> getAnnotatedClasses(String annotationClassName) throws Exception
	{
		List<Class<?>> classList = new LinkedList<Class<?>>();
		List<String> classNameList = getAnnotatedClassList(annotationClassName);
		
		if (classNameList != null) {
			for (String className : classNameList) {
				Class<?> clazz = getClassObject(className);
				classList.add(clazz);
			}
		}
		
		return classList;
	}
	
	
	protected Class<?> getClassObject(String clazzName) throws Exception
	{
		Class<?> clazz = Class.forName(clazzName, true, Thread.currentThread().getContextClassLoader());
		return clazz;
	}
}

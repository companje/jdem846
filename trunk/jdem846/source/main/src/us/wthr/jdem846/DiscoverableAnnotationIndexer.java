package us.wthr.jdem846;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import us.wthr.jdem846.annotations.Discoverable;
import us.wthr.jdem846.exception.AnnotationIndexerException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.annotations.GridProcessing;

/** Creates an index of classes annotated with annotations that are annotated 
 * with the Discoverable annotation. annotation. ;-)
 * 
 * @author Kevin M. Gill
 *
 */
public class DiscoverableAnnotationIndexer
{
	private static Log log = Logging.getLog(DiscoverableAnnotationIndexer.class);

	private static AnnotatedClassMap annotatedClassMap = new AnnotatedClassMap();
	
	private static Reflections reflections = null;
	
	private static List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
	private static List<URL> urls = new ArrayList<URL>();
	private static List<String> packageFilters = new ArrayList<String>();
	
	protected DiscoverableAnnotationIndexer()
	{
		
	}
	
	public static void addClassLoaders(ClassLoader ... classLoaders)
	{
		for (ClassLoader classLoader : classLoaders) {
			DiscoverableAnnotationIndexer.classLoaders.add(classLoader);
		}
	}
	
	public static void addUrls(URL ... urls)
	{
		for (URL url : urls) {
			DiscoverableAnnotationIndexer.urls.add(url);
		}
	}
	
	protected static Reflections getReflections()
	{
		if (DiscoverableAnnotationIndexer.reflections == null) {
			ConfigurationBuilder config = new ConfigurationBuilder();
			config.addClassLoader(DiscoverableAnnotationIndexer.class.getClassLoader());
			config.addClassLoaders(DiscoverableAnnotationIndexer.classLoaders);
			config.addUrls(DiscoverableAnnotationIndexer.urls);
			
			FilterBuilder filterBuilder = new FilterBuilder();
			filterBuilder.include("us.wthr.jdem846.*");
			
			for (String packageFilter : DiscoverableAnnotationIndexer.packageFilters) {
				filterBuilder.include(packageFilter);
			}
			
			config.filterInputsBy(filterBuilder);
			
			reflections = new Reflections(config);
		}
		return reflections;
	}
	
	
	public static void createIndex() throws AnnotationIndexerException
	{
		DiscoverableAnnotationIndexer indexer = new DiscoverableAnnotationIndexer();
		indexer.initDiscoverable();

	}
	
	protected void initDiscoverable() throws AnnotationIndexerException
	{	
		List<Class<?>> discoverableAnnotations = null;
		try {
			discoverableAnnotations = findClassesWithAnnotation(Discoverable.class);
		} catch (Exception ex) {
			throw new AnnotationIndexerException("Error finding classes with annotation '" + GridProcessing.class.getCanonicalName() + "': " + ex.getMessage(), ex);
		}
		
		
		if (discoverableAnnotations != null) {
			for (Class<?> discoverableAnnotation : discoverableAnnotations) {
				List<Class<?>> annotatedClasses = null;
				try {
					annotatedClasses = findClassesWithAnnotation((Class<? extends Annotation>)discoverableAnnotation);
				} catch (Exception ex) {
					throw new AnnotationIndexerException("Error finding classes with annotation '" + discoverableAnnotation.getCanonicalName() + "': " + ex.getMessage(), ex);
				}
				
				if (annotatedClasses != null) {
					for (Class<?> annotatedClass : annotatedClasses) {
						annotatedClassMap.addAnnotatedClass(discoverableAnnotation.getName(), annotatedClass.getCanonicalName());
					}
				}
				
			}
		}
		
	}
	

	
	protected List<Class<?>> findClassesWithAnnotation(Class<? extends Annotation> annotationClass) throws Exception
	{
		List<Class<?>> clazzList = new ArrayList<Class<?>>();
		
		log.info("Searching for classes with annotation '" + annotationClass.getName() + "'");

		Set<Class<?>> annotatedClazzList = getReflections().getTypesAnnotatedWith(annotationClass);
		
		for (Class<?> clazz : annotatedClazzList) {
			String clazzName = clazz.getCanonicalName();
			log.info("	Found Annotated Class: " + clazzName);
			clazzList.add(clazz);
		}
		
		return clazzList;
	}
	

	
	public static List<String> getAnnotatedClassList(String annotationClassName)
	{
		List<String> classNameList = new LinkedList<String>();
		List<String> list = DiscoverableAnnotationIndexer.annotatedClassMap.getAnnotatedClassList(annotationClassName);
		if (list != null) {
			classNameList.addAll(list);
		}
		return classNameList;
	}
	
	public static List<Class<?>> getAnnotatedClasses(String annotationClassName) throws AnnotationIndexerException
	{
		try {
			
			List<Class<?>> clazzList = new LinkedList<Class<?>>();
			List<Class<?>> list = DiscoverableAnnotationIndexer.annotatedClassMap.getAnnotatedClasses(annotationClassName);
			
			if (list != null) {
				clazzList.addAll(list);
			}
			
			return clazzList;
			
		} catch (Exception ex) {
			throw new AnnotationIndexerException("Error getting annotated classes: " + ex.getMessage(), ex);
		}
	}
	
}

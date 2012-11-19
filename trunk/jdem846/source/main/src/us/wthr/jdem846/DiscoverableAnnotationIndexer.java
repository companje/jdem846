package us.wthr.jdem846;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import us.wthr.jdem846.annotations.Discoverable;
import us.wthr.jdem846.exception.AnnotationIndexerException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Creates an index of classes annotated with annotations that are annotated 
 * with the Discoverable annotation. annotation. ;-)
 * 
 * @author Kevin M. Gill
 *
 */
public class DiscoverableAnnotationIndexer
{
	private static Log log = Logging.getLog(DiscoverableAnnotationIndexer.class);
	
	private static Map<String, Set<String>> annotationIndex = null;
	private static AnnotatedClassMap annotatedClassMap = new AnnotatedClassMap();
	
	
	/** Index all the annotations automatically at first usage.
	 * 
	 */
	static {
		try {
			DiscoverableAnnotationIndexer.initialization();
		} catch (AnnotationIndexerException ex) {
			log.error("Failure in annotation indexer: " + ex.getMessage(), ex);
		}
	}
	
	protected DiscoverableAnnotationIndexer()
	{
		
	}
	
	protected static void initialization() throws AnnotationIndexerException
	{
		DiscoverableAnnotationIndexer indexer = new DiscoverableAnnotationIndexer();
		indexer.initDiscoverable();

	}
	
	protected void initDiscoverable() throws AnnotationIndexerException
	{	
		try {
			loadAnnotationIndex();
		} catch (Exception ex) {
			throw new AnnotationIndexerException("Failed to load annotation index: " + ex.getMessage(), ex);
		}
		
		Set<String> discoverableAnnotations = findDiscoverableAnnotations();
		
		if (discoverableAnnotations != null) {
			for (String clazzName : discoverableAnnotations) {
				log.info("Found Discoverable Annotation: " + clazzName);
				try {
					findClassesWithAnnotation(clazzName);
				} catch (Exception ex) {
					throw new AnnotationIndexerException("Error finding classes with annotation '" + clazzName + "': " + ex.getMessage(), ex);
				}
				
				
				
			}
		} else {
			this.log.warn("No discoverable annotations found!");
		}
		
	}
	
	protected void loadAnnotationIndex() throws Exception
	{
		log.info("Loading annotation index");
		
		AnnotationDB db = new AnnotationDB();

		File installPath = new File(System.getProperty("us.wthr.jdem846.installPath"));
		URL url = installPath.toURI().toURL();
		log.info("Scanning Classpath URL: " + url);
		StartupLoadNotifyQueue.add("Searching for modules in " + url);
		
		db.scanArchives(url);
			
			
		//URL[] urls = ClasspathUrlFinder.findClassPaths();
		//for (URL url : urls) {	
		//	log.info("Scanning Classpath URL: " + url);
		//	StartupLoadNotifyQueue.add("Searching for modules in " + url);
		//	db.scanArchives(url);
		//}
		//db.crossReferenceImplementedInterfaces();
		 	
		DiscoverableAnnotationIndexer.annotationIndex = db.getAnnotationIndex();
	}
	
	protected void findClassesWithAnnotation(String annotationClazzName) throws Exception
	{
		Set<String> annotatedClasses = annotationIndex.get(annotationClazzName);
		
		for (String clazzName : annotatedClasses) {
			log.info("	Found Annotated Class: " + clazzName);
			DiscoverableAnnotationIndexer.annotatedClassMap.addAnnotatedClass(annotationClazzName, clazzName);
		}
		
	}
	

	
	
	protected Set<String> findDiscoverableAnnotations()
	{
		log.info("Searching for discoverable type annotations");
		Set<String> discoverableAnnotations = DiscoverableAnnotationIndexer.annotationIndex.get(Discoverable.class.getName());
		
		return discoverableAnnotations;

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

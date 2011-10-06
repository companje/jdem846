package us.wthr.jdem846.annotation;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import us.wthr.jdem846.AnnotatedClassMap;
import us.wthr.jdem846.DiscoverableAnnotationIndexer;
import us.wthr.jdem846.RegistryKernel;
import us.wthr.jdem846.ServiceKernel;
import us.wthr.jdem846.annotations.Discoverable;
import us.wthr.jdem846.annotations.ElevationDataLoader;
import us.wthr.jdem846.exception.AnnotationIndexerException;
import us.wthr.jdem846.exception.ServiceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class DiscoverableAnnotationTesting
{
	private static Log log = Logging.getLog(DiscoverableAnnotationTesting.class);
	
	
	public static void main(String[] args)
	{
		//DiscoverableAnnotationTesting testing = new DiscoverableAnnotationTesting();
		//testing.initDiscoverable();
		
		
		try {
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		} catch (Exception ex) {
			log.error("Failed to initialize registry configuration: " + ex.getMessage(), ex);
			return;
		}
		
		
		
		try {
			ServiceKernel serviceKernel = new ServiceKernel();
			serviceKernel.initializeServices();
			
			
		} catch (ServiceException ex) {
			log.error("Failed to initialize services: " + ex.getMessage(), ex);
		}
		
	}
	
	
	public void initDiscoverable()
	{	
		
		List<String> classNameList = DiscoverableAnnotationIndexer.getAnnotatedClassList(ElevationDataLoader.class.getName());
		
		
		log.info("===============================================");

		for (String className : classNameList) {
			log.info("Indexer found: " + className);
		}

		
		try {
			List<Class<?>> clazzList = DiscoverableAnnotationIndexer.getAnnotatedClasses(ElevationDataLoader.class.getName());
			log.info("===============================================");
			
			for (Class<?> clazz : clazzList) {
				log.info("Indexer found and loaded: " + clazz.getName());
			}
			
		} catch (AnnotationIndexerException ex) {
			log.error("FAIL: " + ex.getMessage(), ex);
		}
		
		
		
	}


}

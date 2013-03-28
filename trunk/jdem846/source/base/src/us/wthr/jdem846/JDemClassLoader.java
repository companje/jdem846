package us.wthr.jdem846;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class JDemClassLoader extends ClassLoader
{
	private static Log log = Logging.getLog(JDemClassLoader.class);
	
	private File libPath;
	
	private ArrayList<JarFile> jarList;
	private Map<String, Object> classMap = new HashMap<String, Object>();

	protected JDemClassLoader(File libPath, ClassLoader parent)  throws Exception
	{
		super(parent);
		this.libPath = libPath;
		initialize();
	}
	
	protected void initialize() throws Exception
	{

	}
	
	@Override
	protected Class<?> findClass(String name)
	{
		if (classMap.containsKey(name)) {
			return (Class<?>) classMap.get(name);
		}
		
		try {
			for (JarFile jarFile : jarList) {
				Class<?> clazz = findClass(jarFile, name);
				if (clazz != null) {
					classMap.put(name, clazz);
					return clazz;
				}
			} 
		} catch (Exception ex) {
			return null;
		}
		
		return null;
	}

	protected Class<?> findClass(JarFile jarFile, String name) throws Exception
	{
		
		JarEntry entry = jarFile.getJarEntry(name + ".class");
		if (entry == null) {
			return null;
		}
		
		byte[] bytes = new byte[(int)entry.getSize()];
		InputStream in = jarFile.getInputStream(entry);
		in.read(bytes);
		
		Class<?> clazz = defineClass(name, bytes, 0, bytes.length, null);
		return clazz;
		
	}
	

	
	protected ArrayList<JarFile> getJarFiles() throws Exception
	{
		ArrayList<JarFile> jarFiles = new ArrayList<JarFile>();
		
		String[] jars = libPath.list(new FilenameFilter() {
			public boolean accept(File path, String file) {
				if (file.substring(file.length() - 4).equalsIgnoreCase(".jar"))
					return true;
				else
					return false;
			}
		});
		
		for (int i = 0; i < jars.length; i++) {
			
			File jar = new File(libPath, jars[i]);
			if (jar.exists()) {
				System.out.println("Adding " + jar.getPath());

				JarFile jarFile = new JarFile(jar);
				
				jarFiles.add(jarFile);
			}
		}
		
		
		return jarFiles;
		
	}
	
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		System.err.println("Loading class: " + name);

		try {
			return this.findSystemClass(name);
		} catch (Exception ex) { }
		
		Class<?> clazz = findClass(name);
		if (clazz == null) {
			throw new ClassNotFoundException("Class not found: " + name);
		} else {
			return clazz;
		}
		
		//return cl;
	}
	
	public void dump()
	{

		for (Package pack : this.getPackages()) {
			log.info("Package: " + pack.getName());
			
		}
		
	}
	
	
	public static ClassLoader createClassLoader(ClassLoader parent) throws Exception
	{

		return new JDemClassLoader(new File(System.getProperty("user.dir") + File.separator + "lib"), parent);
	}
	
	

}

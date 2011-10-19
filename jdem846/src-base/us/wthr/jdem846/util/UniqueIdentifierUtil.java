package us.wthr.jdem846.util;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Provides a means of obtaining a process-unique identifier string
 * 
 * @author Kevin M. Gill
 *
 */
public class UniqueIdentifierUtil
{
	private static Log log = Logging.getLog(UniqueIdentifierUtil.class);
	
	private static final List<String> identifierList = new LinkedList<String>();
	private static final int maxAttempts = 5;
	
	protected UniqueIdentifierUtil()
	{
		
	}
	
	/** Creates a process-unique identifier string.
	 * 
	 * @return A process-unique identifer string
	 */
	public static String getNewIdentifier()
	{
		String identifier = null;
		int checks = 0;
		
		do {
			identifier = UUID.randomUUID().toString();
			
			checks++;
			if (checks >= maxAttempts) { 
				// Does a check for max create iterations. 
				//Avoids a possible, however _really_ unlikely infinite loop
				identifier = null;
				break;
			}
		} while(UniqueIdentifierUtil.identifierExists(identifier, true));
		
		if (identifier != null) {
			log.info("Created process-unique identifier: " + identifier);
			log.info("Identifier List Size: " + UniqueIdentifierUtil.getIdentiferListSize());
		} else {
			log.warn("Failed to create process-unique identifier: max uniqueness checks reached (" + maxAttempts + ")");
			log.warn("Identifier List Size: " + UniqueIdentifierUtil.getIdentiferListSize());
		}
		
		return identifier;
	}
	
	/** Determines if the supplied identifier string already exists. Will not add it
	 * to the list if it is unique.
	 * 
	 * @param identifier Identifier string being checked for uniqueness
	 * @return True if the identifier is unique, otherwise false.
	 */
	public static boolean identifierExists(String identifier)
	{
		return UniqueIdentifierUtil.identifierExists(identifier, false);
	}
	
	/** Determines if the supplied identifier string already exists. If 'addIfUnique' is true
	 * and the identifier is new, then it will be added to the list.
	 * 
	 * @param identifier Identifier string being checked for uniqueness
	 * @param addIfUnique Add the identifier to the list if it is unique
	 * @return True if the identifier is unique, otherwise false.
	 */
	protected static boolean identifierExists(String identifier, boolean addIfUnique)
	{
		synchronized (UniqueIdentifierUtil.identifierList) {
			boolean exists = UniqueIdentifierUtil.identifierList.contains(identifier);
			if (!exists) {
				UniqueIdentifierUtil.identifierList.add(identifier);
			}
			return exists;
		}
	}
	
	protected static int getIdentiferListSize()
	{
		synchronized (UniqueIdentifierUtil.identifierList) {
			return UniqueIdentifierUtil.identifierList.size();
		}
	}
	
}

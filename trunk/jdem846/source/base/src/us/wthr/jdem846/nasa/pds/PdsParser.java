package us.wthr.jdem846.nasa.pds;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implements a _very_ simple PDS (Planetary Data System) label file parser.
 * 
 * @see http://pds.nasa.gov/documents/psdd/PSDDmain_1r71.pdf
 * @author Kevin M. Gill
 * 
 */
public class PdsParser
{
	private static final String QUOTED_STRING_REGEX = "[^\"]*\"([^\"]*)\"";
	private static final String CURLY_BRACE_SET_REGEX = "[^\\{]*\\{([^\\}]*)\\}";
	private static final String C_COMMENTS_REGEX = "\\/\\*+[0-9a-bA-Z(). ].*\\*\\/+"; // TODO: Complete this
	private static final String SET_IDENTIFIER_PREFIX = "@set:";
	private static final String STRING_IDENTIFIER_PREFIX = "@str:";
	private static final String NULL_VALUE = "N/A";

	private Queue<PdsLine> lineQueue = Lists.newLinkedList();
	private Map<String, String> quotedStrings = null;
	private Map<String, StringSet> stringSets = null;

	private File pdsFile;
	
	public PdsObjectMap parse(String pdsFilePath) throws Exception
	{
		pdsFile = new File(pdsFilePath);
		
		String contents = loadFileContents(pdsFile);
		contents = stripCTypeComments(contents);

		// Build map of quoted strings keyed by unique identifiers
		quotedStrings = buildQuotedStringMap(contents);

		// Replace quoted strings in content with their identifiers.
		for (String identifier : quotedStrings.keySet()) {
			String quotedString = quotedStrings.get(identifier);
			contents = contents.replace('\"' + quotedString + '\"', identifier);
		}

		// Build map of value sets keyed by unique identifiers;
		stringSets = buildStringSetMap(contents);

		// Replace sets in content with their identifiers.
		for (String identifier : stringSets.keySet()) {
			StringSet stringSet = stringSets.get(identifier);
			contents = contents.replace('{' + stringSet.raw + '}', identifier);
		}

		// Split content into lines and add all (except blank lines) to line
		// queue
		String[] lines = contents.split("\n");
		for (String line : lines) {
			if (line != null && line.length() > 0) {
				lineQueue.add(new PdsLine(line));
			}
		}

		PdsObjectMap root = readObject();
		return root;
	}

	protected static Map<String, String> buildQuotedStringMap(String content)
	{
		return buildStringMap(content, QUOTED_STRING_REGEX, STRING_IDENTIFIER_PREFIX);
	}

	protected static Map<String, StringSet> buildStringSetMap(String content)
	{
		Map<String, String> stringSetRaw = buildStringMap(content, CURLY_BRACE_SET_REGEX, SET_IDENTIFIER_PREFIX);

		Map<String, StringSet> stringSetMap = Maps.newHashMap();

		for (String identifier : stringSetRaw.keySet()) {
			StringSet stringSet = new StringSet();
			stringSet.raw = stringSetRaw.get(identifier);
			String[] stringSetArray = stringSet.raw.split(",");
			for (String member : stringSetArray) {
				member = member.trim();
				stringSet.set.add(member);
			}
			stringSetMap.put(identifier, stringSet);
		}

		return stringSetMap;
	}

	protected static Map<String, String> buildStringMap(String content, String regex, String idPrefix)
	{
		Map<String, String> stringMap = Maps.newHashMap();
		Matcher m = Pattern.compile(regex).matcher(content);
		while (m.find()) {
			if (m.group(0) != null) {
				String identifier = idPrefix + generateUniqueIdentifier();
				stringMap.put(identifier, m.group(1));
			}
		}

		return stringMap;
	}

	protected PdsObjectMap readObject()
	{

		String objectName = null;

		PdsLine line = readLine();
		if (line.getField().equals("object")) {
			objectName = line.getValue();
			line = readLine();
		}
		PdsObjectMap pdsObject = new PdsObjectMap(objectName);

		while (!line.getField().equals("end") && !line.getField().equals("end_object")) {

			if (quotedStrings.containsKey(line.getValue())) {
				line.checkValueIdentifier(quotedStrings);
			}

			if (line.getField().charAt(0) == '^') {
				pdsObject.setFile(new File(pdsFile.getParentFile(), line.getValue()));
			} else {

				pdsObject.setField(line.getField(), getPdsField(line.getValue()));
			}

			PdsLine peekLine = peekLine();
			if (peekLine.getField().equals("object")) {
				PdsObjectMap subObject = readObject();
				subObject.addSubObject(subObject);
			} else {
				line = readLine();
			}

		}

		return pdsObject;
	}

	public PdsFieldValue<?> getPdsField(String value)
	{
		PdsFieldValue<?> field = null;
		if (value == null) {
			return null;
		}

		if (value.startsWith(SET_IDENTIFIER_PREFIX)) {

			Set<PdsFieldValue<?>> set = Sets.newHashSet();
			for (String setValue : stringSets.get(value).set) {
				set.add(getPdsField(setValue));
			}
			field = new PdsFieldValue<Set<PdsFieldValue<?>>>(set);
		} else {
			if (value.startsWith(STRING_IDENTIFIER_PREFIX)) {
				value = quotedStrings.get(value);
			}
			if (value != null && (value.equals(NULL_VALUE) || value.equals("'" + NULL_VALUE + "'"))) {
				value = null;
			}
			
			field = new PdsFieldValue<String>(value);
		}
		return field;
	}

	protected boolean hasMoreLines()
	{
		return lineQueue.size() > 0;
	}

	protected PdsLine peekLine()
	{
		return lineQueue.peek();
	}

	protected PdsLine readLine()
	{
		return lineQueue.poll();
	}

	protected static String generateUniqueIdentifier()
	{
		return UUID.randomUUID().toString();
	}

	/**
	 * Strips C/C++ style slash-star/star-slash comments from the string.
	 * 
	 * @param contents
	 * @return
	 */
	protected static String stripCTypeComments(String contents)
	{
		contents = contents.replaceAll(C_COMMENTS_REGEX, "");
		return contents;
	}

	/**
	 * Loads contentds of the file into a string.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	protected static String loadFileContents(File pdsFile) throws IOException
	{
		FileInputStream in = new FileInputStream(pdsFile);
		List<String> lines = IOUtils.readLines(in);
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line + "\n");
		}
		String contents = sb.toString();
		return contents;
	}

	private static class StringSet
	{
		public String raw;
		public Set<String> set = Sets.newHashSet();;

		public StringSet()
		{

		}
	}
}

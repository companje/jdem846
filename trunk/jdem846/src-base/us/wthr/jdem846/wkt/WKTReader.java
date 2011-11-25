package us.wthr.jdem846.wkt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class WKTReader
{
	public static final String EMPTY = "EMPTY";
	public static final String L_PAREN = "(";
	public static final String R_PAREN = ")";
	public static final String COMMA = ",";
	public static final String L_BRACKET = "[";
	public static final String R_BRACKET = "]";
	
	private static Log log = Logging.getLog(WKTReader.class);
	
	private String url = null;
	private InputStream in = null;
	private StreamTokenizer tokenizer = null;
	
	protected WKTReader(String url)
	{
		this.url = url;
	}
	
	public void open() throws Exception
	{
		in = JDemResourceLoader.getAsInputStream(url);
		Reader r = new BufferedReader(new InputStreamReader(in));
		tokenizer = new StreamTokenizer(r);
		
		tokenizer.resetSyntax();
		tokenizer.wordChars('a', 'z');
		tokenizer.wordChars('A', 'Z');
		tokenizer.wordChars(128 + 32, 255);
		tokenizer.wordChars('0', '9');
		tokenizer.wordChars('-', '-');
		tokenizer.wordChars('+', '+');
		tokenizer.wordChars('.', '.');
		tokenizer.wordChars('_', '_');
		tokenizer.whitespaceChars(0, '\"');
		tokenizer.commentChar('#');
	}
	
	public void parse() throws Exception
	{
		// TODO: Check file is open first
		
		Tag top = new Tag("TOP");
		parseTag(top);
		
		
	}
	
	
	protected void parseTag(Tag parent) throws Exception
	{
		while (!isEOF() && !peekNextWord().equals(WKTReader.R_BRACKET)) {
			String word = nextWord();
				
			if (isKeyword(word)) {
				Tag tag = new Tag(word);
				if (!nextWord().equals(WKTReader.L_BRACKET)) {
					// TODO: throw!!!
				}
					
				log.info("Keyword: " + word);
				parseTag(tag);
				parent.values.add(tag);
			} else if (!word.equals(WKTReader.COMMA)){
				log.info("Value: " + word);
			}

		}
		
		if (!isEOF() && peekNextWord().equals(WKTReader.R_BRACKET))
			nextWord();
	}
	
	protected boolean isEOF() throws Exception
	{
		int type = tokenizer.nextToken();
		tokenizer.pushBack();
		if (type == StreamTokenizer.TT_EOF) {
			return true;
		} else {
			return false;
		}
	}
	
	protected double nextDouble() throws Exception 
	{
		String word = nextWord();
		if (word == null) {
			return 0; // Throw!!
		}
		try {
			double value = Double.parseDouble(word);
			return value;
		} catch (Exception ex) {
			return 0; // Throw!!
		}
	}
	
	protected String peekNextWord() throws Exception
	{
		String word = nextWord();
		tokenizer.pushBack();
		return word;
	}
	
	protected String nextWord() throws Exception
	{
		int type = tokenizer.nextToken();
		
		switch (type) {
			case StreamTokenizer.TT_WORD:
				String token = tokenizer.sval;
				if (token.equalsIgnoreCase(WKTReader.EMPTY)) {
					return WKTReader.EMPTY;
				} else {
					return token;
				}
			case '(':
				return WKTReader.L_PAREN;
			case ')':
				return WKTReader.R_PAREN;
			case ',':
				return WKTReader.COMMA;
			case '[':
				return WKTReader.L_BRACKET;
			case ']':
				return WKTReader.R_BRACKET;
			default:
				log.info("Type: Unknown: " + type);
				return null;
		}
	}
	
	protected boolean isKeyword(String word)
	{
		for (String keyword : keyWords) {
			if (keyword.equalsIgnoreCase(word))
				return true;
		}
		return false;
	}
	
	
	public void close() throws Exception
	{
		if (in != null) {
			in.close();
			in = null;
		}
	}
	
	
	public static WKTReader load(String url) throws Exception
	{
		WKTReader reader = new WKTReader(url);
		reader.open();
		reader.parse();
		reader.close();
		return reader;
	}
	
	
	protected static final String[] keyWords = 
		{
			"PROJCS",
			"GEOGCS",
			"DATUM",
			"SPHEROID",
			"PRIMEM",
			"UNIT",
			"PROJECTION",
			"PARAMETER",
			"UNIT",
			"AXIS",
			"AUTHORITY",
			"VERT_CS",
			"VERT_DATUM",
			"COMPD_CS"
		};
	
	private class Tag
	{
		public String keyword;
		public List<Tag> values = new LinkedList<Tag>();
		
		public Tag()
		{
			
		}
		
		public Tag(String keyword)
		{
			this.keyword = keyword;
		}
	}
}
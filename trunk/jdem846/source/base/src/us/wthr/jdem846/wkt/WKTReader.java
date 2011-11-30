package us.wthr.jdem846.wkt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.exception.WKTParseException;
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
	
	public static final int VALUE = 0;
	public static final int TAG = 1;
	
	private static Log log = Logging.getLog(WKTReader.class);
	
	private String url = null;
	private InputStream in = null;
	private StreamTokenizer tokenizer = null;
	
	protected WKTReader(String url)
	{
		this.url = url;
	}
	
	public void open() throws IOException
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
	
	public void parse() throws WKTParseException
	{
		// TODO: Check file is open first
		
		WKTElement top = new WKTElement("TOP", TAG);
		
		try {
			parseTag(top);
		} catch (IOException ex) {
			throw new WKTParseException("IO Error loading WKT file: " + ex.getMessage(), ex);
		} // Let WKTParseException fly!
		
	}
	
	
	protected void parseTag(WKTElement parent) throws IOException, WKTParseException
	{
		while (!isEOF() && !peekNextWord().equals(WKTReader.R_BRACKET)) {
			String word = nextWord();
				
			if (isKeyword(word)) {
				WKTElement tag = new WKTElement(word, TAG);
				if (!nextWord().equals(WKTReader.L_BRACKET)) {
					// TODO: throw!!!
				}
					
				log.info("Keyword: " + word);
				parseTag(tag);
				parent.values.add(tag);
			} else if (!word.equals(WKTReader.COMMA)){
				log.info("Value: " + word);
				WKTElement value = new WKTElement(word, VALUE);
				parent.values.add(value);
			}

		}
		
		if (!isEOF() && peekNextWord().equals(WKTReader.R_BRACKET))
			nextWord();
	}
	
	protected boolean isEOF() throws IOException
	{
		int type = tokenizer.nextToken();
		tokenizer.pushBack();
		if (type == StreamTokenizer.TT_EOF) {
			return true;
		} else {
			return false;
		}
	}
	
	protected double nextDouble() throws IOException, WKTParseException 
	{
		String word = nextWord();
		if (word == null) {
			return 0; // Throw!!
		}
		try {
			double value = Double.parseDouble(word);
			return value;
		} catch (Exception ex) {
			throw new WKTParseException("Invalid text '" + word + "', expected double; " + ex.getMessage(), ex);
		}
	}
	
	protected String peekNextWord() throws IOException, WKTParseException
	{
		String word = nextWord();
		tokenizer.pushBack();
		return word;
	}
	
	protected String nextWord() throws IOException, WKTParseException
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
				throw new WKTParseException("Unknown WKT field type: " + type);
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
	
	
	public void close() throws IOException
	{
		if (in != null) {
			in.close();
			in = null;
		}
	}
	
	
	public static WKTReader load(String url) throws WKTParseException
	{
		WKTReader reader = new WKTReader(url);
		try {
			reader.open();
		} catch (Exception ex) {
			throw new WKTParseException("Failed to open WKT file: " + ex.getMessage(), ex);
		}
			
		reader.parse();
		
		try {
			reader.close();
		} catch (Exception ex) {
			throw new WKTParseException("Failed to close WKT file: " + ex.getMessage(), ex);
		}
		
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
	
	private class WKTElement
	{
		
		
		public int type;
		public String word;
		public List<WKTElement> values = new LinkedList<WKTElement>();
		
		public WKTElement()
		{
			
		}
		
		public WKTElement(String word, int type)
		{
			this.word = word;
		}
	}
}
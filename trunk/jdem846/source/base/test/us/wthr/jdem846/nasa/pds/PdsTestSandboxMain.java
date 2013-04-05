package us.wthr.jdem846.nasa.pds;

import java.util.Map;
import java.util.Queue;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.DiscoverableAnnotationIndexer;
import us.wthr.jdem846.exception.AnnotationIndexerException;
import us.wthr.jdem846.logging.Log;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PdsTestSandboxMain extends AbstractTestMain
{
	private static Log log = null;
	
	private Queue<PdsLine> lineQueue = Lists.newLinkedList();
	private Map<String, String> quotedStrings = Maps.newHashMap();

	public static void main(String[] args)
	{

		try {
			AbstractTestMain.initialize(true);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		
		PdsTestSandboxMain testing = new PdsTestSandboxMain();
		
		try {
			DiscoverableAnnotationIndexer.createIndex();
		} catch (AnnotationIndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//String testFile = "C:\\Users\\kgill\\Google Drive\\JDem846\\Data\\Mercury\\messenger\\gdr\\img\\hdem_2.lbl";
		//String testFile = "C:\\Users\\GillFamily\\Google Drive\\JDem846\\Data\\Mercury\\messenger\\gdr\\img\\hdem_2.lbl";
		//String testFile = "C:\\Users\\GillFamily\\Google Drive\\JDem846\\Filetype Specifications\\datadictionary_1r89\\pdsdd.full";
		String testFile = "C:\\Users\\kgill\\Google Drive\\JDem846\\Filetype Specifications\\datadictionary_1r89\\pdsdd.full";
		
		try {
			testing.parse(testFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void parse(String testFile) throws Exception
	{
		PdsParser parser = new PdsParser();
		PdsObjectMap root = parser.parse(testFile);
		
		for (PdsObjectMap objectMap : root.getSubObjects()) {
			System.err.println("Object Type: " + objectMap.getType());
		}
		
	}

	/*
	 * public static final String EMPTY = "EMPTY"; public static final String
	 * L_PAREN = "("; public static final String R_PAREN = ")"; public static
	 * final String COMMA = ","; public static final String L_BRACKET = "[";
	 * public static final String R_BRACKET = "]"; public static final String
	 * QUOTE = "\""; public static final String L_CURLY = "{"; public static
	 * final String R_CURLY = "}"; public static final String EQUALS = "=";
	 * 
	 * StreamTokenizer tokenizer;
	 * 
	 * 
	 * 
	 * 
	 * public void test() throws Exception {
	 * 
	 * String testFile =
	 * "C:\\Users\\kgill\\Google Drive\\JDem846\\Data\\Mercury\\messenger\\gdr\\img\\hdem_2.lbl"
	 * ; //StreamTokenizer
	 * 
	 * String contents = loadFileContents(testFile); contents =
	 * stripCTypeComments(contents);
	 * 
	 * //System.err.println(contents); Reader r = new StringReader(contents);
	 * 
	 * tokenizer = new StreamTokenizer(r);
	 * 
	 * tokenizer.resetSyntax();
	 * 
	 * tokenizer.wordChars('a', 'z'); tokenizer.wordChars('A', 'Z');
	 * tokenizer.wordChars(128 + 32, 255); tokenizer.wordChars('0', '9');
	 * tokenizer.wordChars('-', '-'); tokenizer.wordChars('+', '+');
	 * tokenizer.wordChars('*', '*'); tokenizer.wordChars('.', '.');
	 * tokenizer.wordChars('_', '_'); tokenizer.wordChars('^', '^');
	 * tokenizer.wordChars(':', ':'); tokenizer.wordChars('<', '<');
	 * tokenizer.wordChars('>', '>'); //tokenizer.wordChars('\"', '\"');
	 * //tokenizer.quoteChar('\"'); tokenizer.wordChars('\'', '\'');
	 * tokenizer.wordChars('/', '/'); tokenizer.whitespaceChars(0, '!');
	 * tokenizer.slashSlashComments(true); tokenizer.slashStarComments(true);
	 * 
	 * while (!isEOF()) { String word = nextWord();
	 * 
	 * if (word.equals(PdsTestSandboxMain.QUOTE)) { word = readQuotedString(); }
	 * 
	 * System.out.println("WORD: " + word); } r.close();
	 * 
	 * }
	 * 
	 * protected String readQuotedString() throws Exception { StringBuilder sb =
	 * new StringBuilder(); while (!isEOF()) { String word = nextWord();
	 * 
	 * if (word.equals(PdsTestSandboxMain.QUOTE)) { break; } else {
	 * sb.append(word); }
	 * 
	 * } return sb.toString(); }
	 * 
	 * 
	 * 
	 * 
	 * protected boolean isEOF() throws IOException { int type =
	 * tokenizer.nextToken(); tokenizer.pushBack(); if (type ==
	 * StreamTokenizer.TT_EOF) { return true; } else { return false; } }
	 * 
	 * protected String peekNextWord() throws Exception { String word =
	 * nextWord(); tokenizer.pushBack(); return word; }
	 * 
	 * protected String nextWord() throws Exception { int type =
	 * tokenizer.nextToken();
	 * 
	 * switch (type) { case StreamTokenizer.TT_WORD: String token =
	 * tokenizer.sval; if (token.equalsIgnoreCase(PdsTestSandboxMain.EMPTY)) {
	 * return PdsTestSandboxMain.EMPTY; } else { return token; } case '(':
	 * return PdsTestSandboxMain.L_PAREN; case ')': return
	 * PdsTestSandboxMain.R_PAREN; case ',': return PdsTestSandboxMain.COMMA;
	 * case '[': return PdsTestSandboxMain.L_BRACKET; case ']': return
	 * PdsTestSandboxMain.R_BRACKET; case '{': return
	 * PdsTestSandboxMain.L_CURLY; case '}': return PdsTestSandboxMain.R_CURLY;
	 * case '=': return PdsTestSandboxMain.EQUALS; case '\"': return
	 * PdsTestSandboxMain.QUOTE; default: throw new
	 * WKTParseException("Unknown PDS field type: " + type); } }
	 */

}

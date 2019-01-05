/*
 * Copyright (C) 2018  Magnus Bull
 *
 *  This file is part of jeximel.
 *
 *  jeximel is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  jeximel is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with jeximel.  If not, see <https://www.gnu.org/licenses/>. 
 */
package net.buttology.util.jeximel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * This utility class can read and write XML files. When reading XML files, 
 * it returns a Document containing the XML data. Similarly, writing an 
 * XML file takes a Document as input and writes the contents to a file on disk.
 * @version 1.2.0
 * @author Mudbill
 */
public class XMLParser {

	/** Use this export option to write newline characters for each attribute in an inline element. Does not affect elements with children or text. */
	public static final int OPTION_ATTR_NEWLINE_INLINE = 0x1;
	/** Use this export option to write newline characters for each attribute an element has, regardless of context. */
	public static final int OPTION_ATTR_NEWLINE_ALL = 0x2;
	
	/** Change this to true to print debug messages in the standard output. */
	public static boolean debug = false;
	private static int _indentCount = 0;
	
	/**
	 * Read an XML document from the given input stream using the system's default underlying charset.
	 * @param is - The input stream used to read the file
	 * @return
	 * @throws XMLException
	 */
	public static Document read(InputStream is) throws XMLException {
		return read(is, null);
	}
	
	/**
	 * Read an XML document from the given input stream.
	 * @param is - The input stream used to read the file
	 * @param charset - The charset used to parse characters.
	 * @return
	 * @throws XMLException
	 */
	public static Document read(InputStream is, String charset) throws XMLException
	{
		debug("Reading XML file...");
		long startTime = System.currentTimeMillis();
		
		XMLParser x = new XMLParser();
		x.prepare();
		String content = x.readStream(is, charset);
		debug("Finished reading XML file in %d ms.", System.currentTimeMillis() - startTime);
		
		debug("Parsing XML data...");
		startTime = System.currentTimeMillis();
		
		if (content.isEmpty()) throw new XMLException("File is empty.");
		
		try
		{
			x.parse(content);
		}
		catch (NullPointerException npe)
		{
			throw new XMLException("Failed parsing contents of file, is it valid XML?");
		}
		
		debug("Finished parsing XML data in %d ms.", System.currentTimeMillis() - startTime);
		
		Document d = x._root;
		x = null;
		return d;
	}
	
	/**
	 * Write the given XML document to the given output stream, with options.
	 * @param document
	 * @param os
	 * @param options - An option value from this class that specifies export parameters.
	 * @throws XMLException
	 */
	public static void write(Document document, OutputStream os, String charset, int options) throws XMLException
	{
		_indentCount = 0;
		debug("Writing XML document to file...");
		long startTime = System.currentTimeMillis();
		
		try
		{			
			OutputStreamWriter osw = charset != null 
					? new OutputStreamWriter(os, charset) 
					: new OutputStreamWriter(os);
			
			String version = document.getVersion();
			String encoding = document.getEncoding();
			boolean standalone = document.getStandalone();
			if (version != null || encoding != null || !standalone)
			{
				String declaration = "<?xml";
				if (version != null) declaration += " version=\"" + version + "\"";
				if (encoding != null) declaration += " encoding=\"" + encoding + "\"";
				if (!standalone) declaration += " standalone=\"" + standalone + "\"";
				declaration += " ?>";
				debug("Writing declaration: " + declaration);
				osw.write(declaration);
				osw.write('\n');
			}
						
			for (Element e : document.getChildren())
			{
				writeElement(e, osw, options);
			}
			osw.close();
		}
		catch (IOException e)
		{
			throw new XMLException("Failed to write XML file.", e);
		}
		
		debug("Finished writing XML file in %d ms.", (System.currentTimeMillis() - startTime));
	}
	
	/**
	 * Write the given XML document to the given output stream.
	 * @param document
	 * @param os
	 * @throws XMLException
	 */
	public static void write(Document document, OutputStream os, String charset) throws XMLException
	{
		write(document, os, charset, 0);
	}
	
	/**
	 * Write the given XML document to the given output stream using the default charset.
	 * @param document
	 * @param os
	 * @throws XMLException
	 */
	public static void write(Document document, OutputStream os) throws XMLException
	{
		write(document, os, null, 0);
	}
	
	private static void writeElement(Element e, OutputStreamWriter osw, int options) throws IOException
	{
		boolean optionAttrNewline = (options & OPTION_ATTR_NEWLINE_INLINE) == OPTION_ATTR_NEWLINE_INLINE;
		boolean optionAttrNewlineAll = (options & OPTION_ATTR_NEWLINE_ALL) == OPTION_ATTR_NEWLINE_ALL;

		String tabs = getTabs();
		String tag = tabs + "<" + e.getName();
		for(String s : e.getAttributes().keySet())
		{
			if ((optionAttrNewline && !e.hasChildren() && !e.hasText()) || optionAttrNewlineAll)
				tag += "\n" + tabs + "\t";
			else
				tag += " ";
			tag += s + "=\"" + e.getAttribute(s) + "\"";
		}
		
		if (!e.hasChildren())
		{
			if (!e.hasText())
			{
				if ((optionAttrNewline || optionAttrNewlineAll) && e.hasAttributes())
					tag += "\n" + tabs;
				else
					tag += " ";
				tag += "/>\n";
			}
			else
			{
				tag += ">" + formatTextToXml(e.getText()) + "</" + e.getName() + ">\n";
			}
		}
		else
		{
			_indentCount++;
			if ((optionAttrNewline && !e.hasChildren() && !e.hasText()) || optionAttrNewlineAll && e.hasAttributes())
				tag += "\n" + tabs;
			tag += ">\n";
		}
		osw.write(tag);
		for (Element child : e.getChildren())
		{
			writeElement(child, osw, options);
		}
		if (e.hasChildren())
		{
			_indentCount--;
			osw.write(tabs + "</" + e.getName() + ">\n");
		}
	}
	
	private static String getTabs()
	{
		String tab = "";
		for (int i = 0; i < _indentCount; i++)
			tab += "\t";
		return tab;
	}
	
	private static String formatTextFromXml(String text)
	{
		return text
				.replace("&quot;", "\"")
				.replace("&amp;", "&")
				.replace("&apos;", "'")
				.replace("&lt;", "<")
				.replace("&gt;", ">");
	}
	
	private static String formatTextToXml(String text)
	{
		return text
				.replace("\"", "&quot;")
				.replace("&", "&amp;")
				.replace("'", "&apos;")
				.replace("<", "&lt;")
				.replace(">", "&gt;");
	}
	
	private XMLParser() {}
	
	private Document 	_root;
	private Element 	_parent;
	private boolean		_isComment = false;
	
	private String readStream(InputStream is, String charset) throws NullPointerException, XMLException
	{
		if(is == null) throw new NullPointerException("Input stream cannot be null.");
		
		Scanner scanner;
		if(charset != null) scanner = new Scanner(is, charset);
		else scanner = new Scanner(is);
		
		StringBuilder sb = new StringBuilder();

		try
		{			
			while (scanner.hasNextLine())
			{
				sb.append(scanner.nextLine());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{			
			scanner.close();
		}
		
		String content = sb.toString().trim();
		return content;
	}
	
	private int _parseStart = 0;
	private int _parseStop = 0;

	private void parse(String content) throws XMLException
	{
		String tag = getNextTag(content);
		
		// Check for a declaration and process it if found.
		if (tag.charAt(0) == '?')
		{
			if (tag.startsWith("?xml")
			&& tag.endsWith("?"))
			{				
				debug("Declaration line: " + tag);
				this.processDeclaration(tag);
				tag = getNextTag(content);
			}
			else
			{
				throw new XMLException("Malformed declaration.");
			}
		}
		
		// Loop through all tags.
		do
		{
			Element e = processElementFromString(tag);
			String text;
			text = getText(content).trim();
			if (!text.isEmpty())
			{
//				debug("\tTEXT ("+_parseStop+"): " + text);
				e.setText(text);
			}
		}
		while ((tag = getNextTag(content)) != null);
	}
	
	private String getText(String content) throws XMLException
	{
		try
		{
			_parseStart = _parseStop + 1;
			int parseStop = content.indexOf('<', _parseStart);
//			System.out.println(_parseStart + "-" + parseStop);
			if (parseStop == -1 || _parseStart == -1 || parseStop <= _parseStart) return "";
			String text = content.substring(_parseStart, parseStop);
			return formatTextFromXml(text);
		}
		catch (Exception e)
		{
			throw new XMLException("A parsing error occurred.");
		}
	}
	
	private String getNextTag(String content) throws XMLException
	{
		try
		{
			if (_parseStart == -1 || _parseStop == -1)
				return null;
			
			if (_isComment)
			{
				_parseStop = content.indexOf("-->", _parseStart);
				_isComment = false;
			}
			
			_parseStart = content.indexOf('<', _parseStop);
			_parseStop = content.indexOf('>', _parseStart);
			if (_parseStop == -1 || _parseStart == -1 || _parseStop <= _parseStart)
				return null;
			String tag = content.substring(_parseStart + 1, _parseStop);
			return tag;
		}
		catch (Exception e)
		{
			throw new XMLException("A parsing error occurred.");
		}
	}
	
	private Element processElementFromString(String raw) throws XMLException
	{
		try
		{
			// Check for comments
			if (raw.startsWith("!--"))
			{
				if (raw.endsWith("--"))
				{
					debug("Encountered an in-line comment, ignoring.");
					return null;
				}
				_isComment = true;
				debug("Encountered a multi-line comment, ignoring.");
				return null;
			}
			
			int lowestIdx = Integer.MAX_VALUE;
			int nextWhitespace = getNextWhitespaceIndex(raw, 0);
			if (nextWhitespace != -1)
				lowestIdx = nextWhitespace;
			
			String name = raw.substring(0, Math.min(lowestIdx, raw.length()));
			
			if (name.charAt(0) == '/')
			{
				// This is a closing element, so go up one level in the hierarchy.
				_parent = _parent.getParent();
				return _parent;
			}
			else
			{
				// This is a new opening element
				Element element = new Element(_parent, name);
				if (raw.charAt(raw.length() - 1) != '/')
					// This is not an in-line element, so increment the hierarchy level.
					_parent = element;
				Map<String, String> attributes = parseAttributes(raw);
				if (attributes != null)
				{
					debug("Setting attributes: " + attributes);
					element.setAttributes(attributes);
				}
				return element;
			}
		}
		catch (Exception e)
		{
			throw new XMLException("Failed parsing element from XML data.", e);
		}
	}
	
	private Map<String, String> parseAttributes(String raw)
	{
		if (raw.trim().isEmpty()) return null;
		Map<String, String> attribs = new HashMap<String, String>();
		int parseStart = 0;
		int parseStop = 0;
		do
		{
			parseStart = this.getNextWhitespaceIndex(raw, parseStop);
			parseStop = raw.indexOf('=', parseStart);
			if (parseStart == -1 || parseStop == -1)
				break;
			String attribName = raw.substring(parseStart, parseStop).trim();
			parseStart = raw.indexOf('"', parseStop) + 1;
			parseStop = raw.indexOf('"', parseStart);
			String attribValue = raw.substring(parseStart, parseStop);
			debug("Found attrib %s=%s", attribName, attribValue);
			attribs.put(attribName, attribValue);
		}
		while (raw.indexOf('=', parseStop) != -1);
		if (attribs.isEmpty()) 
			return null;
		return attribs;
	}
	
	private int getNextWhitespaceIndex(String raw, int startIndex)
	{
		int idxSpace = raw.indexOf(' ', startIndex);
		int idxTab = raw.indexOf('\t', startIndex);
		int idxNl = raw.indexOf('\n', startIndex);
		if (idxSpace == -1) idxSpace = raw.length();
		if (idxTab == -1) idxTab = raw.length();
		if (idxNl == -1) idxNl = raw.length();
		int idx = Math.min(idxSpace, Math.min(idxTab, idxNl));
		return idx;
	}
	
	private void processDeclaration(String raw) throws XMLException
	{
		Map<String, String> declar = parseAttributes(raw);
		if (declar != null)
		{
			if (declar.containsKey("version")) this._root.setVersion(declar.get("version"));
			if (declar.containsKey("encoding")) this._root.setEncoding(declar.get("encoding"));
			if (declar.containsKey("standalone")) this._root.setStandalone(Boolean.parseBoolean(declar.get("standalone")));
		}
	}
	
	private void prepare()
	{
		_root = new Document();
		_parent = _root.getDocumentElement();
	}

	private static void debug(String msg, Object... args)
	{
		if (debug) System.out.printf("XML: " + msg + "\n", args);
	}
}

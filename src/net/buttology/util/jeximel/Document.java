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

/**
 * This class represents an entire XML document, including the XML declaration if 
 * present and all the child elements that come directly under the top level. 
 * Elements can be added, modified and removed at request.
 * @version 1.0.1
 * @author Mudbill
 */
public class Document
{
	private Element root;
	private String version;
	private String encoding;
	private boolean standalone = true;
	
	/**
	 * Creates a blank XML document.
	 */
	public Document() {
		this.root = new Element("_ROOT");
	}
	
	/**
	 * Add an element to the document.
	 * @param child
	 */
	public void addChild(Element child) {
		this.root.addChild(child);
	}
	
	/**
	 * Add an element to the document.
	 * @param child
	 */
	public void addChild(String child) {
		this.root.addChild(child);
	}
	
	/**
	 * Get an element from the document.
	 * @param name
	 * @return
	 */
	public Element getChild(String name) {
		return this.root.getChild(name);
	}
	
	/**
	 * Get an array of all elements from the document.
	 * @return
	 */
	public Element[] getChildren() {
		return this.root.getChildren();
	}
	
	/**
	 * Get an array of all elements from the document with the given name.
	 * @param name
	 * @return
	 */
	public Element[] getChildren(String name) {
		return this.root.getChildren(name);
	}
	
	/**
	 * Remove the first element from the document with the given name.
	 * @param name
	 */
	public void removeChild(String name) {
		this.root.removeChild(name);
	}

	/**
	 * Get the XML document version.
	 * @return
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Set the XML document version.
	 * @param version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Get the XML document encoding.
	 * @return
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Set the XML document encoding.
	 * @param encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Get the XML document stand-alone state.
	 * @return
	 */
	public boolean getStandalone() {
		return standalone;
	}

	/**
	 * Set the XML document stand-alone state.
	 * @param standalone
	 */
	public void setStandalone(boolean standalone) {
		this.standalone = standalone;
	}
	
	Element getDocumentElement() {
		return root;
	}

}

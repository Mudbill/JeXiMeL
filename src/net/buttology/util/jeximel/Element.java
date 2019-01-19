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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents an element in the XML tree hierarchy. An element can have a series of attributes,
 * as well as child elements. Elements can also contain text nodes.
 * @version 1.0.2
 * @author Mudbill
 */
public class Element {
	
	private int _id = -1;
	private boolean _debug = XMLParser.debug;
	private String name;
	private String text;
	private Element parent;
	private Map<Integer, Element> children;
	private Map<String, String> attributes;
	
	/**
	 * Create a new, orphaned element with the given name.
	 * @param name
	 */
	public Element(String name) {
		this(null, name);
	}
	
	/**
	 * Create a new element with the given parent and name.
	 * @param parent
	 * @param name
	 */
	public Element(Element parent, String name) {
		this.parent = parent;
		this.name = name;
		this.children = new HashMap<Integer, Element>();
		this.attributes = new HashMap<String, String>();
		if (parent != null) {
			this._id = parent.children.size();
			while(parent.children.containsKey(_id)) {
				_id++;
			}
			parent.addChild(this);
		}
		debug("Created element '%s' with id '%d'", name, _id);
	}
	
	/**
	 * Add a child element to this element.
	 * @param child
	 * @return
	 */
	public Element addChild(Element child) {
//		child.setParent(this);
		child._id = children.size();
		while(children.containsKey(child._id)) {
			child._id++;
		}
		children.put(child._id, child);
		debug("Adding '%s' [ID: %d] to '%s'", child.name, child._id, this.name);
		return child;
	}
	
	/**
	 * Add a child element to this element.
	 * @param name
	 * @return
	 */
	public Element addChild(String name) {
		Element child = new Element(name);
		return addChild(child);
	}
	
	/**
	 * Check if this element has a child element with the given name.
	 * @param name
	 * @return
	 */
	public boolean hasChild(String name) {
		for (Element e : children.values()) {
			if (e.getName().equals(name)) return true;
		}
		return false;
	}
	
	/**
	 * Get a child element with the given name.
	 * @param name
	 * @return
	 */
	public Element getChild(String name) {
		for (Element e : children.values()) {
			if (e.getName().equals(name)) return e;
		}
		debug("No '%s' child found in '%s'", name, this.name);
		return null;
	}
	
	/**
	 * Get a child element which has an attribute matching the given value.
	 * @param attrib
	 * @return
	 */
	public Element getChildByAttrib(String attrib) {
		for (Element e : children.values()) {
			if (e.getAttributes().containsKey(attrib)) return e;
		}
		debug("No child with attrib '%s' found in '%s'", attrib, this.name);
		return null;
	}
	
	/**
	 * Get a child element of the given name which has an attribute matching the given value.
	 * @param element
	 * @param attrib
	 * @return
	 */
	public Element getChildByAttrib(String element, String attrib) {
		for (Element e : children.values()) {
			if (!e.getName().equals(element)) continue;
			if (e.getAttributes().containsKey(attrib)) return e;
		}
		debug("No child with attrib '%s' and name '%s' found in '%s'", attrib, element, this.name);
		return null;
	}
	
	/**
	 * Get a child element of the given name which has an attribute of the given name with the given value.
	 * @param element
	 * @param attribName
	 * @param attribValue
	 * @return
	 */
	public Element getChildByAttrib(String element, String attribName, String attribValue) {
		for (Element e : children.values()) {
			if (!e.getName().equals(element)) continue;
			for (String s : e.getAttributes().keySet()) {
				if (!s.equals(attribName)) continue;
				if (s.equals(attribValue)) return e;
			}
		}
		debug("No child with attrib '%s' set to '%s' and name '%s' found in '%s'", attribName, attribValue, element, this.name);
		return null;
	}
	
	/**
	 * Get an array of all child elements with the given name.
	 * @param name
	 * @return
	 */
	public Element[] getChildren(String name) {
		List<Element> list = new ArrayList<Element>();
		for (Element e : children.values()) {
			if (e.getName().equals(name)) {
				list.add(e);
			}
		}
		Element[] e = new Element[0];
		e = list.toArray(e);
		return e;
	}
	
	/**
	 * Check if this element has any children.
	 * @return
	 */
	public boolean hasChildren() {
		return !children.isEmpty();
	}
	
	/**
	 * Add an attribute with the given name and value. Existing attribute of the given name is overwritten.
	 * @param name
	 * @param value
	 */
	public void addAttribute(String name, String value) {
		attributes.put(name, value);
	}
	
	/**
	 * Get the value of the given attribute name.
	 * @param name
	 * @return
	 */
	public String getAttribute(String name) {
		if (attributes.containsKey(name)) {
			return attributes.get(name);
		}
		debug("No attribute '%s' found in '%s'", name, this.name);
		return "";
	}
	
	/**
	 * Check if this element has an attribute of the given name.
	 * @param name
	 * @return
	 */
	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}

	/**
	 * Get the name of this element.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of this element.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the text for this element.
	 * @return
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Check if this element has a text node.
	 * @return
	 */
	public boolean hasText() {
		return text != null && !text.isEmpty();
	}

	/**
	 * Set the text for this element.
	 * @param value
	 */
	public void setText(String value) {
		this.text = value;
	}

	/**
	 * Get the parent element of this element.
	 * @return
	 */
	public Element getParent() {
		return parent;
	}

	/**
	 * Set the new parent element for this element.
	 * @param parent
	 */
	public void setParent(Element parent) {
		if (this.parent != null) {
			this.parent.children.remove(this._id);
		}
		this._id = parent.children.size();
		while (parent.children.containsKey(_id)) {
			_id++;
		}
		parent.addChild(this);
		this.parent = parent;
		debug("Setting parent '%s' for '%s'", parent.name, this.name);
	}

	/**
	 * Get an array of all children elements under this element.
	 * @return
	 */
	public Element[] getChildren() {
		Element[] e = new Element[0];
		return children.values().toArray(e);
	}

	/**
	 * Set the new mapping of child elements for this element.
	 * @param children
	 */
	public void setChildren(Map<Integer, Element> children) {
		this.children = children;
	}

	/**
	 * Get a copy of the HashMap containing the attributes for this element.
	 * @return
	 */
	public Map<String, String> getAttributes() {
		return new HashMap<String, String>(attributes);
	}
	
	/**
	 * Check if this element has any attributes.
	 * @return
	 */
	public boolean hasAttributes() {
		return !attributes.isEmpty();
	}

	/**
	 * Set the new mapping of attributes for this element.
	 * @param attributes
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * Remove this element from the parent.
	 */
	public void remove() {
		this.parent.children.remove(_id);
	}
	
	/**
	 * Remove the child element of the given name.
	 * @param name
	 */
	public void removeChild(String name) {
		for (Element e : children.values()) {
			if (e.getName().equals(name)) children.remove(e._id);
		}
	}
	
	/**
	 * Print a human-readable string showing the path from the document down to this element.
	 * @return
	 */
	public String printAncestry() {
		StringBuilder sb = new StringBuilder();
		Element parent = this.parent;
		sb.append(name);
		while (parent != null) {
			sb.insert(0, parent.getName() + " -> ");
			parent = parent.getParent();
		}
		return sb.toString();
	}
	
	/**
	 * Print a human-readable representation of this element and its contents. All attributes are listed
	 * within parenthesis and all child element names are listed within curly brackets.
	 */
	public String toString() {
		String output = name + "(";
		for (String s : attributes.keySet()) {
			output += s + "=" + attributes.get(s) + ",";
		}
		if (!attributes.isEmpty()) output = output.substring(0, output.length() - 1);
		output += "){";
		for (Element e : children.values()) {
			output += e.getName() + ",";
		}
		if (!children.isEmpty()) output = output.substring(0, output.length() - 1);
		output += "}";
		return output;
	}
	
	private void debug(String msg, Object... args) {
		if (_debug) System.out.printf("XML: " + msg + "\n", args);
	}

}

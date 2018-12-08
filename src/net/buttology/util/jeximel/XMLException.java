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
 * This class represents a custom exception occurring in the XMLParser.
 * @author Mudbill
 *
 */
public class XMLException extends Exception {

	private static final long serialVersionUID = 6187199395026420548L;

	public XMLException(String message) {
		super(message);
	}

	public XMLException(String message, Throwable cause) {
		super(message, cause);
	}
	
}

package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.buttology.util.jeximel.Document;
import net.buttology.util.jeximel.XMLParser;



public class Example {

	public static void main(String[] args) {
		
		try {
			
			long start = System.currentTimeMillis();
			FileInputStream fis = new FileInputStream(new File("D:/temp/testing.txt"));
//			XMLParser.debug = true;
			Document d = XMLParser.read(fis);

			System.out.println("ms="+(System.currentTimeMillis() - start));
			
			start = System.currentTimeMillis();
			XMLParser.write(d, new FileOutputStream(new File("D:/temp/written.txt")));
			System.out.println("ms="+(System.currentTimeMillis() - start));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

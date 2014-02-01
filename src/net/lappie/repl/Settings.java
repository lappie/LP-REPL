package net.lappie.repl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Settings {
	
	//FILES
	public static final String BASE = "../../../";
	public static final String SESSION_FILE_EXTENTION = ".xml";
	public static final String SESSION_FILE_DESCRIPTION = "XML files";
	
	//SYSTEM
	public static final String VERSION = "0.21";
	
	//FEATURES
	//Output folding:
	public static int MAX_OUTPUT_CHARS = 150;
	public static int MAX_OUTPUT_LINES = 5;
	
	///////////////////// PROPERTIES ///////////////////////////
	
	private static File propertiesFile = new File("repl.properties");
	private static Properties configFile = new Properties();
	static {
		if(!propertiesFile.exists()) {
			try {
				propertiesFile.createNewFile();
			}
			catch (IOException e) {
				System.err.println("properties file could not be created");
			}
		}
		
		try {
			configFile.load(new FileReader(propertiesFile));
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static String getProperty(String key) {
		return configFile.getProperty(key);
	}
	
	public static void setProperty(String key, String value) {
		configFile.setProperty(key, value);
		try {
			configFile.store(new BufferedOutputStream(new FileOutputStream(propertiesFile)), null);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}

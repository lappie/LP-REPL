package net.lappie.repl;

import java.net.URL;

import javax.swing.ImageIcon;

public class Util {
	
	public static ImageIcon getImageIcon(String address) {
		URL url = StandAloneREPL.class.getResource(address);
		return new ImageIcon(url);
	}
}

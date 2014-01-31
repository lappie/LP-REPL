package net.lappie.repl;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class StyleSettings {
	
	public final static Color BACKGROUND_COMMAND_COLOR = new Color(238, 245, 255);
	public final static Color CURRENT_LINE_COLOR = new Color(202, 227, 251);
	public final static Color LINE_ERROR_COLOR = new Color(255, 180, 180);
	
	public final static Color HIGHLIGHT_COLOR = Color.YELLOW;
	public final static Color BACKGROUND_HIGHLIGHT_COLOR = new Color(205, 205, 205);
	
	private Font consoleFont = new Font("Consolas", Font.PLAIN, 12);
	public static Font statusBarFont = new Font("Verdana", Font.ITALIC, 10);
	
	private static StyleSettings me = null;
	
	private Style regular;
	private Style output; 
	private Style info;
	private Style italic;
	private Style error;
	private Style warning;
	
	private Style stringToken;
	private Style keywordToken;
	
	private StyleSettings(JTextPane area) {
		area.setFont(consoleFont);
		area.setForeground(Color.BLACK);
		
		
		Style def = StyleContext.getDefaultStyleContext().getStyle( StyleContext.DEFAULT_STYLE );
		regular = area.addStyle("regular", def );
		
		info = area.addStyle("info", regular);
		StyleConstants.setForeground(info, Color.BLUE);
		
		output = area.addStyle("output", regular);
		StyleConstants.setForeground(output, new Color(30, 80, 20));
		
		// Create an italic style
		italic = area.addStyle("italic", regular);
		StyleConstants.setItalic(italic, true);
		
		// Create an italic style
		error = area.addStyle("error", regular);
		StyleConstants.setForeground(error, Color.RED);
		
		// Create an italic style
		warning = area.addStyle("warning", regular);
		StyleConstants.setForeground(warning, Color.ORANGE);
		StyleConstants.setItalic(warning, true);
		
		stringToken = area.addStyle("StringToken", regular);
		StyleConstants.setForeground(stringToken, new Color(0x80, 0x80, 0x80));
		
		keywordToken = area.addStyle("KeywordToken", regular);
		StyleConstants.setForeground(keywordToken, new Color(123, 0, 82));
		StyleConstants.setBold(keywordToken, true);
	}
	
	public static StyleSettings getInstance(JTextPane area) {
		if(me == null)
			me = new StyleSettings(area);
		return me;
	}
	
	public Style getRegular() {
		return regular;
	}
	
	public Style getInfo() {
		return info;
	}
	
	public Style getOutput() {
		return output;
	}
	
	public Style getItalic() {
		return italic;
	}
	
	public Style getError() {
		return error;
	}
	
	public Style getWarning() {
		return warning;
	}
	
	public Style getStringToken() {
		return stringToken;
	}
	
	public Style getKeywordToken() {
		return keywordToken;
	}
}

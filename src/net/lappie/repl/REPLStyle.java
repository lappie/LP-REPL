package net.lappie.repl;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class REPLStyle {
	
	public final static Color LINE_COLOR = new Color(202, 227, 251);
	public final static Color LINE_ERROR_COLOR = new Color(255, 180, 180);
	
	public final static Color HIGHLIGHT_COLOR = Color.YELLOW;
	public final static Color BACKGROUND_HIGHLIGHT_COLOR = new Color(205, 205, 205);
	
	private static REPLStyle me = null;
	
	private Style regular;
	private Style info;
	private Style italic;
	private Style error;
	private Style warning;
	
	private Style stringToken;
	private Style keywordToken;
	
	private REPLStyle(JTextPane area) {
		
		Font font = new Font("Courier", Font.PLAIN, 12);
		area.setFont(font);
		area.setForeground(Color.BLACK);
		
		
		Style def = StyleContext.getDefaultStyleContext().getStyle( StyleContext.DEFAULT_STYLE );
		regular = area.addStyle("regular", def );
		
		info = area.addStyle("info", regular);
		StyleConstants.setForeground(info, Color.BLUE);
		
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
	
	public static REPLStyle getInstance(JTextPane area) {
		if(me == null)
			me = new REPLStyle(area);
		return me;
	}
	
	public Style getRegular() {
		return regular;
	}
	
	public Style getInfo() {
		return info;
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

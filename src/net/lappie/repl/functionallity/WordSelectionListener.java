package net.lappie.repl.functionallity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import net.lappie.repl.ExtendedREPLPanel;

/**
 * Add a caret listener that applies that colors the background of words similar to selection
 * @author Lappie
 *
 */
public class WordSelectionListener implements CaretListener{

	private ExtendedREPLPanel repl;
	private boolean isDrawn = false; //wether we are currently drawing for optimizing
	
	public WordSelectionListener(ExtendedREPLPanel repl) {
		this.repl = repl;
	}
	
	@Override
	public void caretUpdate(CaretEvent e) {
		String selection = repl.getSelection();
		if(selection != null && selection.length() > 0) {
			repl.removeAllBackgroundHighlights(); //remove previous
			selection = selection.trim();
			if(isWord(selection)) {
				String result = repl.getResult();
				
				Pattern p = Pattern.compile("\\W" + selection + "\\W");
				Matcher matcher = p.matcher(result);
				
				int index = 0;
				while(matcher.find(index)) {
					repl.addBackgroundHighlight(matcher.start()+1, matcher.end()-matcher.start()-2);
					index = matcher.end()-1; //necessary for two similar words next to eachother (e.g. hi hi)
				}
				isDrawn = true;
			}
		}
		else if (isDrawn) {
			isDrawn = false;
			repl.removeAllBackgroundHighlights();
		}
	}

	private static boolean isWord(String word) {
		return word.matches("[A-Za-z]+");
	}
}

package net.lappie.repl.functionallity;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import net.lappie.repl.AbstractREPLPanel;

public class REPLDocumentFilter extends DocumentFilter
{
	private boolean enabledREPLFilter = true;
	private boolean completelyDisabled = false;
	
	private AbstractREPLPanel panel;
	
	public REPLDocumentFilter(AbstractREPLPanel replPanel) {
		this.panel = replPanel;
	}
	
	public void enableREPLFiltering() {
		enabledREPLFilter = true;
	}
	
	/**
	 * This ables just the REPL specific filtering part. Allows other (normal) changes still to be made.
	 */
	public void disableREPLFiltering() {
		enabledREPLFilter = false;
	}
	
	/**
	 * Allow no changes at all
	 */
	public void disableCompletely() {
		completelyDisabled = true;
	}	
	
	public void enableCompletely() {
		completelyDisabled = false;
	}
	
	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		//if(completelyDisabled) //allow no changes -- Due to bug in JDocument not necessary
			//return; 
		if (enabledREPLFilter) {
			int uneditableOffset = panel.getUneditableOffset();
			
			if (offset < uneditableOffset) {
				length = length - (uneditableOffset - offset);
				if (length <= 0) // nothing to remove anymore;
				return;
				offset = uneditableOffset;
			}
		}
		super.remove(fb, offset, length);
	}
	
	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		if(completelyDisabled) //allow no changes
			return;
		if (!enabledREPLFilter) {
			super.replace(fb, offset, length, text, attrs); // no filtering
			return;
		}
		int uneditableOffset = panel.getUneditableOffset();
		
		text = text.replaceAll("\n", "\n   "); // TODO, find better solution here
		
		if (offset < uneditableOffset) {
			if (length + offset > uneditableOffset) {// small usability tweak: if you select text at the command line AND history, it will be added at the beginning
				length = Math.max((offset + length) - uneditableOffset, 0);
				offset = uneditableOffset;
				panel.setCursor(offset + length);
			} else {// otherwise just at the end of the command line
				offset = panel.getTotalLength();
				panel.setCursor(offset);
				length = 0;
			}
		}
		
		super.replace(fb, offset, length, text, attrs);
	}
}

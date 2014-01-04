package net.lappie.repl.functionallity;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import net.lappie.repl.AbstractREPLPanel;

public class DocumentREPLFilter extends DocumentFilter
{
	private boolean enabled = true;
	
	private AbstractREPLPanel panel;
	
	public DocumentREPLFilter(AbstractREPLPanel replPanel)
	{
		this.panel = replPanel;
	}

	public void enable()
	{
		enabled = true;
	}
	
	public void disable()
	{
		enabled = false;
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException
	{
		if(enabled) {
			int uneditableOffset = panel.getUneditableOffset();
			
			if(offset < uneditableOffset)
			{
				length = length - (uneditableOffset - offset);
				if(length <= 0) //nothing to remove anymore; 
					return;
				offset = uneditableOffset;
			}
		}
		super.remove(fb, offset, length);
	}
	
	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException 
	{
		if(!enabled)
		{
			super.replace(fb, offset, length, text, attrs); //no filtering
			return;
		}
		int uneditableOffset = panel.getUneditableOffset();
		
		if (offset < uneditableOffset)
		{
			if(length + offset > uneditableOffset) //small usability tweak: if you select text at the command line AND history, it will be added at the beginning
			{
				length = Math.max((offset + length) - uneditableOffset, 0);
				offset = uneditableOffset;
				panel.setCursor(offset+length);
			}
			else //otherwise just at the end of the command line
			{
				offset = panel.getTotalLength(); 
				panel.setCursor(offset);
				length = 0;
			}
		}
		
		super.replace(fb, offset, length, text, attrs);
	}
}

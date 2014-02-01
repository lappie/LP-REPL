package net.lappie.repl.functionallity;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import net.lappie.repl.AbstractREPLPanel;
import net.lappie.repl.BasicREPLPanel;
import net.lappie.repl.REPLOutputStream;

public class FoldedTextRepository implements MouseListener, IOffsetListener {

	private BasicREPLPanel repl; 
	private ArrayList<TextHolder> text = new ArrayList<>();
	
	public FoldedTextRepository(BasicREPLPanel repl) {
		this.repl = repl;
	}
	
	public void addFoldedText(int offset, String unfoldedText) {
		text.add(new TextHolder(offset, AbstractREPLPanel.parseOutput(unfoldedText)));
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int caret = repl.getCaretPosition();
		for(TextHolder th : text) {
			if(caret >= th.offset && caret <= th.offset+REPLOutputStream.UNFOLD_SYMBOL.length()-1) {
				
				if(th.folded) {
					repl.switchClickableText(th.offset, REPLOutputStream.FOLD_SYMBOL);
					repl.insertOutput(th.offset, th.unfoldedText);	
					th.folded = false;
					//offset is updated via event
				}
				else {
					repl.switchClickableText(th.offset, REPLOutputStream.UNFOLD_SYMBOL);
					repl.removeOutput(th.offset-th.unfoldedText.length(), th.unfoldedText.length());	
					th.folded = true;
					//offset is updated via event
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void fire(int offset, int length) {
		for(TextHolder th : text) {
			if(th.offset >= offset) {
				th.offset += length;
			}
		}
	}
	
	private class TextHolder {
		private String unfoldedText;
		private int offset = -1;
		private boolean folded = true;
		
		public TextHolder(int offset, String unfoldedText) {
			this.offset = offset;
			this.unfoldedText = unfoldedText;
		}
	}
}

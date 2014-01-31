package net.lappie.repl.functionallity;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import net.lappie.repl.StyleSettings;

public class BackgroundLinePainter implements Highlighter.HighlightPainter {

	private ArrayList<OffsetTuple> offsets = new ArrayList<>();
	
	public BackgroundLinePainter(JTextComponent component) {
		try {
			component.getHighlighter().addHighlight(0, 0, this);
		}
		catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void addLineOffset(int offset) {
		offsets.add(new OffsetTuple(offset));
	}
	
	public void addLineOffset(int offset, int end) {
		offsets.add(new OffsetTuple(offset, end));
	}
	
	@Override
	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
		try {
			for(OffsetTuple ot : offsets) {
				Rectangle r = c.modelToView(ot.offset);
				int height = r.height;
				if(ot.end > 0) {
					Rectangle r2 = c.modelToView(ot.end);
					height = r2.y + r2.height - r.y;
				}
				g.setColor(StyleSettings.BACKGROUND_COMMAND_COLOR);
				g.fillRect(0, r.y, c.getWidth(), height);
			}
		}
		catch (BadLocationException ble) {
			System.out.println(ble);
		}
	}

	private class OffsetTuple {
		private int offset = -1;
		private int end = -1;
		
		public OffsetTuple(int offset, int end) {
			this.offset = offset;
			this.end = end;
		}
		
		public OffsetTuple(int offset) {
			this.offset = offset;
		}
	}
}

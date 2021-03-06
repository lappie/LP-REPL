package net.lappie.repl.functionallity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

/**
 * Track the movement of the Caret by painting a background line at the current caret position.
 * 
 * Source: http://tips4java.wordpress.com/2008/10/29/line-painter/
 */
public class CurrentLinePainter implements Highlighter.HighlightPainter, CaretListener, MouseListener, MouseMotionListener {
	private JTextComponent component;
	
	private Color color;
	
	private Rectangle lastView;
	
	/*
	 * Manually control the line color
	 * 
	 * @param component text component that requires background line painting
	 * 
	 * @param color the color of the background line
	 */
	public CurrentLinePainter(JTextComponent component, Color color) {
		this.component = component;
		setColor(color);
		
		// Add listeners so we know when to change highlighting
		
		component.addCaretListener(this);
		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		
		// Turn highlighting on by adding a dummy highlight
		
		try {
			component.getHighlighter().addHighlight(0, 0, this);
		}
		catch (BadLocationException ble) {
			ble.printStackTrace();
		}
	}
	
	/*
	 * You can reset the line color at any time
	 * 
	 * @param color the color of the background line
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	// Paint the background highlight
	
	@Override
	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
		try {
			Rectangle r = c.modelToView(c.getCaretPosition());
			g.setColor(color);
			g.fillRect(0, r.y, c.getWidth(), r.height);
			
			if (lastView == null) lastView = r;
		}
		catch (BadLocationException ble) {
			ble.printStackTrace();
		}
	}
	
	/*
	 * Caret position has changed, remove the highlight
	 */
	private void resetHighlight() {
		// Use invokeLater to make sure updates to the Document are completed,
		// otherwise Undo processing causes the modelToView method to loop.
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					int offset = component.getCaretPosition();
					Rectangle currentView = component.modelToView(offset);
					
					// Remove the highlighting from the previously highlighted line
					
					if (lastView != null && lastView.y != currentView.y) {
						component.repaint(0, lastView.y, component.getWidth(), lastView.height);
						lastView = currentView;
					}
				}
				catch (BadLocationException ble) {
					ble.printStackTrace();
				}
			}
		});
	}
	
	// Implement CaretListener
	
	@Override
	public void caretUpdate(CaretEvent e) {
		resetHighlight();
	}
	
	// Implement MouseListener
	
	@Override
	public void mousePressed(MouseEvent e) {
		resetHighlight();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	// Implement MouseMotionListener
	
	@Override
	public void mouseDragged(MouseEvent e) {
		resetHighlight();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
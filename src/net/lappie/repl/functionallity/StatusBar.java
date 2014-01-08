package net.lappie.repl.functionallity;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import net.lappie.repl.REPLStyle;

public class StatusBar extends JPanel {
	
	private JLabel statusLabel = new JLabel();
	private final int HEIGHT = 16;
	
	public StatusBar(int width) {
		super();
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setPreferredSize(new Dimension(width, HEIGHT));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		statusLabel.setPreferredSize(new Dimension(300, HEIGHT)); //TODO, 300?
		statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		statusLabel.setFont(REPLStyle.statusBarFont);
		add(Box.createHorizontalGlue());
		add(statusLabel);
	}
	
	public void clear() {
		statusLabel.setText("");
	}
	
	public void setText(String text) {
		statusLabel.setText(text);
	}
}

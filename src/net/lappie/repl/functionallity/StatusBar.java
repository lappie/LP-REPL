package net.lappie.repl.functionallity;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class StatusBar extends JPanel {
	
	private JLabel statusLabel = new JLabel();
	
	public StatusBar(int width) {
		super();
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setPreferredSize(new Dimension(width, 16));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		statusLabel.setPreferredSize(new Dimension(300, 16)); //TODO, 300?
		statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		statusLabel.setFont(new Font("Verdana", Font.ITALIC, 10));
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

package net.lappie.repl;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter.Highlight;

import net.lappie.repl.functionallity.LinePainter;
import net.lappie.repl.functionallity.REPLDocumentListener;
import net.lappie.repl.functionallity.StatusBar;
import net.lappie.repl.functionallity.SyntaxHighlightParser;
import net.lappie.repl.functionallity.WordSelectionListener;
import net.lappie.repl.functionallity.extensions.IREPLExtension;
import net.lappie.repl.functionallity.extensions.SearchExtension;
import net.lappie.repl.languages.IREPLSettings;
import net.lappie.repl.languages.PythonSettings;

/**
 * Creates the BasicREPL with extended usability features: 
 * - History management
 * - Home key 
 * - Insert blank line 
 * - LineHighlighter 
 * - Same word highlighting 
 * - Execute Selection on the press of CTRL+ENTER
 * 
 * And the ability to extend the REPL with other functionallity. This can be
 * added via the function addExtension
 */
public class ExtendedREPLPanel extends BasicREPLPanel {

	private REPLDocumentListener documentListener;
	private DefaultHighlighter.DefaultHighlightPainter highlightPainter;
	private DefaultHighlighter.DefaultHighlightPainter backgroundHighlightPainter;

	private LinePainter linePainter;
	private IREPLExtension loadedExtention = null;

	private StatusBar statusBar = new StatusBar(AbstractREPLPanel.WIDTH);

	public ExtendedREPLPanel(IREPLSettings settings) {
		super(settings);
		loadKeys();

		//The painters for background colors: 
		highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(REPLStyle.HIGHLIGHT_COLOR);
		backgroundHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(REPLStyle.BACKGROUND_HIGHLIGHT_COLOR);
		linePainter = new LinePainter(getREPLTextComponent(), REPLStyle.LINE_COLOR); // line highlighter
		
		//Listeners: 
		SyntaxHighlightParser parser = new SyntaxHighlightParser(getREPLTextComponent(), styles);
		documentListener = new REPLDocumentListener(this, parser);
		addDocumentListener(documentListener);
		addCaretListener(new WordSelectionListener(this));

		add(statusBar, BorderLayout.SOUTH);
	}

	public void addHighlight(int offs, int len) {
		try {
			getHighlighter().addHighlight(offs, offs + len, highlightPainter);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void removeAllHighlights() {
		for (Highlight h : getHighlighter().getHighlights()) {
			if (h.getPainter() != linePainter)
				getHighlighter().removeHighlight(h);
		}
	}

	public void addBackgroundHighlight(int offs, int len) {
		try {
			getHighlighter().addHighlight(offs, offs + len, backgroundHighlightPainter);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove all background highlights set via addBackgroundHighlight();
	 */
	public void removeAllBackgroundHighlights() {
		for (Highlight h : getHighlighter().getHighlights()) {
			if(getSelection() == null || (h.getStartOffset() != getSelectionStart() && h.getEndOffset() != getSelectionEnd())) //do not remove current selection 
				if (h.getPainter() != linePainter && h.getPainter() != highlightPainter)
					getHighlighter().removeHighlight(h);
		}
	}

	public void showErrorStatus() {
		linePainter.setColor(REPLStyle.LINE_ERROR_COLOR);
	}

	public void showNormalStatus() {
		linePainter.setColor(REPLStyle.LINE_COLOR);
	}

	public void addStatusMessage(String message) {
		statusBar.setText(message);
	}

	public void clearStatusMessage() {
		statusBar.clear();
	}

	public void addExtension(IREPLExtension extention) {
		addKeyAction(extention.getKeyCombination(), new KeyExtensionLoader(
				extention));
	}

	@Override
	public void evaluate() {
		clearStatusMessage();
		if (loadedExtention != null) {
			loadedExtention.execute();
			return;
		}

		super.evaluate();
	}

	public void loadKeys() {
		addKeyAction("shift ENTER", new InsertBreakCommand());
		addKeyAction("control ENTER", new ForceExecuteCommand());
		addKeyAction("control UP", new HistoryUpCommand());
		addKeyAction("control DOWN", new HistoryDownCommand());

		addKeyAction("HOME", new HomeCommand());
		addKeyAction("shift HOME", new ShiftHomeCommand());
		addKeyAction("ESCAPE", new EscapeCommand());
	}

	private class KeyExtensionLoader extends AbstractAction {

		private IREPLExtension extention;

		public KeyExtensionLoader(IREPLExtension extention) {
			this.extention = extention;
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			loadedExtention = extention;
			documentListener.loadExtension(extention);
		}
	}

	private class InsertBreakCommand extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent ev) {
			addNewOutputLine();
		}
	}

	private class ForceExecuteCommand extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent ev) {
			String selection = getSelection();
			if (selection != null) {
				setCommand(selection);
			}
			forceEvaluate();
		}
	}

	private class EscapeCommand extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent ev) {
			historyIndex = history.size();
			setCommand("");
			setMode(COMMAND_SYMBOL);

			documentListener.stopExtension();
			loadedExtention = null;
			removeAllBackgroundHighlights();
		}
	}

	private class HomeCommand extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent ev) {
			// if on last line, do or do not include commandSymbol
			if (onLastLine()) {
				setCursor(commandIndex);
			} else {
				ActionMap actions = getREPLActionMap();
				Action a = actions.get("caret-begin-line"); // perform original
															// action.
				a.actionPerformed(ev);
			}
		}
	}

	private class ShiftHomeCommand extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent ev) {
			if (onLastLine()) {
				setCursorSelection(commandIndex);
			} else {
				ActionMap actions = getREPLActionMap();
				Action a = actions.get("selection-begin-line"); // perform
																// original
																// action.
				a.actionPerformed(ev);
			}
		}
	}

	private class HistoryUpCommand extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			historyIndex -= historyIndex >= 0 ? 1 : 0;
			if (historyIndex < 0) {
				setCommand("");
				return;
			}
			setCommand(commandHistory.get(historyIndex));
		}
	}

	private class HistoryDownCommand extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			historyIndex += historyIndex < commandHistory.size() ? 1 : 0;
			if (historyIndex == commandHistory.size()) {
				setCommand("");
				return;
			}
			setCommand(commandHistory.get(historyIndex));
		}
	}

	public static void main(String args[]) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setVisible(true);

				ExtendedREPLPanel x = new ExtendedREPLPanel(
						new PythonSettings());
				x.addExtension(new SearchExtension(x));

				frame.setContentPane(x);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setLocationRelativeTo(null);
			}
		});
	}

}

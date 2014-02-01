package net.lappie.repl;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter.Highlight;

import net.lappie.repl.functionallity.BackgroundLinePainter;
import net.lappie.repl.functionallity.CurrentLinePainter;
import net.lappie.repl.functionallity.FoldedTextRepository;
import net.lappie.repl.functionallity.REPLDocumentListener;
import net.lappie.repl.functionallity.StatusBar;
import net.lappie.repl.functionallity.SyntaxHighlightParser;
import net.lappie.repl.functionallity.WordSelectionListener;
import net.lappie.repl.functionallity.extensions.IREPLExtension;
import net.lappie.repl.functionallity.extensions.SearchExtension;
import net.lappie.repl.languages.ILanguageSettings;
import net.lappie.repl.languages.rascal.RascalSettings;

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
	private BackgroundLinePainter commandBackgroundPainter;
	private CurrentLinePainter currentLinePainter;
	
	private IREPLExtension loadedExtention = null;
	private FoldedTextRepository foldedTextRepo = new FoldedTextRepository(this);
	
	private StatusBar statusBar = new StatusBar(AbstractREPLPanel.WIDTH);
	private ILanguageSettings settings;
	
	//history completion: 
	private int hcIndex = -1;
	private ArrayList<String> hcList = new ArrayList<>();
	private String hcPrefix = "";

	public ExtendedREPLPanel(ILanguageSettings settings) {
		super(settings);
		this.settings = settings;
		loadKeys();

		//The painters for background colors: 
		highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(StyleSettings.HIGHLIGHT_COLOR);
		backgroundHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(StyleSettings.BACKGROUND_HIGHLIGHT_COLOR);
		commandBackgroundPainter = new BackgroundLinePainter(getREPLTextComponent());
		addOffsetListener(commandBackgroundPainter);
		currentLinePainter = new CurrentLinePainter(getREPLTextComponent(), StyleSettings.CURRENT_LINE_COLOR); // line highlighter
		
		//Listeners: 
		SyntaxHighlightParser parser = new SyntaxHighlightParser(getREPLTextComponent(), styles);
		documentListener = new REPLDocumentListener(this, parser);
		addDocumentListener(documentListener);
		addCaretListener(new WordSelectionListener(this));
		out.addFoldedTextRepo(foldedTextRepo);
		getREPLTextComponent().addMouseListener(foldedTextRepo);
		addOffsetListener(foldedTextRepo);
		
		add(statusBar, BorderLayout.SOUTH);
	}
	
	@Override
	public void removeOutput(int offset, int len) {
		documentFilter.disableREPLFiltering();
		documentListener.disable();
		super.removeOutput(offset, len);
		documentListener.enable();
		documentFilter.enableREPLFiltering();
	}
	
	@Override
	public void switchClickableText(int offset, String text) {
		documentFilter.disableREPLFiltering();
		documentListener.disable();
		super.switchClickableText(offset, text);
		documentListener.enable();
		documentFilter.enableREPLFiltering();
	}

	@Override
	protected void addBackgroundCommandMarker() {
		commandBackgroundPainter.addLineOffset(getCommandIndex(), getTotalLength());
	}

	/**
	 * ONLY for proof of concept that we can use search in an Eclipse view. 
	 * This method will be removed. 
	 */
	public void startSearch() {
		documentListener.loadExtension(new SearchExtension(this));
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
			if (h.getPainter() != currentLinePainter && h.getPainter() != commandBackgroundPainter)
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
				if (h.getPainter() != currentLinePainter && h.getPainter() != highlightPainter && h.getPainter() != commandBackgroundPainter)
					getHighlighter().removeHighlight(h);
		}
	}

	public void showErrorStatus() {
		currentLinePainter.setColor(StyleSettings.LINE_ERROR_COLOR);
	}

	public void showNormalStatus() {
		currentLinePainter.setColor(StyleSettings.CURRENT_LINE_COLOR);
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
		addKeyAction("TAB", new HistoryCompletionCommand());

		addKeyAction("HOME", new HomeCommand());
		addKeyAction("shift HOME", new ShiftHomeCommand());
		addKeyAction("ESCAPE", new EscapeCommand());
		
		if(settings.hasFunctionHelpCommand())
			addKeyAction("F2", new FunctionHelpCommand());
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
	
	private class FunctionHelpCommand extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent ev) {
			String functionName = getWordUnderCursor();
			if(functionName.length() > 0)
				settings.getFunctionHelpProvider().performFunctionHelp(functionName);
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
			setCursorToEnd();
		}
	}

	/**
	 * The Escape Commands has different functions: 
	 *  - Reset the history counter
	 *  - Clear the command path
	 *  - Return to command Mode (stop any extensions)
	 *  - Stop any current running evaluation
	 * @author Lappie
	 *
	 */
	private class EscapeCommand extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent ev) {
			System.out.println("ESC pressed");
			if(currentExecution!= null && !currentExecution.isDone()) { //stop any current running evaluation.
				System.out.println("Stopping running thread");
				if(currentExecution.cancel(true)) {
					currentExecution.done();
					addNewLine();
					addCommandMarker();
					displayREPLError("Operation cancelled");
					setCursorToEnd();
				}
				
				System.out.println("Stopped");
				return;
			}
			//reset necessary history settings
			historyIndex = commandHistory.size(); //reset counter
			if(hcIndex >= 0) {
				hcList.clear();
				hcIndex = -1;
				setCommand(hcPrefix);
				return;
			}
			
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
				Action a = actions.get("caret-begin-line"); // perform original action.
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
				Action a = actions.get("selection-begin-line"); // perform original action.
				a.actionPerformed(ev);
			}
		}
	}

	private class HistoryUpCommand extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			historyIndex--;
			if (historyIndex < 0) {
				historyIndex = -1;
				setCommand("");
				return;
			}
			setCommand(commandHistory.get(historyIndex));
		}
	}

	private class HistoryDownCommand extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			historyIndex++;
			if (historyIndex >= commandHistory.size()) {
				historyIndex = commandHistory.size(); //don't let it go up
				setCommand("");
				return;
			}
			setCommand(commandHistory.get(historyIndex));
		}
	}
	
	private class HistoryCompletionCommand extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent ev) {
			String command = getCommand();
			if(command.trim().length() == 0 || command.contains("\n"))  { //perform original action
				ActionMap actions = getREPLActionMap();
				Action a = actions.get("insert-tab"); // perform original action.
				a.actionPerformed(ev);
				return;
			}
			
			if(hcIndex < 0) {
				hcPrefix = command;
				hcList.clear();
				for(String c : commandHistory) {
					if(c.startsWith(command))
						hcList.add(c);
				}
			}
			
			hcIndex++;
			if(hcIndex < hcList.size()) {
				setCommand(hcList.get(hcIndex));
			}
			else {
				hcIndex = -1; //start from the beginning
				setCommand(hcPrefix);
			}
		}
		
	}

	public static void main(String args[]) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setVisible(true);

				ExtendedREPLPanel x = new ExtendedREPLPanel(
						new RascalSettings());
				x.addExtension(new SearchExtension(x));

				frame.setContentPane(x);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setLocationRelativeTo(null);
			}
		});
	}

}

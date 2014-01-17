package net.lappie.repl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Highlighter;

import net.lappie.repl.functionallity.LongWordWrapEditorKit;

@SuppressWarnings("serial")
/**
 * This is a JPanel with all basic functions for a REPL. It offers however only these
 * functions and has no behavior in it at all.
 * 
 * Support for wordwrap is supported. Too long words are automatically broken off. 
 * 
 * @author Lappenschaar
 */
public abstract class AbstractREPLPanel extends JPanel {
	private final int PANEL_WIDTH = 500;
	private final int PANEL_HEIGHT = 250;

	private final int TAB_SIZE = 4;
	
	protected final String COMMAND_SYMBOL = ">> ";
	private final String OUT_SYMBOL = "   ";

	public static final Color CURRENT_LINE_BG_COLOR = new Color(200, 215, 255);
	public static final Color SELECT_BG_COLOR = new Color(0xf0f0f0);
	public static final Color NEGATIVE_BG_COLOR = new Color(255, 143, 143);

	/**
	 * Internal we hold the following terms: - command: The command as it is
	 * typed by the user or will be executed - result: The result of a command -
	 * commandMode: The mode we're working with. e.g. normal, search,
	 * historysearch
	 */

	private JTextPane area;
	private Document document;
	protected REPLStyle styles;

	// Also used as identifier:
	private final String commandSymbol = ">> ";
	protected int commandIndex = 0;

	protected String commandMode = commandSymbol; // current mode of command

	public AbstractREPLPanel() {
		super(new BorderLayout());

		loadArea();
		styles = REPLStyle.getInstance(area);
	}

	private void loadArea() {
		area = new JTextPane();
		area.setEditorKit(new LongWordWrapEditorKit());
		document = area.getDocument();

		JScrollPane sp = new JScrollPane(area);
		sp.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(sp, BorderLayout.CENTER);
	}

	private String getText(int offset, int length) {
		try {
			return document.getText(offset, length);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return "";
	}

	private String getAllText() {
		return getText(0, document.getLength());
	}

	/**
	 * Returns the result so far created. Everything except the current command
	 * prompt
	 */
	public String getResult() {
		return getText(0, commandIndex - commandMode.length());
	}

	public void clearScreen() {
		commandIndex = 0;
		area.setText("");
		addCommandMarker();
	}

	public void setMode(String mode) {
		setCommand(""); // first empty line.
		commandIndex = document.getLength() - commandMode.length(); // do this first, otherwise the document filter won't allow us to change it
		try {
			document.remove(commandIndex, commandMode.length());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		add(mode, styles.getRegular());
		this.commandMode = mode;
		commandIndex = document.getLength();
	}

	public void resetMode() {
		setMode(COMMAND_SYMBOL);
	}

	public String getCommandMode() {
		return commandMode;
	}

	public int getUneditableOffset() {
		return commandIndex;
	}

	public void setCursorToEnd() {
		area.setCaretPosition(document.getLength());
	}

	public void setCursor(int pos) {
		area.setCaretPosition(pos);
	}

	public void setCursorSelection(int pos) {
		area.setSelectionStart(pos);
	}
	
	public int getSelectionStart() {
		return area.getSelectionStart();
	}
	
	public int getSelectionEnd() {
		return area.getSelectionEnd();
	}

	public int getTotalLength() {
		return document.getLength();
	}

	public String getTypedCommand() {
		return getText(commandIndex, document.getLength() - commandIndex);
	}
	
	/**
	 * Returns the word that is under de cursor. Only returns [a-zA-Z] words, and will return
	 *  an empty string if there is no word under the cursor. 
	 * @return
	 */
	public String getWordUnderCursor() {
		
		//we cannot search backward, so we start from the beginning: 
		String text = getAllText();
		int index = getCaretPosition();
		Pattern pattern = Pattern.compile("[a-zA-Z]+"); //test version, this is not perfect. For example words between quotes
		Matcher matcher = pattern.matcher(text);
		while(matcher.find()) {
			if(matcher.start() > index)
				return "";
			if(matcher.start() <= index && matcher.end() >= index)
				return getText(matcher.start(), matcher.end()-matcher.start());
		}
		return "";
	}

	private void add(String text, AttributeSet style) {
		try {
			document.insertString(document.getLength(), text, style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Insert at current position
	 */
	private void insert(String text, AttributeSet style) {
		try {
			document.insertString(getCaretPosition(), text, style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	private void remove(int offset, int length) {
		try {
			document.remove(offset, length);
		} catch (BadLocationException e) {

			e.printStackTrace();
		}
	}

	/**
	 * equals to setCommand("");
	 */
	protected void addCommandMarker() {
		add(commandMode, styles.getRegular());
		commandIndex = document.getLength();
	}

	protected void removeCommandMarker() {
		int commandLength = commandMode.length();
		commandIndex = document.getLength() - commandLength;
		// TODO check if there really is a command marker in place
		remove(document.getLength() - commandLength, commandLength);
	}

	protected void removeCommand() {
		remove(commandIndex, document.getLength() - commandIndex);
	}

	public String getCommand() {
		return getText(commandIndex, document.getLength()-commandIndex);
	}
	
	public int getCommandIndex() {
		return commandIndex;
	}
	
	public void setCommand(String command) {
		removeCommand();
		//command = command.replaceAll("\n", "\n" + OUT_SYMBOL);
		add(command, styles.getRegular());
	}

	protected void addCommand(String command) {
		command = command.replaceAll("\n", "\n" + OUT_SYMBOL); //TODO ugly, fix this
		add(command, styles.getRegular());
	}

	protected void addREPLCommand(String command) {
		add(command + "\n", styles.getRegular());
	}

	protected void addInfo(String info) {
		add(OUT_SYMBOL + info + "\n", styles.getInfo());
	}

	protected void addOutput(String output) {
		/*add(OUT_SYMBOL, styles.getRegular());
		parseOutput(output);
		add("\n", styles.getRegular());*/
		output = output.replaceAll("\n", "\n" + OUT_SYMBOL);//TODO, find better solution
		add(OUT_SYMBOL + output + "\n", styles.getRegular());
		setCursorToEnd();
	}

	protected void addError(String error) {
		add(OUT_SYMBOL + error + "\n", styles.getError());
	}

	protected void addWarning(String warning) {
		add(OUT_SYMBOL + warning + "\n", styles.getWarning());
	}

	protected void addNewLine() {
		add("\n", styles.getRegular());
	}

	protected void addNewOutputLine() {
		insert("\n" + OUT_SYMBOL, styles.getRegular());
	}

	public void messageColorTest() {
		addInfo("Welcome to LP-REPL");
		addCommand("print('xx')");
		addOutput("xx");
		addREPLCommand("now loading a new file...");
		addError("File not found");
	}

	public int getCaretPosition() {
		return area.getCaretPosition();
	}
	
	public String getSelection() {
		return area.getSelectedText();
	}

	public boolean onLastLine() {
		return getAllText().lastIndexOf("\n") < area.getCaretPosition();
	}
	
	/////////////// Public Area functions /////////////
	
	public void addDocumentFilter(DocumentFilter documentFilter) {
		((AbstractDocument) document).setDocumentFilter(documentFilter);
	}
	
	public void addDocumentListener(DocumentListener listener) {
		document.addDocumentListener(listener);
	}
	
	public void addCaretListener(CaretListener listener) {
		area.addCaretListener(listener);
	}
	
	protected InputMap getREPLInputMap() {
		return area.getInputMap();
	}
	
	protected ActionMap getREPLActionMap() {
		return area.getActionMap();
	}
	
	protected Highlighter getHighlighter() {
		return area.getHighlighter();
	}
	
	protected JTextPane getREPLTextComponent() {
		return area;
	}
}
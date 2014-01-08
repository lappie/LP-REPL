package net.lappie.repl;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.lappie.repl.functionallity.REPLDocumentFilter;
import net.lappie.repl.history.Command;
import net.lappie.repl.history.CommandType;
import net.lappie.repl.languages.IREPLSettings;
import net.lappie.repl.languages.PythonSettings;
import net.lappie.repl.languages.evaluator.IEvaluator;

/**
 * This class extends the AbstractREPLPanel and adds the default REPL behavior of evaluating 
 * commands, and only being able to edit everything after the commandMarker;
 * 
 * It handles extra functionallity of maintaining a history
 */
@SuppressWarnings("serial")
public class BasicREPLPanel extends AbstractREPLPanel {
	
	private IEvaluator evaluator;
	protected REPLDocumentFilter documentFilter = new REPLDocumentFilter(this);
	
	protected IREPLSettings settings;
	private REPLOutputStream output = new REPLOutputStream(this);
	private REPLErrorStream err = new REPLErrorStream(this);
	
	protected ArrayList<Command> history = new ArrayList<>();
	protected ArrayList<String> commandHistory = new ArrayList<>();
	protected int historyIndex = 0; // if scrolling through history this will hold where we are
	
	public BasicREPLPanel(IREPLSettings settings) {
		super();
		
		this.settings = settings;
		this.evaluator = settings.getEvaluator();
		evaluator.setOutputStream(output);
		evaluator.setErrorStream(err);
		
		// load:
		addDocumentFilter(documentFilter);
		addKeyAction("ENTER", new ExecuteCommand());
		
		addInfo("Welcome to the LP-REPL v0.0.1");
		addCommandMarker();
	}
	
	public IEvaluator getEvaluator() {
		return evaluator;
	}
	
	public void addKeyAction(String keyCombination, Action action) {
		InputMap input = getREPLInputMap();
		ActionMap actions = getREPLActionMap();
		KeyStroke stroke = KeyStroke.getKeyStroke(keyCombination);
		input.put(stroke, keyCombination); // we use the keyCombination as an identifier since it is also unique
		actions.put(keyCombination, action);
	}
	
	public void displayMessage(String message) {
		removeCommand();
		removeCommandMarker();
		addInfo(message);
		addCommandMarker();
	}
	
	public void displayWarning(String warning) {
		removeCommand();
		removeCommandMarker();
		addWarning(warning);
		addCommandMarker();
	}
	
	public void executeCommand(String command) {
		addCommand(command);
		forceEvaluate();
	}
	
	@Override
	public String getName() {
		return evaluator.getName();
	}
	
	public ArrayList<Command> getHistory() {
		return history;
	}
	
	public void evaluate() {
		String command = getTypedCommand();
		if(!evaluator.isComplete(command)) {
			addNewOutputLine();
			return;
		}
		forceEvaluate(command);
	}
	
	public void forceEvaluate() {
		String command = getTypedCommand();
		forceEvaluate(command);
	}
	
	public void forceEvaluate(String command) {
		addNewLine();
		history.add(new Command(command, CommandType.COMMAND));
		commandHistory.add(command);
		historyIndex = commandHistory.size();
		evaluator.execute(command);
		addCommandMarker();
	}
	
	private class ExecuteCommand extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent ev) {
			evaluate();
		}
	}
	
	public static void main(String args[]) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setVisible(true);
				
				frame.setContentPane(new BasicREPLPanel(new PythonSettings()));
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.pack();
				frame.setLocationRelativeTo(null);
			}
		});
	}
}

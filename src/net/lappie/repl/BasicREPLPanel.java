package net.lappie.repl;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import net.lappie.repl.functionallity.REPLDocumentFilter;
import net.lappie.repl.history.Command;
import net.lappie.repl.history.CommandType;
import net.lappie.repl.languages.IREPLSettings;
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
	
	private void doBeforeExecution(String command) {
		history.add(new Command(command, CommandType.COMMAND));
		commandHistory.add(command);
		historyIndex = commandHistory.size();
	}
	
	private void doAfterExecution(String command) {
		addCommandMarker();
		setCursorToEnd();
	}
	
	
	public void forceEvaluate(final String command) { //Warning: if changing this function, then also pay attention to ExecuteTasks
		addNewLine();
		doBeforeExecution(command);
		
		documentFilter.disableCompletely();
		ExecuteTask et = new ExecuteTask(command);
		et.execute();
	}
	
	public void executeCommand(String command) {
		addCommand(command);
		forceEvaluate();
	}
	
	
	public void executeCommands(ArrayList<String> commands) {
		documentFilter.disableCompletely();
		
		ExecuteTasks et = new ExecuteTasks(commands);
		et.execute();
	}
	
	private class ExecuteCommand extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent ev) {
			evaluate();
		}
	}
	
	private class ExecuteTasks extends SwingWorker<Void, Void> {

		private ArrayList<String> commands;
		
		public ExecuteTasks(ArrayList<String> commands) {
			this.commands = commands;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			for(String command : commands) {
				doBeforeExecution(command);
				
				addCommand(command);
				addNewLine();
				evaluator.execute(command);
				
				doAfterExecution(command);
			}
			return null;
		}
		
		@Override
		protected void done() {
			documentFilter.enableCompletely();
		}
	}
	
	private class ExecuteTask extends SwingWorker<Void, Void> {

		private String command;
		
		public ExecuteTask(String command) {
			this.command = command;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			evaluator.execute(command);
			doAfterExecution(command);
			return null;
		}
		
		@Override
		protected void done() {
			documentFilter.enableCompletely();
			setCursorToEnd();
		}
	}
	
	/*public static void main(String args[]) {
		
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
	}*/
}

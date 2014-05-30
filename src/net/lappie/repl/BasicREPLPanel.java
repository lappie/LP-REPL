package net.lappie.repl;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import net.lappie.repl.functionallity.REPLDocumentFilter;
import net.lappie.repl.functionallity.extensions.IREPLCommandListener;
import net.lappie.repl.history.Command;
import net.lappie.repl.history.CommandType;
import net.lappie.repl.history.History;
import net.lappie.repl.languages.AbstractResult;
import net.lappie.repl.languages.IEvaluator;
import net.lappie.repl.languages.ILanguageSettings;

/**
 * This class extends the AbstractREPLPanel and adds the default REPL behavior of evaluating 
 * commands, and only being able to edit everything after the commandMarker;
 * 
 * It handles extra functionallity of maintaining a history of the following types:
 * Command, Output, Result, Error, Message, REPL Command, REPL Warning, REPL Error
 */
@SuppressWarnings("serial")
public class BasicREPLPanel extends AbstractREPLPanel {
	
	private IEvaluator evaluator;
	protected REPLDocumentFilter documentFilter = new REPLDocumentFilter(this);
	
	protected ILanguageSettings settings;
	protected REPLOutputStream out = new REPLOutputStream(this);
	private REPLErrorStream err = new REPLErrorStream(this);
	
	private History history = new History();
	protected ArrayList<String> commandHistory = new ArrayList<>();
	
	private List<IREPLCommandListener> replCommandListeners = new ArrayList<>();
	
	/**
	 * This will hold where we are when scrolling through history. 
	 * Goes from -1 (empty), 0 (first command), ..., history.length-1 (last executed command), history.length (empty) 
	 */
	protected int historyIndex = 0;
	
	protected CommandExecutor currentExecution = null;
	
	
	public BasicREPLPanel(ILanguageSettings settings) {
		super();
		
		this.settings = settings;
		this.evaluator = settings.getEvaluator();
		evaluator.load(out, err);
		
		// load:
		addDocumentFilter(documentFilter);
		addKeyAction("ENTER", new ExecuteCommand());
	}
	
	
	
	public void start() {
		clearLine(); //if command marker already in place
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
	
	/////////////////////////////////////////////////////////////
	// Use the following functions to add anything to the REPL //
	/////////////////////////////////////////////////////////////
	
	public void displayError(String error) {
		history.addError(error);
		clearLine();
		addError(error);
		addCommandMarker();
	}
	
	public void displayOutput(String error) {
		history.addOutput(error);
		clearLine();
		addOutput(error);
		addCommandMarker();
	}
	
	public void displayMessage(String message) {
		history.addMessage(message);
		clearLine();
		addMessage(message);
		addCommandMarker();
	}
	
	public void displayREPLCommand(String command) {
		history.addREPLCommand(command);
		documentFilter.disableREPLFiltering();
		clearLine();
		documentFilter.enableREPLFiltering();
		addREPLCommand(command);
		addCommandMarker();
	}
	
	public void displayREPLWarning(String warning) {
		history.addREPLWarning(warning);
		clearLine();
		addREPLWarning(warning);
		addCommandMarker();
	}
	
	public void displayREPLError(String error) {
		history.addREPLError(error);
		clearLine();
		addREPLError(error);
		addCommandMarker();
	}
	
	@Override
	public String getName() {
		return evaluator.getName();
	}
	
	public History getHistory() {
		return history;
	}
	
	public void evaluate() {
		String command = getTypedCommand();
		//addBackgroundCommandMarker();
		if(!evaluator.isComplete(command)) {
			addToCommand(settings.getPostUnfinishedStatement());
			return;
		}
		forceEvaluate(command);
	}
	
	public void forceEvaluate() {
		String command = getTypedCommand();
		forceEvaluate(command);
	}
	
	private void doBeforeExecution(String command) {
		history.addCommand(command);
		commandHistory.add(command);
		historyIndex = commandHistory.size();
		
		//documentFilter.disableCompletely();
	}
	
	private void doAfterExecution() {
		if(!onBlankLine()) {
			addNewLine();
		}
		addCommandMarker();
		setCursorToEnd();
	}
	
	protected void addBackgroundCommandMarker() {
		//TODO, make event listener?
	}
	
	public void forceEvaluate(final String command) { 
		currentExecution = new CommandExecutor(command);
		currentExecution.beforeStart();
		currentExecution.execute();
	}
	
	public void executeCommands(List<String> commands) {
		currentExecution = new CommandExecutor(commands);
		currentExecution.beforeStart();
		currentExecution.execute();
	}
	
	private void handleREPLCommand(String command) {
		for(IREPLCommandListener l : replCommandListeners)
			if(l.match(command))
				l.execute(command);
	}
	
	public void addREPLCommandListener(IREPLCommandListener listener) {
		replCommandListeners.add(listener);
	}
	
	public void executeCommandsPlusREPLCommands(List<Command> commands) {
		ArrayList<String> toExecute = new ArrayList<>();
		for(Command c : commands) {
			if(c.getType() == CommandType.COMMAND)
				toExecute.add(c.getText());
			else if (c.getType() == CommandType.REPL_COMMAND) {
				
				executeCommands(toExecute);
				while(!currentExecution.isDone()) { //TODO, This is where the GUI freezes up 
					try {
						Thread.sleep(10);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				toExecute.clear();
				handleREPLCommand(c.getText());
			}
		}
		if(toExecute.size() > 0)
			executeCommands(toExecute);
	}
	
	private class ExecuteCommand extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent ev) {
			evaluate();
		}
	}
	
	protected class CommandExecutor extends SwingWorker<Void, Void> {

		private List<String> commands;
		
		public CommandExecutor(List<String> commands) {
			this.commands = commands;
		}
		
		public CommandExecutor(String command) {
			this.commands = new ArrayList<String>();
			this.commands.add(command);
		}
		
		public void beforeStart() {
			documentFilter.disableCompletely();
		}
		
		@Override
		protected Void doInBackground() {
			for(String command : commands) {
				setCommand(command);
				addBackgroundCommandMarker();
				addNewLine();
				
				doBeforeExecution(command);
				err.clear();
				AbstractResult result = evaluator.execute(command);
				
				//If we have an evaluator that returns everything via the streams, wait for it: 
				if(settings.getEvaluator().waitForOutput()) {
					while(!out.isReady() && !err.hasError()) {
						try {
							Thread.sleep(200);
						}
						catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				out.finish();
				if(err.hasError())
					err.finish();
				
				if(result != null) {
					if(result.hasError())
			    		err.write(result.getError());
			    	else {
			    		String resultString = result.toString();
			    		if(!resultString.equals("")) {
			    			handleOutputJunk();
			    			
			    			addResult(resultString);
			    			history.addResult(resultString);
			    		}
			    	}
				}
		    	doAfterExecution();
			}
			return null;
		}
		
		private void handleOutputJunk() {
			//To make things ugly
			if(onEmptyOutputLine()) //if a new line is written
				removeCommandMarker();
			else if (!onBlankLine()) //if output is written
				addNewLine();
		}
		
		@Override
		protected void done() {
			documentFilter.enableCompletely(); //TODO
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

package net.lappie.repl.history;

import java.util.ArrayList;
import java.util.List;

public class History {
	private ArrayList<Command> history = new ArrayList<>();
	
	public History() {
		
	}
	
	public List<Command> getList() {
		return history;
	}
	
	public void addCommand(String command) {
		history.add(new Command(command, CommandType.COMMAND));
	}
	
	public void addOutput(String output) {
		history.add(new Command(output, CommandType.OUTPUT));
	}
	
	public void addResult(String result) {
		history.add(new Command(result, CommandType.RESULT));
	}
	
	public void addError(String error) {
		history.add(new Command(error, CommandType.ERROR));
	}
	
	public void addMessage(String message) {
		history.add(new Command(message, CommandType.MESSAGE));
	}
	
	public void addREPLCommand(String command) {
		history.add(new Command(command, CommandType.REPL_COMMAND));
	}
	
	public void addREPLWarning(String wrn) {
		history.add(new Command(wrn, CommandType.REPL_WARNING));
	}
	
	public void addREPLError(String err) {
		history.add(new Command(err, CommandType.REPL_ERROR));
	}
}

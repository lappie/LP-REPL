package net.lappie.repl.functionallity.extensions;

import net.lappie.repl.ExtendedREPLPanel;

public class SearchExtension implements IREPLExtension {
	
	private final String SEARCH_COMMAND_SYMBOL = "search>";
	private ExtendedREPLPanel repl;
	
	public SearchExtension(ExtendedREPLPanel repl) {
		this.repl = repl;
	}
	
	@Override
	public void start() {
		repl.setMode(SEARCH_COMMAND_SYMBOL);
	}
	
	@Override
	public void close() {
		repl.resetMode();
		repl.removeAllHighlights();
		repl.showNormalStatus();
		repl.clearStatusMessage();
	}
	
	@Override
	public void update() {
		repl.removeAllHighlights();
		
		String result = repl.getResult();
		String command = repl.getTypedCommand();
		
		if(command.length() == 0) {
			repl.showNormalStatus();
			return;
		}
		
		int searchIndex = 0;
		int results = 0;
		while(true) {
			searchIndex = result.indexOf(command, searchIndex);
			if(searchIndex < 0)
				break;
			results++;
			repl.addHighlight(searchIndex, command.length());
			searchIndex++;
		}
		
		if(results == 0 && !command.equals(SEARCH_COMMAND_SYMBOL)) {
			repl.addStatusMessage("No results found");
			repl.showErrorStatus();
		}
		else {
			if(command.equals(SEARCH_COMMAND_SYMBOL))
				repl.addStatusMessage("");
			else
				repl.addStatusMessage(results + (results > 1 ? " results" : " result") + " found");
			repl.showNormalStatus();
		}
	}
	
	@Override
	public void execute() {
		
	}
	
	@Override
	public String getKeyCombination() {
		return "control F";
	}
	
}

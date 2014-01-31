package net.lappie.repl.functionallity.extensions;

import net.lappie.repl.functionallity.ImportHandler;

public class ImportREPLCommand implements IREPLCommandListener {

	private ImportHandler ih;
	private final String trigger = "Import: ";
	
	public ImportREPLCommand(ImportHandler ih) {
		this.ih = ih;
	}
	
	@Override
	public boolean match(String command) {
		return command.startsWith(trigger);
	}

	@Override
	public boolean execute(String command) {
		String file = command.substring(trigger.length());
		ih.executeImport(file);
		
		return false;
	}
	
}

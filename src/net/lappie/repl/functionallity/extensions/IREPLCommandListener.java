package net.lappie.repl.functionallity.extensions;

public interface IREPLCommandListener {
	public boolean match(String command);
	
	public boolean execute(String command);
}

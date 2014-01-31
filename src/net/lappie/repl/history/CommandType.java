package net.lappie.repl.history;

public enum CommandType {
	MESSAGE("message"), COMMAND("command"), OUTPUT("output"), ERROR("error"), RESULT("result"), 
		REPL_COMMAND("REPLCommand"), REPL_WARNING("REPLWarning"), REPL_ERROR("REPLError");
	
	private String type = "";
	
	CommandType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type;
	}
}

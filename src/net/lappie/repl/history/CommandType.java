package net.lappie.repl.history;

public enum CommandType {
	MESSAGE("message"), COMMAND("command"), OUTPUT("output"), ERROR("error");
	
	private String type = "";
	
	CommandType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type;
	}
}

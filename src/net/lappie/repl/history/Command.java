package net.lappie.repl.history;

public class Command {
	private CommandType type = CommandType.MESSAGE;
	
	private String text;
	
	public Command(String text, CommandType type) {
		this.text = text;
		this.type = type;
	}
	
	/**
	 * @return the command
	 */
	public String getText() {
		return text;
	}
	
	public CommandType getType() {
		return type;
	}
	
	public boolean isCommand() {
		return type.equals(CommandType.COMMAND);
	}
}

package net.lappie.repl.languages;


public abstract class AbstractResult {
	
	protected String error = null;
	
	public AbstractResult(String error) {
		this.error = error;
	}
	
	public boolean hasError() {
		return error != null;
	}
	
	public String getError() {
		return error;
	}
	
	@Override
	public abstract String toString();
}

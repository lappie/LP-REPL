package net.lappie.repl.functionallity.extensions;


public interface IREPLExtension {
	
	/**
	 * Return the keyCombination after which this extension starts running
	 */
	public String getKeyCombination();
	
	public void start();
	
	public void close();
	
	public void update();
	
	public void execute();
}

package net.lappie.repl.languages;

/**
 * An interface for a provider of help-text to a user corresponding to a function. 
 * @author Lappie
 */
public interface IFunctionHelpProvider {

	/**
	 * Perform a help action that will help the user with this specific function. 
	 * 
	 * TODO This return type suggests that it will always handle itself, however if the REPL could ever support 
	 *  dialogs/helpbubbels/... it would be best to return a (formatted?) String and let the REPL handle it. 
	 * @param functionName
	 */
	public void performFunctionHelp(String functionName); 
}

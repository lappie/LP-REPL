package net.lappie.repl.languages;



public interface ILanguageSettings
{
	public IEvaluator getEvaluator();
	
	/**
	 * Return the output that needs to be done to add to the REPL display when 
	 * a statement is not yet finished but is executed. 
	 * 
	 * Returns an empty string if nothing has to happen. 
	 * Use this for auto indentation. E.g. "\n\t";
	 * @return
	 */
	public String getPostUnfinishedStatement(); 
	
	/**
	 * @return true if the FunctionHelpProvider is defined
	 */
	public boolean hasFunctionHelpCommand();
	
	public IFunctionHelpProvider getFunctionHelpProvider();
	
	public String getFileExtention();
	public String getLanguageName();
	
	public boolean hasImport();
	
	public boolean hasWorkspace();
}

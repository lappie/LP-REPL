package net.lappie.repl.languages;



public interface ILanguageSettings
{
	public IEvaluator getEvaluator();
	
	public void load() throws Exception;
	
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
	
	/**
	 * Indicates if the output is handled in a different thread. 
	 * This means that the evaluator will not wait for a result to return. <- TODO
	 * @return
	 */
	public boolean parallelOutput();
	
	/**
	 * Ignore the first output since it is an echo of the input. 
	 * @return
	 */
	public boolean ignoreFirstOutput();
}

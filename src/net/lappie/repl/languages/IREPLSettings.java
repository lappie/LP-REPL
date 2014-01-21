package net.lappie.repl.languages;

import java.io.File;

import net.lappie.repl.languages.evaluator.IEvaluator;
import net.lappie.repl.languages.functionallity.IFunctionHelpProvider;

public interface IREPLSettings
{
	public IEvaluator getEvaluator();
	
	public String getSyntaxType();
	
	/**
	 * Return the output that needs to be done to add to the REPL display when 
	 * a statement is not yet finished but is executed. 
	 * 
	 * Returns an empty string if nothing has to happen. 
	 * Use this for auto indentation. E.g. "\n\t";
	 * @return
	 */
	public String getPostUnfinishedStatement(); 
	
	public boolean hasLoadModuleCommand();

	public String getLoadModuleCommand(File path);
	//streams?
	
	/**
	 * @return true if the FunctionHelpProvider is defined
	 */
	public boolean hasFunctionHelpCommand();
	
	public IFunctionHelpProvider getFunctionHelpProvider();
}

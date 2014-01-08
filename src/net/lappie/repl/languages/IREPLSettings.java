package net.lappie.repl.languages;

import java.io.File;

import net.lappie.repl.languages.evaluator.IEvaluator;
import net.lappie.repl.languages.functionallity.IFunctionHelpProvider;

public interface IREPLSettings
{
	public IEvaluator getEvaluator();
	
	public String getSyntaxType();
	
	public boolean hasLoadModuleCommand();

	public String getLoadModuleCommand(File path);
	//streams?
	
	/**
	 * @return true if the FunctionHelpProvider is defined
	 */
	public boolean hasFunctionHelpCommand();
	
	public IFunctionHelpProvider getFunctionHelpProvider();
}

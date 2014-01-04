package net.lappie.repl.languages;

import java.io.File;

import net.lappie.repl.languages.evaluator.IEvaluator;

public interface IREPLSettings
{
	public IEvaluator getEvaluator();
	
	public String getSyntaxType();
	
	public boolean hasLoadModuleCommand();

	public String getLoadModuleCommand(File path);
	//streams?
}

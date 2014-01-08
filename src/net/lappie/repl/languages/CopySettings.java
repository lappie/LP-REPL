package net.lappie.repl.languages;

import java.io.File;

import net.lappie.repl.languages.evaluator.CopyEvaluator;
import net.lappie.repl.languages.evaluator.IEvaluator;
import net.lappie.repl.languages.functionallity.IFunctionHelpProvider;

public class CopySettings implements IREPLSettings
{

	@Override
	public IEvaluator getEvaluator()
	{
		return new CopyEvaluator();
	}

	@Override
	public String getSyntaxType()
	{
		return null;
	}

	@Override
	public boolean hasLoadModuleCommand()
	{
		return false;
	}

	@Override
	public String getLoadModuleCommand(File path)
	{
		return null;
	}

	@Override
	public boolean hasFunctionHelpCommand() {
		return false;
	}

	@Override
	public IFunctionHelpProvider getFunctionHelpProvider() {
		return null;
	}
}

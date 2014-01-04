package net.lappie.repl.languages;

import java.io.File;

import net.lappie.repl.languages.evaluator.IEvaluator;
import net.lappie.repl.languages.evaluator.PythonEvaluator;

public class PythonSettings implements IREPLSettings
{
	@Override
	public IEvaluator getEvaluator()
	{
		return new PythonEvaluator();
	}

	@Override
	public String getSyntaxType()
	{
		return null;
	}

	@Override
	public boolean hasLoadModuleCommand()
	{
		return true;
	}

	@Override
	public String getLoadModuleCommand(File file)
	{
		String fileNameWithOutExt = file.getName().replaceFirst("[.][^.]+$", "");
		return "import sys\nsys.path.append('" + file.getParentFile().getAbsolutePath() + "')\n" + "from " + fileNameWithOutExt + " import *";
	}

}

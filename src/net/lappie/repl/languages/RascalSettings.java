package net.lappie.repl.languages;

import java.io.File;

import net.lappie.repl.languages.evaluator.IEvaluator;
import net.lappie.repl.languages.evaluator.RascalEvaluator;
import net.lappie.repl.languages.functionallity.IFunctionHelpProvider;
import net.lappie.repl.languages.functionallity.RascalFunctionHelpProvider;

public class RascalSettings implements IREPLSettings {
	
	private RascalFunctionHelpProvider functionHelpProvider = new RascalFunctionHelpProvider();
	
	@Override
	public IEvaluator getEvaluator() {
		return new RascalEvaluator();
	}

	@Override
	public String getSyntaxType() {
		return null;
	}

	@Override
	public boolean hasLoadModuleCommand() {
		return true;
	}

	@Override
	public String getLoadModuleCommand(File file) {
		String fileNameWithoutExt = file.getName()
				.replaceFirst("[.][^.]+$", "");
		return "import " + fileNameWithoutExt;
	}

	@Override
	public boolean hasFunctionHelpCommand() {
		return true;
	}

	@Override
	public IFunctionHelpProvider getFunctionHelpProvider() {
		return functionHelpProvider;
	}

	@Override
	public String getPostUnfinishedStatement() {
		return "\n\t"; //TODO Multiple tabs
	}

}

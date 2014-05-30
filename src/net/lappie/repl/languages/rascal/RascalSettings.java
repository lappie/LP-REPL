package net.lappie.repl.languages.rascal;

import net.lappie.repl.languages.IEvaluator;
import net.lappie.repl.languages.IFunctionHelpProvider;
import net.lappie.repl.languages.ILanguageSettings;

public class RascalSettings implements ILanguageSettings {
	
	private RascalFunctionHelpProvider functionHelpProvider = new RascalFunctionHelpProvider();
	private IEvaluator evaluator = new RascalEvaluator();

	@Override
	public IEvaluator getEvaluator() {
		return evaluator;
	}
	
	@Override
	public void load() {
		
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
	
	@Override
	public String getFileExtention() {
		return ".rsc";
	}

	@Override
	public String getLanguageName() {
		return "Rascal";
	}

	@Override
	public boolean hasImport() {
		return true;
	}

	@Override
	public boolean hasWorkspace() {
		return true;
	}

	@Override
	public boolean parallelOutput() {
		return false;
	}
	
	@Override
	public boolean ignoreFirstOutput() {
		return false;
	}
}

package net.lappie.repl.languages.jython;

import net.lappie.repl.languages.IEvaluator;
import net.lappie.repl.languages.IFunctionHelpProvider;
import net.lappie.repl.languages.ILanguageSettings;

public class JythonSettings implements ILanguageSettings {

	private JythonEvaluator evaluator = new JythonEvaluator();
	
	@Override
	public IEvaluator getEvaluator() {
		return evaluator;
	}

	@Override
	public void load() throws Exception {
		//nothing to do
	}

	@Override
	public String getPostUnfinishedStatement() {
		return "\t";
	}

	@Override
	public boolean hasFunctionHelpCommand() {
		return true;
	}

	@Override
	public IFunctionHelpProvider getFunctionHelpProvider() {
		return new JythonFunctionHelpProvider();
	}

	@Override
	public String getFileExtention() {
		return ".py";
	}

	@Override
	public String getLanguageName() {
		return "Jython";
	}

	@Override
	public boolean hasImport() {
		return false;
	}

	@Override
	public boolean hasWorkspace() {
		return false;
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

package net.lappie.repl.languages.jatha;

import net.lappie.repl.languages.IEvaluator;
import net.lappie.repl.languages.IFunctionHelpProvider;
import net.lappie.repl.languages.ILanguageSettings;

public class JathaSettings implements ILanguageSettings {
	
	private IEvaluator evaluator = new JathaEvaluator();
	
	@Override
	public IEvaluator getEvaluator() {
		return evaluator;
	}
	
	@Override
	public void load() throws Exception {
		
	}
	
	@Override
	public String getPostUnfinishedStatement() {
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
	
	@Override
	public String getFileExtention() {
		return ".lisp";
	}
	
	@Override
	public String getLanguageName() {
		return "Lisp";
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

package net.lappie.repl.languages;

import java.io.File;

import net.lappie.repl.languages.evaluator.IEvaluator;
import net.lappie.repl.languages.evaluator.LispEvaluator;

public class LispSettings implements IREPLSettings {

	@Override
	public IEvaluator getEvaluator() {
		return new LispEvaluator();
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
	public String getLoadModuleCommand(File path) {
		return "import " + path + ";";
	}

}

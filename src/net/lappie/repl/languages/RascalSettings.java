package net.lappie.repl.languages;

import java.io.File;

import net.lappie.repl.languages.evaluator.IEvaluator;
import net.lappie.repl.languages.evaluator.RascalEvaluator;

public class RascalSettings implements IREPLSettings {
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

}

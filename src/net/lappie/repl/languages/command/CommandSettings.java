package net.lappie.repl.languages.command;

import net.lappie.repl.languages.IEvaluator;
import net.lappie.repl.languages.IFunctionHelpProvider;
import net.lappie.repl.languages.ILanguageSettings;

public class CommandSettings  implements ILanguageSettings{

	@Override
	public IEvaluator getEvaluator() {
		return new CommandEvaluator(null);
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
		return null;
	}

	@Override
	public String getLanguageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasImport() {
		return false;
	}

	@Override
	public boolean hasWorkspace() {
		return false;
	}
	
}

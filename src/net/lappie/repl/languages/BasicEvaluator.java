package net.lappie.repl.languages;

import java.io.File;

public abstract class BasicEvaluator implements IEvaluator {

	@Override
	public boolean isComplete(String statement) {
		return true;
	}

	@Override
	public AbstractResult doImport(String module) {
		return null;
	}

	@Override
	public String getName() {
		return getLanguage();
	}
	
	@Override
	public void terminate() {
		
	}

	@Override
	public String getLanguageVersion() {
		return null;
	}
	
	@Override
	public void setWorkspace(File workspace) {
		
	}

	@Override
	public File getWorkspace() {
		return null;
	}
}

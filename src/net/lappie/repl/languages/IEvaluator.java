package net.lappie.repl.languages;

import java.io.File;

import net.lappie.repl.REPLErrorStream;
import net.lappie.repl.REPLOutputStream;


public interface IEvaluator
{	
	public boolean isComplete(String statement);
	
	public void clear();
	
	/**
	 * This function should execute the statement it is given. The function will give its output
	 * to either the set outputstream or errorstream. 
	 * 
	 * It is responsible for handling any error that might be given, and keeping track of its state
	 * if that is required of this evaluator. 
	 */
	public AbstractResult execute(String statement); 
	
	public void terminate();
	
	public AbstractResult doImport(String module);
	
	public String getName();
	
	public String getLanguage();
	
	public String getLanguageVersion();
	
	public void load(REPLOutputStream out, REPLErrorStream err);
	
	public void setWorkspace(File workspace);
	
	public File getWorkspace();
	
	/**
	 * Some evaluators don't return any output via the execute statement but return it directly (in another thread) to the
	 * output stream. 
	 * @return
	 */
	public boolean waitForOutput();
}
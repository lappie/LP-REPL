package net.lappie.repl.languages.evaluator;

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
	public EvalResult execute(String statement); 
	
	public String getName();
	
	public String getLanguage();
	
	public String getLanguageVersion();
	
	public void load(REPLOutputStream out, REPLErrorStream err);
}
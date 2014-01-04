package net.lappie.repl.languages.evaluator;

import net.lappie.repl.REPLErrorStream;
import net.lappie.repl.REPLOutputStream;

import org.jatha.Jatha;
import org.jatha.dynatype.LispValue;

public class LispEvaluator implements IEvaluator
{
	private Jatha lisp;
	private REPLOutputStream out;
	private REPLErrorStream err;

	public LispEvaluator()
	{
		lisp = new Jatha(false, false);
		lisp.init();
		lisp.start();
		
	}

	@Override
	public boolean completeStatement(String statement)
	{
		return true;
	}

	@Override
	public void clear()
	{

	}

	@Override
	public void execute(String statement)
	{
		try
		{
			LispValue result = lisp.eval(statement);
			out.write(result.toString());
		}
		catch (Exception e)
		{
			err.write(e.getLocalizedMessage());
		}
	}

	@Override
	public void setOutputStream(REPLOutputStream out)
	{
		this.out = out;
	}

	@Override
	public void setErrorStream(REPLErrorStream err)
	{
		this.err = err;
	}
	
	public String getName()
	{
		return "Lisp";
	}
	
	public String getLanguage()
	{
		return "Lisp";
	}
	
	public String getVersion()
	{
		return "0.1";
	}
}

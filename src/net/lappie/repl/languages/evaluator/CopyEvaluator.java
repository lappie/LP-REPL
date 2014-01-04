package net.lappie.repl.languages.evaluator;

import net.lappie.repl.REPLErrorStream;
import net.lappie.repl.REPLOutputStream;

public class CopyEvaluator implements IEvaluator
{
	private REPLOutputStream out;
	private REPLErrorStream err;

	@Override
	public boolean completeStatement(String statement)
	{
		return true;
	}

	@Override
	public void clear()
	{
		//there is no environment, nothing to do here. 
	}

	@Override
	public void execute(String statement)
	{
		if(statement.equals("error"))
			err.write(statement);
		else
			out.print(statement);
	}

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
		return "Copy evaluator";
	}
	
	public String getLanguage()
	{
		return "Copy";
	}
	
	public String getVersion()
	{
		return "0.1";
	}
}

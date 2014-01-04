package net.lappie.repl.languages.evaluator;

import java.io.IOException;
import java.io.OutputStream;

import net.lappie.repl.REPLErrorStream;
import net.lappie.repl.REPLOutputStream;

import org.python.util.InteractiveInterpreter;

public class PythonEvaluator implements IEvaluator
{
	private InteractiveInterpreter python = new InteractiveInterpreter();
	private REPLOutputStream out = null;
	private REPLErrorStream err = null;

	private OutputStream dummyOut = new DummyOutputStream();

	@Override
	public boolean completeStatement(String statement)
	{
		//TODO: make flush system!
		python.setOut(dummyOut);
		python.setErr(dummyOut);
		boolean result = python.runsource(statement);
		python.setOut(out);
		python.setErr(err);

		return !result;
	}

	@Override
	public void clear()
	{
		python.cleanup();
	}

	@Override
	public void execute(String statement)
	{
		try
		{
			boolean result = python.runsource(statement);
			if(result)
			{
				String[] statements = statement.split("\\n");
				for(String s : statements)
				{
					python.runsource(s);
				}
			}
		}
		catch (Exception e)
		{
			//error will already be written.
			e.printStackTrace();
		}
	}

	@Override
	public void setOutputStream(REPLOutputStream out)
	{
		out.setTrim(true);
		this.out = out;
		python.setOut(out);
	}

	@Override
	public void setErrorStream(REPLErrorStream err)
	{
		this.err = err;
		python.setErr(err);
	}

	@Override
	public String getName()
	{
		return "Python";
	}

	@Override
	public String getLanguage()
	{
		return "Python";
	}

	@Override
	public String getVersion()
	{
		return "0.1";
	}

	private class DummyOutputStream extends OutputStream
	{
		@Override
		public void write(int b) throws IOException
		{
			return;
		}
	}

	public void test()
	{
		InteractiveInterpreter python = new InteractiveInterpreter();

		python.setOut(new DummyOutputStream());
		python.setErr(new DummyOutputStream());

		System.out.println(python.runsource("x=4\ny=5"));
		System.out.println(python.runsource("3+3+"));
		System.out.println(python.runsource("(3+3)"));
		System.out.println(python.runsource("(3+3"));
		System.out.println(python.runsource("def y(s):\n\tprint(s)\n"));
	}

	public static void main(String args[])
	{
		PythonEvaluator py = new PythonEvaluator();
		py.test();
	}
}

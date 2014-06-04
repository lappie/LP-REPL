package net.lappie.repl.languages.jython;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import net.lappie.repl.REPLErrorStream;
import net.lappie.repl.REPLOutputStream;
import net.lappie.repl.languages.AbstractResult;
import net.lappie.repl.languages.IEvaluator;

import org.python.util.InteractiveInterpreter;

public class JythonEvaluator implements IEvaluator {

	private InteractiveInterpreter python = new InteractiveInterpreter();
	private REPLOutputStream out = null;
	private REPLErrorStream err = null;

	private OutputStream dummyOut = new DummyOutputStream();

	
	@Override
	public boolean isComplete(String statement) {
		//TODO: make flush system!
		python.setOut(dummyOut);
		python.setErr(dummyOut);
		boolean result = python.runsource(statement);
		python.setOut(out);
		python.setErr(err);
		
		return !result;
	}

	@Override
	public void clear() {
		python.cleanup();
	}

	@Override
	public AbstractResult execute(String statement) {
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
			//e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean terminate() {
		return false;
	}

	@Override
	public AbstractResult doImport(String module) {
		return null;
	}

	@Override
	public String getName() {
		return "Python (Jython)";
	}

	@Override
	public String getLanguage() {
		return "Python";
	}

	@Override
	public String getLanguageVersion() {
		return null;
	}

	@Override
	public void load(REPLOutputStream out, REPLErrorStream err) {
		//No output trimm? TODO 
		this.out = out;
		this.err = err;
		
		python.setOut(out);
		python.setErr(err);
	}

	@Override
	public void setWorkspace(File workspace) {
	}

	@Override
	public File getWorkspace() {
		return null;
	}

	@Override
	public boolean waitForOutput() {
		return false;
	}

	@Override
	public void close() {
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
		JythonEvaluator py = new JythonEvaluator();
		py.test();
	}
	
}

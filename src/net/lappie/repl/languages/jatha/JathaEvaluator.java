package net.lappie.repl.languages.jatha;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import net.lappie.repl.REPLErrorStream;
import net.lappie.repl.REPLOutputStream;
import net.lappie.repl.languages.AbstractResult;
import net.lappie.repl.languages.IEvaluator;

import org.jatha.Jatha;
import org.jatha.dynatype.LispValue;

public class JathaEvaluator implements IEvaluator {

	private Jatha lisp;
	private REPLOutputStream out;
	private REPLErrorStream err;

	public JathaEvaluator()
	{
		clear();
	}
	
	@Override
	public boolean isComplete(String statement) {
		return true;
	}

	@Override
	public void clear() {
		lisp = new Jatha(false, false);
		lisp.init();
		lisp.start();
	}

	@Override
	public AbstractResult execute(String statement) {
		try
		{
			DummyOutputStream dummyErr = new DummyOutputStream();
			PrintStream orgErr = System.err;
			System.setErr(new PrintStream(dummyErr));
			LispValue result = lisp.eval(statement);
			System.setErr(orgErr);
			
			if(!dummyErr.didPrint())
				out.write(result.toString());
			else
				err.write(result.toString());
		}
		catch (Exception e)
		{
			err.write(e.getLocalizedMessage());
		}
		return null;
	}

	@Override
	public boolean terminate() {
		return false;
	}

	@Override
	public AbstractResult doImport(String module) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "Lisp (Jatha)";
	}

	@Override
	public String getLanguage() {
		return "Lisp";
	}

	@Override
	public String getLanguageVersion() {
		return null;
	}

	@Override
	public void load(REPLOutputStream out, REPLErrorStream err) {
		this.out = out;
		this.err = err;
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
		lisp.exit();
	}
	
	private class DummyOutputStream extends OutputStream
	{
		private boolean printed = false;
		
		@Override
		public void write(int b) throws IOException
		{
			printed = true;
		}
		
		public boolean didPrint() {
			return printed;
		}
	}
	
}

package net.lappie.repl.languages.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import net.lappie.repl.IREPLOutputStream;
import net.lappie.repl.REPLErrorStream;
import net.lappie.repl.REPLOutputStream;
import net.lappie.repl.languages.AbstractResult;
import net.lappie.repl.languages.BasicEvaluator;

public class CommandEvaluator extends BasicEvaluator {

	private ProcessBuilder processBuilder;
	private Process process;
	private PrintWriter outputWriter;
	
	public CommandEvaluator(String command) {
		String commands[] = command.split("\\s+"); 
		try {
			processBuilder = new ProcessBuilder(commands);
			process = processBuilder.start();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OutputStream outStream = process.getOutputStream();
		outputWriter = new PrintWriter(outStream);
	}

	@Override
	public void clear() {
		process.destroy();
		try {
			process = processBuilder.start();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public AbstractResult execute(String statement) {
		outputWriter.println(statement + "\n"); // Some evaluators need a newline at the end to work properly
		outputWriter.flush();
		return null;
	}

	@Override
	public String getLanguage() {
		return "Any command language";
	}

	@Override
	public void load(REPLOutputStream out, REPLErrorStream err) {
		StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), out);
		outputGobbler.start();
		
		StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), err);
		errorGobbler.start();
	}
	
	private static class StreamGobbler extends Thread
	{
		private InputStream is;
		
		private BufferedReader br;
		
		private IREPLOutputStream out;
		
		StreamGobbler(InputStream is, IREPLOutputStream out)
		{
			this.is = is;
			this.out = out;
			
			InputStreamReader isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
		}
		
		@Override
		public void run()
		{
			try
			{
				String line = null;
				while ((line = br.readLine()) != null) {
					out.write(line);
				}
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}

	@Override
	public boolean waitForOutput() {
		return true;
	}
}

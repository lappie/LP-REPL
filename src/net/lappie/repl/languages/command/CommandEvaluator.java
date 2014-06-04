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

class CommandEvaluator extends BasicEvaluator {

	private ProcessBuilder processBuilder;
	private Process process;
	private PrintWriter outputWriter;
	
	CommandEvaluator(String command) throws IOException {
		//String commands[] = command.split("\\s+"); 
		
		processBuilder = new ProcessBuilder(command);
		process = processBuilder.start();
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
		private BufferedReader br;
		
		private IREPLOutputStream out;
		
		StreamGobbler(InputStream is, IREPLOutputStream out)
		{
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
	
	@Override
	public void close() {
		System.out.println("Closing..");
		if(process != null)
			process.destroy();	
	}
}

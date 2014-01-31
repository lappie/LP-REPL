package net.lappie.repl;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class is an Outputstream so that evaluator can write something when the user uses the print-function.
 * 
 * The write(type, value) method is used for printing the results.
 * 
 * @author Lappie
 * 
 */
public class REPLOutputStream extends OutputStream {
	
	private BasicREPLPanel panel;
	
	private boolean trim = false;
	
	private String output = ""; //this will hold what is printed as a total for saving in the history
	private String stack = ""; //this will hold a stack, to optimize printing
	
	private final int PRINT_STEP = 100;
	
	public REPLOutputStream(BasicREPLPanel panel) {
		this.panel = panel;
	}
	
	@Override
	public void write(int b) throws IOException {
		write(Integer.toString(b));
	}
	
	@Override
	public void write(byte[] b) {
		String s = new String(b);
		write(s);
	}
	
	@Override
	public void write(byte[] b, int off, int len) {
		String s = new String(b, off, len);
		if (trim) {
			if (s.endsWith("\n")) s = s.substring(0, s.length() - 2);
			s.trim();
		}
		write(s);
	}
	
	
	
	public void write(String output) {
		if (panel == null)
			return;
		
		stack += output;
		if (stack.length() < PRINT_STEP)
			return;
		forceWriteStack();
	}
	
	private void forceWriteStack() {
		for (int i = 0; i < stack.length(); i += PRINT_STEP) { // Long text in pieces
			int endIndex = i + PRINT_STEP < stack.length() ? i + PRINT_STEP : stack.length();
			String sub = stack.substring(i, endIndex);
			panel.addOutput(sub);
		}
		output = new String(stack);
		stack = "";
	}
	
	public void print(String s) {
		write(s);
	}
	
	public void println(String s) {
		write(s);
	}
	
	public void setTrim(boolean on) {
		trim = on;
	}
	
	public void myFlush() {
		forceWriteStack();
		panel.getHistory().addOutput(output);
		output = "";
	}
}

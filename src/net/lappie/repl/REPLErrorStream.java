package net.lappie.repl;

import java.io.IOException;

public class REPLErrorStream extends IREPLOutputStream {
	private BasicREPLPanel panel;
	private String stack = "";
	
	public REPLErrorStream(BasicREPLPanel panel) {
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
		write(s);
	}
	
	@Override
	public void write(String error) {
		System.out.println("Err: " + error);
		if(!stack.isEmpty())
			stack += "\n";
		stack += error;
	}
	
	@Override
	public boolean isReady() {
		return !stack.isEmpty();
	}
	
	public boolean hasError() {
		return !stack.isEmpty();
	}
	
	public void finish() {
		panel.getHistory().addError(stack);
		panel.addError(stack);
		stack = "";
	}
	
	public void clear() {
		stack = "";
	}
}

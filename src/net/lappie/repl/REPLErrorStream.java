package net.lappie.repl;

import java.io.IOException;
import java.io.OutputStream;

public class REPLErrorStream extends OutputStream {
	private BasicREPLPanel panel;
	
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
	
	public void write(String error) {
		panel.getHistory().addError(error);
		panel.addError(error);
	}
}

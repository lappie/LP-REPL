package net.lappie.repl;

import java.io.IOException;
import java.io.OutputStream;

public class REPLOutputStream extends OutputStream {
	private BasicREPLPanel panel;
	
	private boolean trim = false;
	
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
		if(panel == null)
			return;
		panel.addOutput(output);
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
}

package net.lappie.repl;

import java.io.OutputStream;

public abstract class IREPLOutputStream extends OutputStream{
	
	public abstract void write(String line);
	
	public abstract boolean isReady();
}

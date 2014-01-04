package net.lappie.repl.functionallity;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.lappie.repl.BasicREPLPanel;
import net.lappie.repl.functionallity.extensions.IREPLExtension;

public class REPLDocumentListener implements DocumentListener {
	
	private IREPLExtension e = null;
	
	private SyntaxHighlightParser parser;
	private BasicREPLPanel repl;
	
	public REPLDocumentListener(BasicREPLPanel repl, SyntaxHighlightParser parser) {
		this.parser = parser;
		this.repl = repl;
	}
	
	public void loadExtension(IREPLExtension e) {
		this.e = e;
		this.e.start();
	}
	
	public void stopExtension() {
		if(e == null)
			return;
		e.close();
		e = null;
	}
	
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		//parser.parseCommand(repl.getCommandIndex(), repl.getCommand());
		if(e != null)
			e.update();
	}
	
	@Override
	public void insertUpdate(DocumentEvent arg0) {
		
		parser.parseCommand(repl.getCommandIndex(), repl.getCommand());
		if(e != null)
			e.update();
	}
	
	@Override
	public void removeUpdate(DocumentEvent arg0) {
		parser.parseCommand(repl.getCommandIndex(), repl.getCommand());
		if(e != null)
			e.update();
	}
}

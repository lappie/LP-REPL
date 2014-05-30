package net.lappie.repl;

import java.io.IOException;

import net.lappie.repl.functionallity.FoldedTextRepository;

/**
 * This class is an Outputstream so that evaluator can write something when the user 
 * uses the print-function.
 * 
 * This class will hold all output until finish() is called to prevent the JTextComponent of 
 * overworking
 * 
 * @author Lappie
 * 
 */
public class REPLOutputStream extends IREPLOutputStream {
	
	private BasicREPLPanel panel;
	private FoldedTextRepository textRepo;
	
	private boolean trim = false;
	
	private String stack = ""; //this will hold a stack, to optimize printing
	private String foldedStack = "";
	
	public static final String FOLD_SYMBOL = " <<";
	public static final String UNFOLD_SYMBOL = "...";
	
	private int printedIndex = 0;
	
	public REPLOutputStream(BasicREPLPanel panel) {
		this.panel = panel;
	}
	
	public void addFoldedTextRepo(FoldedTextRepository textRepo) {
		this.textRepo = textRepo;
	}
	
	@Override
	public void write(int b) throws IOException {
		write(Integer.toString(b));
	}
	
	@Override
	public void write(byte[] b) {
		write(new String(b));
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
	
	@Override
	public void write(String output) {
		System.out.println("Out: " + output);
		if (panel == null)
			return;
		if(output.equals(""))
			return;
		printedIndex++;
		if(panel.getSettings().ignoreFirstOutput())
			if(printedIndex == 1)
				return;
		if(stack != "")
			stack += "\n";
		stack += output;
	}
	
	
	private boolean trimUntilLimit() {
		boolean trimmed = false;
		foldedStack = new String(stack);
		//trim on max chars
		if(stack.length() > Settings.MAX_OUTPUT_CHARS) {
			foldedStack = foldedStack.substring(0, Settings.MAX_OUTPUT_CHARS);
			trimmed = true;
		}
		
		//trim on nr of lines
		String lines[] = foldedStack.split("\n", Settings.MAX_OUTPUT_LINES+1);
		if(lines.length > 5) {
			foldedStack = "";
			for(int i = 0; i < Settings.MAX_OUTPUT_LINES; i++)
				foldedStack += lines[i] + "\n";
			foldedStack = foldedStack.substring(0, foldedStack.length()-1); //remove last new line
			trimmed = true;
		}
		return trimmed;
	}
	
	private void writeTotal() {
		if(stack.length() == 0)
			return;
		
		boolean trimmed = trimUntilLimit();
		panel.addOutput(foldedStack);
		if(trimmed) {
			int offset = panel.getTotalLength();
			panel.addClickableText(UNFOLD_SYMBOL);
			textRepo.addFoldedText(offset, stack.substring(foldedStack.length()));
		}
	}
	
	public void setTrim(boolean on) {
		trim = on;
	}
	
	/**
	 * Use this to flush any output that is left behind. 
	 * 
	 * Other than flush() since that workes on a lower level. (And can't be overwritten because
	 * that would flush on the wrong times)
	 */
	public void finish() {
		writeTotal();
		panel.getHistory().addOutput(stack);
		stack = "";
		printedIndex = 0;
	}
	
	@Override
	public boolean isReady() {
		return !stack.isEmpty();
	}
}

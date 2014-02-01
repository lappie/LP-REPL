package net.lappie.repl.functionallity;

/**
 * In normal cases for a REPL, all text above the commandmarker does not change. However
 * there are cases, like code-folding, when this does happen. This might disrupt any
 * functionallity for classes that hold an offset. 
 * 
 * These listeners are called when text is changed above the command marker. 
 */
public interface IOffsetListener {
	
	/**
	 * This method is called when a change has occured in the REPL above the command marker. 
	 * @param offset The place where text was inserted/removed
	 * @param length The amount of text that was inserted/removed. Positive if inserted, negative if removed. 
	 */
	public void fire(int offset, int length);
}

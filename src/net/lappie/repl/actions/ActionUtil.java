package net.lappie.repl.actions;

public class ActionUtil
{
	public static String getHelpText()
	{
		return "Welcome to the LP-REPL. Currently only Rascal is supported, but other\n languages are coming soon.\n" +
				"\n" +
				"Some tips to get you started:\n" +
				" - History: CTRL + UP, CTRL + DOWN\n" +
				" - History completion: TAB\n" + 
				" - Search in the REPL: CTRL + F\n" +
				" - Save session: CTRL + S\n" +
				" - Open session: CTRL + O\n" +
				" - Select some text and press CTRL+ENTER to evaluate it\n" + 
				"\n" +
				"Have fun!";
	}
}

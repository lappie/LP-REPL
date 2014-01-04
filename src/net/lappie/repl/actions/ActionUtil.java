package net.lappie.repl.actions;

public class ActionUtil
{
	public static String getHelpText()
	{
		return "Welcome to the new REPL for Rascal. You can make a REPL per project by rightclicking on " +
				"a project file and click 'Start REPL'.\n" +
				"\n" +
				"Some tips:\n" +
				" - History: CTRL + UP, CTRL + DOWN\n" +
				" - Search results: CTRL + F\n" +
				" - Save history: CTRL + S\n" +
				" - Open history: CTRL + O\n" +
				" - Select some text and press CTRL+ENTER to evaluate it\n" + 
				"\n" +
				"Have fun!";
	}
}

package net.lappie.repl.eclipse.keys;

import net.lappie.repl.plugin.views.REPLView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class SearchHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		//Use this class later for nice tools: HandlerUtil
		
		REPLView.getREPL().startSearch();
		
		return null;
	}
	
}

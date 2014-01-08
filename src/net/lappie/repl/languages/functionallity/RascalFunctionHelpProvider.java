package net.lappie.repl.languages.functionallity;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class RascalFunctionHelpProvider implements IFunctionHelpProvider{
	
	private final String BASE = "http://tutor.rascal-mpl.org/Rascal/Rascal.html";
	private HashMap<String, String> functionMap = new HashMap<>();
	
	public RascalFunctionHelpProvider() {
		
		//private final String BASE = "http://127.0.0.1:9000/Rascal/Rascal.html";
		//Tutor tutor = new Tutor(); //For now a bit too much to start our own server, just for this. Also: How do we stop this. 
		
		//until we have a better way to fill it, we'll do it manually:
		functionMap.put("visit", "#/Rascal/Expressions/Visit/Visit.html");
		
	}
	
	private void openBrowser(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	@Override
	public void performFunctionHelp(String functionName) {
		try {
			if(functionMap.containsKey(functionName)) {
				URI uri = new URI(BASE + functionMap.get(functionName));
				openBrowser(uri);
			}
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}

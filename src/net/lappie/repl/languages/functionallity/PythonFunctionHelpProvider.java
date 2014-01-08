package net.lappie.repl.languages.functionallity;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;

public class PythonFunctionHelpProvider implements IFunctionHelpProvider {
	
	private final String BASE = "http://docs.python.org/2/library/functions.html#";
	private HashSet<String> functionSet = new HashSet<>(Arrays.asList("abs", "divmod", "input", "open", "staticmethod", "all", "enumerate", "int", "ord", "str", "any", "eval", "isinstance", "pow", "sum", "basestring", "execfile", "issubclass", "print", "super", "bin", "file", "iter", "property", "tuple", "bool", "filter", "len", "range", "type", "bytearray", "float", "list", "raw_input", "unichr", "callable", "format", "locals", "reduce", "unicode", "chr", "frozenset", "long", "reload", "vars", "classmethod", "getattr", "map", "repr", "xrange", "cmp", "globals", "max", "reversed", "zip", "compile", "hasattr", "memoryview", "round", "__import__", "complex", "hash", "min", "set", "apply", "delattr", "help", "next", "setattr", "buffer", "dict", "hex", "object", "slice", "coerce", "dir", "id", "oct", "sorted", "intern"));
	
	private void openBrowser(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void performFunctionHelp(String functionName) {
		try {
			if (functionSet.contains(functionName)) {
				URI uri = new URI(BASE + functionName);
				openBrowser(uri);
			}
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}

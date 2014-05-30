package net.lappie.repl.languages.command;

import java.io.File;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.lappie.repl.languages.IEvaluator;
import net.lappie.repl.languages.IFunctionHelpProvider;
import net.lappie.repl.languages.ILanguageSettings;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CommandSettings  implements ILanguageSettings{

	private File file;
	private String name = "";
	private CommandEvaluator evaluator = null;
	
	public CommandSettings(File file) {		
		this.file = file;
	}
	
	@Override
	public void load() throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		
		dBuilder = dbFactory.newDocumentBuilder();
		
		Document doc = dBuilder.parse(file);
		
		NodeList commands = doc.getElementsByTagName("command");
		if(commands.getLength() == 0)
			throw new XMLParseException();
		Node command = commands.item(0);
		evaluator = new CommandEvaluator(command.getTextContent()); 
		
		NodeList names = doc.getElementsByTagName("name");
		if(names.getLength() == 0)
			throw new XMLParseException();
		this.name = names.item(0).getTextContent();
	}
	
	@Override
	public IEvaluator getEvaluator() {
		return evaluator;
	}

	@Override
	public String getPostUnfinishedStatement() {
		return null;
	}

	@Override
	public boolean hasFunctionHelpCommand() {
		return false;
	}

	@Override
	public IFunctionHelpProvider getFunctionHelpProvider() {
		return null;
	}

	@Override
	public String getFileExtention() {
		return null;
	}

	@Override
	public String getLanguageName() {
		return name;
	}

	@Override
	public boolean hasImport() {
		return false;
	}

	@Override
	public boolean hasWorkspace() {
		return false;
	}
	
	@Override
	public boolean parallelOutput() {
		return true;
	}
	
	@Override
	public boolean ignoreFirstOutput() {
		return true;
	}
	
}

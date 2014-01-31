package net.lappie.repl.history;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.lappie.repl.BasicREPLPanel;
import net.lappie.repl.ExtendedREPLPanel;
import net.lappie.repl.languages.IEvaluator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLSessionParser {
	private static Element getEnvironmentElement(Document doc) {
		Element environment = doc.createElement("environment");
		environment.setAttribute("OS-name", System.getProperty("os.name"));
		environment.setAttribute("OS-version", System.getProperty("os.version"));
		environment.setAttribute("OS-architecture", System.getProperty("os.arch"));
		environment.setAttribute("java-version", System.getProperty("java.version"));
		
		return environment;
	}
	
	private static Element getEvaluatorElement(IEvaluator evaluator, Document doc) {
		Element evaluatorElement = doc.createElement("evaluator");
		evaluatorElement.setAttribute("version", evaluator.getLanguageVersion());
		evaluatorElement.setAttribute("language", evaluator.getLanguage());
		evaluatorElement.setTextContent(evaluator.getName());
		
		return evaluatorElement;
	}
	
	private static Element getFileElement(Document doc) {
		Element fileElement = doc.createElement("file");
		
		Date now = new Date();
		fileElement.setAttribute("time", new SimpleDateFormat("HH:mm").format(now));
		fileElement.setAttribute("date", new SimpleDateFormat("YYYY-MM-dd").format(now));
		
		return fileElement;
	}
	
	public static boolean exportHistory(BasicREPLPanel repl, File exportFile) {
		boolean success = true;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			Element lpREPL = doc.createElement("lp-repl");
			doc.appendChild(lpREPL);
			
			// root elements
			lpREPL.appendChild(getEnvironmentElement(doc));
			lpREPL.appendChild(getEvaluatorElement(repl.getEvaluator(), doc));
			lpREPL.appendChild(getFileElement(doc));
			
			// history
			
			Element historyElement = doc.createElement("history");
			lpREPL.appendChild(historyElement);
			
			for (Command run : repl.getHistory().getList()) {
				Element runElement = doc.createElement(run.getType().toString());
				historyElement.appendChild(runElement);
				runElement.setTextContent(run.getText());
				//runElement.setAttribute("type", run.getType().toString());
			}
			
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(exportFile);
			
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		catch (TransformerException e) {
			e.printStackTrace();
		}
		return success;
	}
	
	public static boolean importHistory(ExtendedREPLPanel repl, File importFile) {
		boolean success = true;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			
			dBuilder = dbFactory.newDocumentBuilder();
			
			Document doc = dBuilder.parse(importFile);
			
			// optional, but recommended
			// read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			// doc.getDocumentElement().normalize();
			
			Node evaluatorNode = doc.getElementsByTagName("evaluator").item(0);
			if (evaluatorNode.getNodeType() == Node.ELEMENT_NODE) {
				Element evaluatorElement = (Element) evaluatorNode;
				String language = evaluatorElement.getAttribute("language");
				String version = evaluatorElement.getAttribute("version");
				// show message if they don't match.
				if(!repl.getEvaluator().getLanguage().equals(language)) {
					String warning = "Warning: Language does not match running-language";
					repl.addStatusMessage(warning);
					repl.displayREPLWarning(warning);
					success = false;
				}
				else if(!repl.getEvaluator().getLanguageVersion().equals(version)) {
					String warning = "Warning: Evaluator-version does not match";
					repl.addStatusMessage(warning);
					repl.displayREPLWarning(warning);
					success = false;
				}
			}
			
			NodeList historyList = doc.getElementsByTagName("history");
			if(historyList.getLength() == 0)
				return false;
			Node history = historyList.item(0);
			ArrayList<Command> commands = new ArrayList<>();
			historyList = history.getChildNodes();
			
			for (int i = 0; i < historyList.getLength(); i++) {
				Node node = historyList.item(i);

				if(node.getNodeName().equals(CommandType.COMMAND.toString())) {
					Command command = new Command(node.getTextContent(), CommandType.COMMAND);
					commands.add(command);
				}
				else if(node.getNodeName().equals(CommandType.REPL_COMMAND.toString())) {
					Command command = new Command(node.getTextContent(), CommandType.REPL_COMMAND);
					commands.add(command);
				}
			}
			repl.executeCommandsPlusREPLCommands(commands);
		}
		catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}
}

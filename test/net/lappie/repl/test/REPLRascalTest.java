package net.lappie.repl.test;
import static org.junit.Assert.fail;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import net.lappie.repl.ExtendedREPLPanel;
import net.lappie.repl.history.XMLSessionParser;
import net.lappie.repl.languages.rascal.RascalSettings;

import org.junit.Test;
import org.rascalmpl.library.experiments.resource.resources.file.File;

/**
 * This class will test the REPL using Rascal as its language
 * @author Lappie
 *
 */
public class REPLRascalTest {
	
	private ExtendedREPLPanel repl = new ExtendedREPLPanel(new RascalSettings());
	private Robot robot;
	
	public REPLRascalTest() {
		try {
			robot = new Robot();
		}
		catch (AWTException e) {
			fail("robot could not be created");
		}
	}
	
	@Test
	public void evaluator() throws InterruptedException {
		repl.setCommand("6*7");
		robot.keyPress(KeyEvent.VK_ENTER);
		Thread.sleep(1000); //second should be long enough before result is returned
		assert(repl.getResult().endsWith("42"));
	}
	
	@Test
	public void loadSession() {
		File f = new File();
		
		XMLSessionParser.importHistory(repl, null);
	}
	
}

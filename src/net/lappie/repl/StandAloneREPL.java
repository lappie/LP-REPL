package net.lappie.repl;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.lappie.repl.actions.ActionUtil;
import net.lappie.repl.functionallity.ImportHandler;
import net.lappie.repl.functionallity.extensions.SearchExtension;
import net.lappie.repl.history.XMLSessionParser;
import net.lappie.repl.languages.ILanguageSettings;
import net.lappie.repl.languages.rascal.RascalSettings;

/**
 * Creates a REPL with full functionallity running in a JFrame. 
 * @author Lappie
 *
 */
public class StandAloneREPL {
	private JFrame frame;
	private ExtendedREPLPanel replPanel;
	private ILanguageSettings settings;
	
	private ImportHandler importHandler;
	
	private final String clearIconLoc = Settings.BASE + "display16.png";
	private final String importIconLoc = Settings.BASE + "load16.png";
	private final String reloadImportIconGreenLoc = Settings.BASE + "reload-imports-green-16.png";
	private final String rascalIconLoc = Settings.BASE + "rascal48.gif";

	public StandAloneREPL() {
		frame = new JFrame();

		replPanel = createREPLPanel(); //create first, necessary for other functions
		replPanel.addExtension(new SearchExtension(replPanel));
		importHandler = new ImportHandler(replPanel, settings);

		JPanel buttonPanel = createButtonPanel();
		loadKeyActions();
		JPanel mainPanel = new JPanel(new BorderLayout());

		mainPanel.add(buttonPanel, BorderLayout.NORTH);
		mainPanel.add(replPanel, BorderLayout.CENTER);

		frame.setContentPane(mainPanel);

		frame.setTitle("LP-REPL"
				+ (replPanel.getName().length() > 0 ? ": "
						+ replPanel.getName() : ""));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		
		frame.setLocationRelativeTo(null);

		// Make textField get the focus whenever frame is activated.
		frame.addWindowFocusListener(new WindowAdapter() { //TODO
			@Override
			public void windowGainedFocus(WindowEvent e) {
				replPanel.requestFocusInWindow();
			}
		});
		
		frame.setJMenuBar(createMenuBar());

		frame.setVisible(true);
	}
	
	private JMenuBar createMenuBar() {
		//Create the menu bar.
		JMenuBar menuBar = new JMenuBar();

		JMenuItem item;
		
		//////////// SESSION ////////////
		JMenu session = new JMenu("Session");
		session.setMnemonic(KeyEvent.VK_S);
		
		item = new JMenuItem("Clear");
		item.addActionListener(new ClearAction());
		session.add(item);
		
		item = new JMenuItem("Load session");
		item.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		item.addActionListener(new LoadAction());
		session.add(item);
		
		item = new JMenuItem("Save session");
		item.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		item.addActionListener(new SaveAction());
		session.add(item);
		
		menuBar.add(session);
		
		/////////// LANGUAGES ///////////
		JMenu languages = new JMenu("Languages");
		languages.setMnemonic(KeyEvent.VK_L);
		
		ButtonGroup group = new ButtonGroup();
		item = new JRadioButtonMenuItem("Rascal");
		item.setSelected(true);
		group.add(item);
		
		languages.add(item);
		menuBar.add(languages);
		
		/////////// ABOUT /////////////////
		JMenu about = new JMenu("About");
		about.setMnemonic(KeyEvent.VK_A);
		
		item = new JMenuItem("Help");
		item.addActionListener(new HelpAction());
		about.add(item);
		
		item = new JMenuItem("About");
		item.addActionListener(new AboutAction());
		about.add(item);
		
		menuBar.add(about);
		
		return menuBar;
	}

	private ExtendedREPLPanel createREPLPanel() {
		//settings = new LispSettings();
		//settings = new PythonSettings();
		//settings = new CopySettings();
		settings = new RascalSettings();
		
		//Loading settings: 
		String workspaceLoc = Settings.getProperty("workspace");
		if(workspaceLoc != null) {
			File workspace = new File(Settings.getProperty("workspace"));
			settings.getEvaluator().setWorkspace(workspace);
		}
		
		return new ExtendedREPLPanel(settings);
	}

	private void loadKeyActions() {
		replPanel.addKeyAction("F1", new HelpAction());
		replPanel.addKeyAction("ctrl S", new SaveAction());
		replPanel.addKeyAction("ctrl O", new LoadAction());
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		URL url = StandAloneREPL.class.getResource(clearIconLoc);
		Icon clear = new ImageIcon(url);
		JButton clearButton = new JButton(clear);
		clearButton.addActionListener(new ClearAction());
		clearButton.setToolTipText("Clear screen");

		JButton importButton = null;
		JButton reloadImportButton = null;
		if (settings.hasImport()) {
			url = StandAloneREPL.class.getResource(importIconLoc);
			Icon importIcon = new ImageIcon(url);
			importButton = new JButton(importIcon);
			importButton.addActionListener(new ImportAction());
			importButton.setToolTipText("Import file");
			
			url = StandAloneREPL.class.getResource(reloadImportIconGreenLoc);
			Icon reloadImportIcon = new ImageIcon(url);
			reloadImportButton = new JButton(reloadImportIcon);
			reloadImportButton.addActionListener(new ReloadImportAction());
			reloadImportButton.setToolTipText("Reload imports - unknown out of sync");
			reloadImportButton.setEnabled(false);
			importHandler.setReloadButton(reloadImportButton);
		}

		buttonPanel.add(clearButton, FlowLayout.LEFT);
		if (settings.hasImport()) {
			buttonPanel.add(reloadImportButton, FlowLayout.LEFT);
			buttonPanel.add(importButton, FlowLayout.LEFT);
		}

		return buttonPanel;
	}
	
	private class ImportAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			importHandler.showImportPanel(frame);
		}
		
	}
	
	private class ReloadImportAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			importHandler.reload();
		}
		
	}

	private class HelpAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JOptionPane.showMessageDialog(frame, ActionUtil.getHelpText());
		}
	}
	
	private class AboutAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			URL url = StandAloneREPL.class.getResource(rascalIconLoc);
			Icon rascal = new ImageIcon(url);
			
			JOptionPane.showMessageDialog(frame, "About this REPL...", "About", JOptionPane.INFORMATION_MESSAGE, rascal); //TODO
		}
	}

	private class ClearAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			replPanel.clearScreen();
			replPanel.getEvaluator().clear();
			replPanel.addStatusMessage("Session cleared");
		}
	}	
	
	private class LoadAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser c = new JFileChooser();
			c.setFileFilter(new GenericFileFilter(Settings.SESSION_FILE_EXTENTION, Settings.SESSION_FILE_DESCRIPTION, true));
			int rVal = c.showOpenDialog(frame);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				if (XMLSessionParser.importHistory(replPanel,
						c.getSelectedFile()))
					replPanel.addStatusMessage("File succesfully loaded");
			}
		}
	}

	private class SaveAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser c = new JFileChooser();
			c.setFileFilter(new GenericFileFilter(Settings.SESSION_FILE_EXTENTION, Settings.SESSION_FILE_DESCRIPTION, true));
			int rVal = c.showSaveDialog(frame);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				XMLSessionParser.exportHistory(replPanel, new File(c.getSelectedFile()+Settings.SESSION_FILE_EXTENTION));
				replPanel.addStatusMessage("File succesfully saved");
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				new StandAloneREPL();
			}
		});
	}
}

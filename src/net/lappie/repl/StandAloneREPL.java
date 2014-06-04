package net.lappie.repl;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import net.lappie.repl.languages.command.CommandSettings;
import net.lappie.repl.languages.jatha.JathaSettings;
import net.lappie.repl.languages.jython.JythonSettings;
import net.lappie.repl.languages.rascal.RascalSettings;

/**
 * Creates a REPL with full functionallity running in a JFrame. 
 * @author Lappie
 * TODO: http://technical-tejash.blogspot.nl/2010/03/eclipse-avoid-cyclic-dependency-between.html
 */
public class StandAloneREPL {
	private JFrame frame;
	private ExtendedREPLPanel replPanel;
	private ILanguageSettings settings;
	
	private ImportHandler importHandler;
	
	private final String clearIconLoc = Settings.BASE + "display16.png";
	private final String importIconLoc = Settings.BASE + "load16.png";
	private final String rascalIconLoc = Settings.BASE + "rascal48.gif";

	public StandAloneREPL() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new CloseREPLListener());
		frame.setLocationRelativeTo(null);


		//////// Initiate the REPL Panel ///////
		replPanel = new ExtendedREPLPanel(); //create first, necessary for other functions
		loadLanguage(new RascalSettings());
		
		replPanel.addExtension(new SearchExtension(replPanel));
		importHandler = new ImportHandler(replPanel, settings);

		JPanel buttonPanel = createButtonPanel();
		loadKeyActions();
		JPanel mainPanel = new JPanel(new BorderLayout());

		mainPanel.add(buttonPanel, BorderLayout.NORTH);
		mainPanel.add(replPanel, BorderLayout.CENTER);

		frame.setContentPane(mainPanel);

		loadTitle();
		frame.pack();
		
		// Make textField get the focus whenever frame is activated.
		frame.addWindowFocusListener(new WindowAdapter() { 
			@Override
			public void windowGainedFocus(WindowEvent e) {
				replPanel.getREPLTextComponent().requestFocusInWindow();
			}
		});
		
		//Start:
		replPanel.displayMessage("Welcome to the LP-REPL v" + Settings.VERSION);
		replPanel.start();
		
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
		
		ButtonGroup languageGroup = new ButtonGroup();
		
		List<JMenuItem> commandLanguageItems = loadCommandLanguagesMenu();
		for(JMenuItem languageItem : commandLanguageItems) {
			languageGroup.add(languageItem);
			languages.add(languageItem);
		}
		if(commandLanguageItems.size() > 0)
			languages.addSeparator();
		
		
		item = new JRadioButtonMenuItem("Rascal");
		item.addActionListener(new LoadLanguageAction(new RascalSettings()));
		item.setSelected(true);
		languageGroup.add(item);
		languages.add(item);
		
		item = new JRadioButtonMenuItem("Jython");
		item.addActionListener(new LoadLanguageAction(new JythonSettings()));
		languageGroup.add(item);
		languages.add(item);
		
		item = new JRadioButtonMenuItem("Jatha");
		item.addActionListener(new LoadLanguageAction(new JathaSettings()));
		languageGroup.add(item);
		languages.add(item);
		
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
	
	private List<JMenuItem> loadCommandLanguagesMenu() {
    	File dir = new File(""+"commandLanguages");
    	File files[] = dir.listFiles(new FilenameFilter() { 
    	         @Override
				public boolean accept(File dir, String filename)
    	              { return filename.endsWith(".xml"); }
    	});
    	if(files == null) //if directory not found
    		return new ArrayList<>();
    	
    	List<JMenuItem> buttons = new ArrayList<>();
    	
    	for(File file : files) {
	    	JMenuItem item = new JRadioButtonMenuItem(file.getName());
			item.setSelected(false);
			item.addActionListener(new LoadLanguageAction(new CommandSettings(file)));
			buttons.add(item);
    	}
    	return buttons;
	}
	
	public void loadLanguage(ILanguageSettings settings) {
		this.settings = settings;
		try {
			settings.load();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "Could not load language", "Error", JOptionPane.ERROR_MESSAGE);
			replPanel.displayError("Failed to load language");
			e.printStackTrace();
			return;
		}
		replPanel.load(settings);
		replPanel.start();
		loadTitle();
	}
	
	private void loadSettings() { //TODO
		//Loading settings: 
		String workspaceLoc = Settings.getProperty("workspace");
		if(workspaceLoc != null) {
			File workspace = new File(Settings.getProperty("workspace"));
			settings.getEvaluator().setWorkspace(workspace);
		}	
	}
	
	private void loadTitle() {
		frame.setTitle("LP-REPL"
				+ (settings.getLanguageName().length() > 0 ? ": "
						+ settings.getLanguageName() : ""));	
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
			importButton.setToolTipText("Manage imports");
			
			reloadImportButton = importHandler.getReloadButton();
		}

		buttonPanel.add(clearButton, FlowLayout.LEFT);
		if (settings.hasImport()) {
			buttonPanel.add(reloadImportButton, FlowLayout.LEFT);
			buttonPanel.add(importButton, FlowLayout.LEFT);
		}

		return buttonPanel;
	}
	
	
	private class LoadLanguageAction extends AbstractAction {

		private ILanguageSettings settings = null;
		
		protected LoadLanguageAction(ILanguageSettings settings) {
			this.settings = settings;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			loadLanguage(settings);
		}
		
	}
	
	private class ImportAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			importHandler.showImportPanel(frame);
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
			importHandler.clear();
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
				File f = c.getSelectedFile();
				if(!c.getSelectedFile().toString().contains("."))
					f = new File(c.getSelectedFile()+Settings.SESSION_FILE_EXTENTION);
				XMLSessionParser.exportHistory(replPanel, f);
				replPanel.addStatusMessage("File succesfully saved");
			}
		}
	}
	
	private class CloseREPLListener implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {
		}

		@Override
		public void windowClosing(WindowEvent e) {
			replPanel.close();
		}

		@Override
		public void windowClosed(WindowEvent e) {
			replPanel.close();
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowActivated(WindowEvent e) {
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
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
					e.printStackTrace();
				}
				new StandAloneREPL();
			}
		});
	}
}

package net.lappie.repl;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import net.lappie.repl.actions.ActionUtil;
import net.lappie.repl.functionallity.extensions.SearchExtension;
import net.lappie.repl.history.XMLCommandParser;
import net.lappie.repl.languages.IREPLSettings;
import net.lappie.repl.languages.RascalSettings;

/**
 * Creates a REPL with full functionallity running in a JFrame. 
 * @author Lappie
 *
 */
public class StandAloneREPL {
	private JFrame frame;
	private ExtendedREPLPanel replPanel;
	private IREPLSettings settings;

	private final String base = "../../../";
	private final String helpIconLoc = base + "info16.png";
	private final String clearIconLoc = base + "display16.png";
	private final String saveIconLoc = base + "save16.png";
	private final String loadIconLoc = base + "load16.png";
	private final String importIconLoc = base + "import16.png";

	public StandAloneREPL() {
		frame = new JFrame();

		replPanel = createREPLPanel();
		replPanel.addExtension(new SearchExtension(replPanel));

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
		frame.addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				replPanel.requestFocusInWindow();
			}
		});

		frame.setVisible(true);
	}

	private ExtendedREPLPanel createREPLPanel() {
		// settings = new LispSettings();
		// settings = new PythonSettings();
		// settings = new CopySettings();
		settings = new RascalSettings();
		return new ExtendedREPLPanel(settings);
	}

	private void loadKeyActions() {
		replPanel.addKeyAction("F1", new HelpAction());
		replPanel.addKeyAction("ctrl S", new SaveAction());
		replPanel.addKeyAction("ctrl O", new LoadAction());
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		URL url = StandAloneREPL.class.getResource(helpIconLoc);
		Icon help = new ImageIcon(url);
		JButton helpButton = new JButton(help);
		helpButton.addActionListener(new HelpAction());
		helpButton.setToolTipText("Help");

		url = StandAloneREPL.class.getResource(clearIconLoc);
		Icon clear = new ImageIcon(url);
		JButton clearButton = new JButton(clear);
		clearButton.addActionListener(new ClearAction());
		clearButton.setToolTipText("Clear screen");

		// opening and saving files:
		url = StandAloneREPL.class.getResource(saveIconLoc);
		Icon save = new ImageIcon(url);
		JButton saveButton = new JButton(save);
		saveButton.addActionListener(new SaveAction());
		saveButton.setToolTipText("Save commands to file");

		url = StandAloneREPL.class.getResource(loadIconLoc);
		Icon load = new ImageIcon(url);
		JButton loadButton = new JButton(load);
		loadButton.addActionListener(new LoadAction());
		loadButton.setToolTipText("Load file");

		JButton importButton = null;
		if (settings.hasLoadModuleCommand()) {
			url = StandAloneREPL.class.getResource(importIconLoc);
			Icon importIcon = new ImageIcon(url);
			importButton = new JButton(importIcon);
			importButton.addActionListener(new ImportAction());
			importButton.setToolTipText("Import file");
		}

		buttonPanel.add(helpButton, FlowLayout.LEFT);
		buttonPanel.add(clearButton, FlowLayout.LEFT);
		// if (settings.hasLoadModuleCommand()) buttonPanel.add(importButton,
		// FlowLayout.LEFT);

		buttonPanel.add(loadButton, FlowLayout.LEFT);
		buttonPanel.add(saveButton, FlowLayout.LEFT);

		return buttonPanel;
	}

	private class HelpAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JOptionPane.showMessageDialog(frame, ActionUtil.getHelpText());
		}
	}

	private class ClearAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			replPanel.clearScreen();
			replPanel.getEvaluator().clear();
		}
	}

	private class ImportAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			/*
			 * JFileChooser c = new JFileChooser(); int rVal =
			 * c.showOpenDialog(frame); if (rVal == JFileChooser.APPROVE_OPTION)
			 * replPanel.loadModule(c.getSelectedFile());
			 */
		}
	}

	private class LoadAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser c = new JFileChooser();
			c.setFileFilter(new XMLFileFilter());
			int rVal = c.showOpenDialog(frame);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				if (XMLCommandParser.importHistory(replPanel,
						c.getSelectedFile()))
					replPanel.addStatusMessage("File succesfully loaded");
			}
		}
	}

	private class SaveAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser c = new JFileChooser();
			c.setFileFilter(new XMLFileFilter());
			int rVal = c.showSaveDialog(frame);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				XMLCommandParser.exportHistory(replPanel, c.getSelectedFile());
				replPanel.addStatusMessage("File succesfully saved");
			}
		}
	}
	
	private class XMLFileFilter extends FileFilter{

		@Override
		public boolean accept(File f) {
			return f.getName().endsWith(".xml");
		}

		@Override
		public String getDescription() {
			return "XML files";
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

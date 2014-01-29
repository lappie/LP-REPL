package net.lappie.repl.functionallity;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.lappie.repl.ExtendedREPLPanel;
import net.lappie.repl.GenericFileFilter;
import net.lappie.repl.Settings;
import net.lappie.repl.Util;
import net.lappie.repl.languages.AbstractResult;
import net.lappie.repl.languages.IEvaluator;
import net.lappie.repl.languages.ILanguageSettings;

//TODO http://docs.oracle.com/javase/tutorial/essential/io/notification.html
public class ImportHandler {
	
	private ExtendedREPLPanel replPanel;
	private ILanguageSettings settings;
	private IEvaluator evaluator;
	
	private final String workspaceIconLoc = Settings.BASE + "workspace16.png";
	private final String removeIconLoc = Settings.BASE + "remove16.png";
	private final String importIconLoc = Settings.BASE + "load16.png";
	
	private ArrayList<String> imported = new ArrayList<>();
	private ArrayList<String> removedImports = new ArrayList<>();
	private ArrayList<String> addedImports = new ArrayList<>();
	
	private File newWorkspace = null;
	
	//SWING
	private JFrame frame = new JFrame("Import");
	
	//Components:
	private JPanel container;
	private JButton newImportButton;
	private JButton ok;
	private JButton cancel;
	private JLabel workspaceLabel;
	private JButton reloadImportButton;
	
	public ImportHandler(ExtendedREPLPanel replPanel, ILanguageSettings settings) {
		this.replPanel = replPanel;
		this.settings = settings;
		this.evaluator = settings.getEvaluator();
	}
	
	public void showImportPanel(JFrame parent) {
		renderContainerPanel();
		
		frame.setContentPane(container);
		frame.pack();
		frame.setLocationRelativeTo(parent);
		frame.setVisible(true);
	}
	
	public void reload() {
		evaluator.clear(); //restart
		replPanel.clearScreen();
		
		for(String newImport : imported) {
			System.out.println(newImport);
			doImport(newImport);
		}
	}
	
	public void setReloadButton(JButton reloadImportButton) {
		this.reloadImportButton = reloadImportButton;
	}
	
	
	private JPanel renderContainerPanel() {
		container = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		File workspace = evaluator.getWorkspace();
		workspaceLabel = new JLabel(workspace == null ? "..." : workspace.toString());
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0,0,20,0);
		container.add(workspaceLabel, c);
		
		JButton workspaceButton = new JButton(Util.getImageIcon(workspaceIconLoc));
		workspaceButton.addActionListener(new SetWorkspaceAction());
		c.gridx = 1;
		c.gridy = 0;
		container.add(workspaceButton, c);
		
		c.insets = new Insets(0,0,0,0); //reset
		newImportButton = new JButton("Import new file", Util.getImageIcon(importIconLoc));
		newImportButton.addActionListener(new AddImportAction());
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		if(workspace == null) 
			newImportButton.setEnabled(false);
		container.add(newImportButton, c);
		
		
		int i = 2;
		c.gridwidth = 1; //reset
		for(String address : imported) {
			createImportAdressField(address, i);
			i++;
		}
		createOkCancelButtons(i);
		
		
		return container;
	}
	
	private void createOkCancelButtons(int index) {
		GridBagConstraints c = new GridBagConstraints();
		
		ok = new JButton("OK");
		ok.addActionListener(new OkAction());
		c.gridx = 0;
		c.gridy = index;
		c.insets = new Insets(20,0,0,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		container.add(ok, c);
		cancel = new JButton("Cancel");
		cancel.addActionListener(new CancelAction());
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		container.add(cancel, c);
	}
	
	private void createImportAdressField(String address, int i) {
		GridBagConstraints c = new GridBagConstraints();
		JLabel label = new JLabel(address);
		c.gridx = 0;
		c.gridy = i;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		
		container.add(label, c);
		
		JButton remove = new JButton(Util.getImageIcon(removeIconLoc));
		remove.setBorderPainted(false);
		remove.setFocusPainted(false);
		remove.setContentAreaFilled(false);
		
		remove.setSize(new Dimension(16, 16));
		remove.addActionListener(new RemoveAction(address, label, remove));
		c.gridx = 1;
		c.weightx = 0;
		container.add(remove, c);
	}
	
	
	
	
	private void updateOkButtonToRestart() {
		ok.setText("OK (requires restart)");
	}
	
	private void doImport(String file) {
		AbstractResult result = evaluator.doImport(file);

		if(!result.hasError())
			replPanel.displayMessage("Import completed succesfully");
		else {
			replPanel.displayError(result.getError());
			replPanel.displayError("Import failed");
		}
		
		return;
	}
	
	/**
	 * Execute after the user has finished touching the settings
	 */
	private void execute() {
		if(newWorkspace == null && removedImports.size() == 0) {
			for(String newImport : addedImports) {
				doImport(newImport);
			}
			imported.addAll(addedImports);
			addedImports.clear();
			
			if(imported.size() > 0 && reloadImportButton != null)
				reloadImportButton.setEnabled(true);
			
			return;
		}
		//Restart required. 
		
		Settings.setProperty("workspace", newWorkspace.toString());
		evaluator.setWorkspace(newWorkspace);
		newWorkspace = null;
		evaluator.clear(); //restart
		replPanel.clearScreen();
		
		imported.addAll(addedImports);
		addedImports.clear();
		
		for(String toImport : imported) {
			doImport(toImport);
		}
		
		if(imported.size() > 0 && reloadImportButton != null)
			reloadImportButton.setEnabled(true);
	}
	
	private void close() {
		frame.setVisible(false);
		frame.dispose();
	}
	
	private class RemoveAction extends AbstractAction {
		
		private String address;
		private Component label;
		private Component removeButton;
		
		public RemoveAction(String address, Component label, Component removeButton) {
			this.address = address;
			this.label = label;
			this.removeButton = removeButton;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			imported.remove(address);
			container.remove(label);
			container.remove(removeButton);
			updateOkButtonToRestart();
			container.invalidate();
			container.repaint();
		}
	}
	
	private class AddImportAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser c = new JFileChooser();
			if(newWorkspace != null)
				c.setCurrentDirectory(newWorkspace);
			else if(settings.hasWorkspace() && settings.getEvaluator().getWorkspace() != null)
				c.setCurrentDirectory(evaluator.getWorkspace());
			
			c.setFileFilter(new GenericFileFilter(settings.getFileExtention(), settings.getLanguageName(), true));
			int rVal = c.showOpenDialog(frame);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				String file = c.getSelectedFile().getAbsolutePath();
				if(imported.contains(file)) //don't add the same file twice
					return; 
				
				addedImports.add(file);
				if(ok != null) {
					container.remove(ok);
					container.remove(cancel);
				}
				createImportAdressField(file, 2+imported.size());
				createOkCancelButtons(2+imported.size()+1);
				container.invalidate();
				container.repaint();
				frame.pack();
			}
		}
	}
	
	private class SetWorkspaceAction extends AbstractAction {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser c = new JFileChooser();
			c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			int rVal = c.showOpenDialog(frame);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				newWorkspace = c.getSelectedFile();
				workspaceLabel.setText(c.getSelectedFile().toString());
				updateOkButtonToRestart();
				newImportButton.setEnabled(true);
				frame.pack(); //we need to resize
			}
		}
	}	
	
	private class OkAction extends AbstractAction{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			execute();
			close();
		}
	}
	
	private class CancelAction extends AbstractAction {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			removedImports.clear();
			addedImports.clear();
			newWorkspace = null;
			close();
		}
	}
	/*
	public static void main(String args[]) {
		ImportHandler ih = new ImportHandler(null, new RascalSettings());
		ih.imported.add("workspace/rascal-repl.standalone2/icons");
		ih.imported.add("workspace/rascal-repl.standalone2/pietje");
		ih.imported.add("workspace/rascal-repl.standalone2/jantje");
		ih.imported.add("workspace/rascal-repl.standalone2/hi");
		ih.showImportPanel(null);
	}*/
}

package net.lappie.repl.plugin.views;

import java.awt.Frame;

import net.lappie.repl.ExtendedREPLPanel;
import net.lappie.repl.languages.IREPLSettings;
import net.lappie.repl.languages.RascalSettings;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class REPLView extends ViewPart{

	private ExtendedREPLPanel replPanel;

	private Composite mySwing;
	private Frame mySwingFrame;
	
	
	@Override
	public void createPartControl(Composite parent) {
		this.mySwing = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		mySwingFrame = SWT_AWT.new_Frame(this.mySwing);

		IProject project = null;
		if(getViewSite().getSecondaryId() != null)
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(getViewSite().getSecondaryId());
		
		IREPLSettings settings = new RascalSettings();
		replPanel = new ExtendedREPLPanel(settings);
		
		setPartName(replPanel.getName());
		mySwingFrame.add(replPanel);
	}

	@Override
	public void setFocus() {
		mySwing.setFocus();
	}
	
}

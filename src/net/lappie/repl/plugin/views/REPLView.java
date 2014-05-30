package net.lappie.repl.plugin.views;

import java.awt.Frame;
import java.util.ArrayList;

import net.lappie.repl.ExtendedREPLPanel;
import net.lappie.repl.languages.ILanguageSettings;
import net.lappie.repl.languages.rascal.RascalSettings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

public class REPLView extends ViewPart{

	private ExtendedREPLPanel replPanel;

	private Composite mySwing;
	private Frame mySwingFrame;
	
	public REPLView() {
		super();
	}
	
	//For making Search happen:
	// this is truly the most horrible thing you have ever seen, however, since we don't have a factory yet
	// and we want to keep the ability of multiple views, we'll do it like this. 
	private static ArrayList<ExtendedREPLPanel> repls = new ArrayList<>();
	
	public static ExtendedREPLPanel getREPL() { //for making search happen
		if(repls.isEmpty())
			return null;
		return repls.get(0);
	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		this.mySwing = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		mySwingFrame = SWT_AWT.new_Frame(this.mySwing);

		//We will use this later on to load the correct project in the REPL
//		IProject project = null;
//		if(getViewSite().getSecondaryId() != null)
//			project = ResourcesPlugin.getWorkspace().getRoot().getProject(getViewSite().getSecondaryId());
		
		ILanguageSettings settings = new RascalSettings();
		replPanel = new ExtendedREPLPanel(settings);
		repls.add(replPanel);
		
		
		setPartName(replPanel.getName());
		mySwingFrame.add(replPanel);
		

		//This will make sure we are in the right context so that Commands (keys) only work for us
		IContextService contextService = (IContextService)getSite().getService(IContextService.class);
		contextService.activateContext("rascal-repl.standalone2.view.context");

	}

	@Override
	public void setFocus() {
		mySwing.setFocus();
	}
	
}

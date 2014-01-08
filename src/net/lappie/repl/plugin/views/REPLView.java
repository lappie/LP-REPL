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
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

public class REPLView extends ViewPart{

	private ExtendedREPLPanel replPanel;

	private Composite mySwing;
	private Frame mySwingFrame;
	
	public REPLView() {
		super();
	}
	
	
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
		
		/*org.eclipse.swt.graphics.Cursor waitCursor = Display.getDefault().getSystemCursor(SWT.CURSOR_IBEAM);
		
		if(Display.getDefault() != null && Display.getDefault().getActiveShell() != null)
			Display.getDefault().getActiveShell().setCursor(waitCursor);
		*/
		//Cursor waitCursor = Display.getDefault().getSystemCursor(SWT.CURSOR_IBEAM);
		//parent.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		//Display.getDefault().getActiveShell().setCursor(waitCursor);

		//This will make sure we are in the right context so that Commands (keys) only work for us
		IContextService contextService = (IContextService)getSite().getService(IContextService.class);
		contextService.activateContext("rascal-repl.standalone2.view.context");

	}

	@Override
	public void setFocus() {
		mySwing.setFocus();
	}
	
}

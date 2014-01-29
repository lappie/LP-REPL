package net.lappie.repl;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class GenericFileFilter extends FileFilter{

	private String extention;
	private String description;
	private boolean showFolders;
	
	public GenericFileFilter(String extention, String description, boolean showFolders) {
		super();
		this.extention = extention;
		this.description = description;
		this.showFolders = showFolders;
	}
	
	@Override
	public boolean accept(File f) {
		if(f.isDirectory())
			return showFolders;
			
		return f.getName().endsWith(extention);
	}

	@Override
	public String getDescription() {
		return description;
	}
	
}
package me.pxl.editor.FileExplorer;

import java.nio.file.Path;

import me.pxl.editor.Windows.ImWindow;

public abstract class FileWindow extends ImWindow {
	protected Path p;
	public FileWindow(Path p) {
		this.p=p;
		Name=p.toString();
		init();
	}
	protected abstract void init();
	
}

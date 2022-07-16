package me.pxl.editor.Windows;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import me.pxl.editor.EditorMain;

public abstract class ImWindow {
	protected String Name=this.getClass().getSimpleName()+":"+this.hashCode();
	protected int flags=0;
	boolean move=true;
	public void display() {
		ImBoolean bool=new ImBoolean(true);
		if (ImGui.begin(Name,bool,flags|(move?0:ImGuiWindowFlags.NoMove))) {
			move=ImGui.getWindowPosX()<ImGui.getMousePosX()&&ImGui.getWindowPosY()<ImGui.getMousePosY()&&(ImGui.getWindowPosX()+ImGui.getWindowWidth())>ImGui.getMousePosX()&&(ImGui.getWindowPosY()+ImGui.getWindowContentRegionMinY())>ImGui.getMousePosY();
			update();
		}
		ImGui.end();
		if(!bool.get())
			EditorMain.getEditor().removeWindow(this);
	}
	protected abstract void update();
	public abstract void destroy();
}

package ImGui;

import java.lang.reflect.Field;

import org.lwjgl.glfw.GLFW;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiMouseCursor;
import imgui.glfw.ImGuiImplGlfw;
import me.pxl.Engine;
import me.pxl.Event.EventManager;
import me.pxl.Event.EventTarget;
import me.pxl.Event.Events.ERender;
import me.pxl.Utils.Utils.Call;

public class ImUI {
	private final ImGuiImplPXL imGuiPXL;
    private final ImGuiImplGlfw imGuiglfw;
    
	public ImUI(Engine e,Call c) {
		this.c=c;
		this.imGuiglfw = new ImGuiImplGlfw();
		ImGui.createContext();
		ImGuiIO io=ImGui.getIO();
		io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
//      io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
		io.setIniFilename("settings.ini");
      	this.imGuiglfw.init(e.getWin(), true);
		EventManager.register(this);
		this.imGuiPXL=new ImGuiImplPXL(e.getRenderAPI());
		try {
//			for(Field f:this.imGuiglfw.getClass().getDeclaredFields())
			Field f = this.imGuiglfw.getClass().getDeclaredField("mouseCursors");
			f.setAccessible(true);
			long[] s=(long[]) f.get(imGuiglfw);
			s[ImGuiMouseCursor.ResizeNESW]=GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_NESW_CURSOR);
			s[ImGuiMouseCursor.ResizeNWSE]=GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_NWSE_CURSOR);
			s[ImGuiMouseCursor.ResizeAll]=GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_ALL_CURSOR);
			f.set(imGuiglfw, s);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		
//		this.imGuiglfw.mouseCursors[ImGuiMouseCursor.ResizeNESW] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
	}
	Call c;

	@EventTarget
	public void onRender(ERender r) {
		imGuiglfw.newFrame();
		ImGui.newFrame();
		ImGui.dockSpaceOverViewport();
		try {
		c.call();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		ImGui.render();
		
		
		imGuiPXL.renderDrawData(ImGui.getDrawData());
		
		if(ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
			//Broken right now
        	final long winid=GLFW.glfwGetCurrentContext();
        	ImGui.updatePlatformWindows();
        	ImGui.renderPlatformWindowsDefault();
        	GLFW.glfwMakeContextCurrent(winid);
        }
		
	}

	public void setC(Call c) {
		this.c = c;
	}
}

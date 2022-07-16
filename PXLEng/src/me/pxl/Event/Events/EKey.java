package me.pxl.Event.Events;

import org.lwjgl.glfw.GLFW;

import me.pxl.Event.Event;

public class EKey extends Event{
	
	public enum Action{
		PRESS,REPEAT,RELEASE,UNKNOWN;
	}

	Action ac;
	private String key;
	private int mods;

	public EKey(String s, int action, int mods) {
		this.key=s;
		switch(action) {
		case GLFW.GLFW_PRESS:
			ac=Action.PRESS;
			break;
		case GLFW.GLFW_REPEAT:
			ac=Action.REPEAT;
			break;
		case GLFW.GLFW_RELEASE:
			ac=Action.RELEASE;
			break;
		default:
			ac=Action.UNKNOWN;
			break;
		}
		this.mods=mods;
	}

	public String getKey() {
		return key;
	}

	public Action getAction() {
		return ac;
	}

	public int getMods() {
		return mods;
	}

}

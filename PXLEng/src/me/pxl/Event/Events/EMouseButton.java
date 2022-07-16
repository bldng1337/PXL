package me.pxl.Event.Events;

import me.pxl.Event.Event;

public class EMouseButton extends Event {

	private int button;
	private int action;
	private int mods;

	public EMouseButton(int button, int action, int mods) {
		this.button=button;
		this.action=action;
		this.mods=mods;
	}

	public int getButton() {
		return button;
	}

	public void setButton(int button) {
		this.button = button;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getMods() {
		return mods;
	}

	public void setMods(int mods) {
		this.mods = mods;
	}

}

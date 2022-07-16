package me.pxl.Event.Events;

import me.pxl.Event.Event;

public class EChar extends Event{

	private int codepoint;

	public EChar(int codepoint) {
		this.codepoint=codepoint;
	}

	public int getCodepoint() {
		return codepoint;
	}

	public void setCodepoint(int codepoint) {
		this.codepoint = codepoint;
	}

}

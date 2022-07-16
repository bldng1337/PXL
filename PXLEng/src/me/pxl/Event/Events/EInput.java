package me.pxl.Event.Events;

import me.pxl.Event.Event;

public class EInput extends Event{
	boolean pre;
	public EInput(boolean pre) {
		this.pre=pre;
	}
	public boolean isPre() {
		return pre;
	}
	public void setPre(boolean pre) {
		this.pre = pre;
	}
}

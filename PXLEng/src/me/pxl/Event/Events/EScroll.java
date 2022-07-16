package me.pxl.Event.Events;

import me.pxl.Event.Event;

public class EScroll extends Event {

	private double xoffset;
	private double yoffset;

	public EScroll(double xoffset, double yoffset) {
		this.xoffset=xoffset;
		this.yoffset=yoffset;
	}

	public double getXoffset() {
		return xoffset;
	}

	public void setXoffset(double xoffset) {
		this.xoffset = xoffset;
	}

	public double getYoffset() {
		return yoffset;
	}

	public void setYoffset(double yoffset) {
		this.yoffset = yoffset;
	}

}

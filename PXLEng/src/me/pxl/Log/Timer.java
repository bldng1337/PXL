package me.pxl.Log;

import me.pxl.Utils.Utils.Call;

public class Timer {
	long time=0;
	public Timer() {
		reset();
	}
	
	public long getmillis() {
		return (System.nanoTime()-time)/1_000_000;
	}
	public long getnano() {
		return (System.nanoTime()-time);
	}
	
	public void reset() {
		time=System.nanoTime();
	}
	public static Timer time(Call c) {
		Timer t=new Timer();
		t.reset();
		c.call();
		return t;
	}

	public void log() {
		System.out.println("Timer took "+this.getmillis()+"ms");
	}

	public void log(String string) {
		System.out.println("Timer "+string+" took "+this.getmillis()+"ms");
	}
}

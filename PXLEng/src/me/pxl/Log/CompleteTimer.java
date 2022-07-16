package me.pxl.Log;

public class CompleteTimer {
	long timestamp;
	long time;
	public CompleteTimer() {
		timestamp=0;
		time=0;
	}
	
	public void start() {
		timestamp=System.nanoTime();
	}
	
	public void stop() {
		time+=(System.nanoTime()-timestamp);
	}
	
	public long getms() {
		return time/1_000_000;
	}
	
	public void reset() {
		time=0;
	}
}

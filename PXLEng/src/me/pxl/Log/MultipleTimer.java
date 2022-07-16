package me.pxl.Log;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import me.pxl.Utils.Utils.Call;

public class MultipleTimer {
	HashMap<String, Long> stamps;
	public MultipleTimer() {
		stamps=new HashMap<>();
	}
	public MultipleTimer setOnReset(Consumer<Set<Entry<String, Long>>> c) {
		r=c;
		return this;
	}
	Consumer<Set<Entry<String, Long>>> r;
	public void reset() {
		if(r!=null)
			r.accept(getTimeStamps());
		stamps.clear();
	}
	
	public void begin(String s) {
		stamps.put(s, System.nanoTime());
	}
	
	public void end(String s) {
		stamps.put(s, (System.nanoTime()-stamps.get(s))/1_000_000);
	}
	
	public <T> T beginend(String s,Supplier<T> su) {
		begin(s);
		T t=su.get();
		end(s);
		return t;
	}
	
	public Set<Entry<String, Long>> getTimeStamps() {
		return stamps.entrySet();
	}
	public void time(String s,Call su) {
		begin(s);
		su.call();
		end(s);
	}
}

package me.pxl.Event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import me.pxl.Log.MultipleTimer;
import me.pxl.Utils.Tuple;

public class EventManager {
	@SuppressWarnings("rawtypes")
	public static HashMap<Class, List<Tuple<Object,Method>>> eventmap=new HashMap<>();
	
	public static void register(Object o) {
		for(Method m:o.getClass().getMethods()) {
			if(m.isAnnotationPresent(EventTarget.class)) {
				if(!eventmap.containsKey(m.getParameters()[0].getType()))
					eventmap.put(m.getParameters()[0].getType(), new ArrayList<>());
				eventmap.get(m.getParameters()[0].getType()).add(new Tuple<>(o, m));
			}
		}
	}
	
	public static void call(Event e) {
		if(!eventmap.containsKey(e.getClass()))
			eventmap.put(e.getClass(), new ArrayList<>());
		eventmap.get(e.getClass()).forEach((a)->{
			try {
				a.getC().invoke(a.getT(), e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
	}
	public static void call(Event e,MultipleTimer t) {
		if(!eventmap.containsKey(e.getClass()))
			eventmap.put(e.getClass(), new ArrayList<>());
		eventmap.get(e.getClass()).forEach((a)->{
			t.time(e.getClass().getSimpleName()+":"+a.getT().toString(),()->{
			try {
				a.getC().invoke(a.getT(), e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			});
		});
	}

	public static void unregister(Object that) {
		for(Method m:that.getClass().getMethods()) {
			if(m.isAnnotationPresent(EventTarget.class)) {
				if(eventmap.containsKey(m.getParameters()[0].getType())) {
					List<Tuple<Object, Method>> li=eventmap.get(m.getParameters()[0].getType());
					li.removeAll(li.stream().filter((a)->a.getT()==that).collect(Collectors.toList()));
				}
			}
		}
	}

	public static List<Object> getEvents(Class<?> e) {
		if(!eventmap.containsKey(e))
			eventmap.put(e, new ArrayList<>());
		return eventmap.get(e).stream().map((a)->a.getT()).collect(Collectors.toList());
	}
}

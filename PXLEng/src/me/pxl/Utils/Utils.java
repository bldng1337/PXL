package me.pxl.Utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {

	private Utils() {
		throw new RuntimeException("tried o instanciate a Util Class");
	}
	
	@FunctionalInterface
	public static interface Call{
		public abstract void call();
	}
	@FunctionalInterface
	public static interface SaveCall<T extends Exception>{
		public abstract void call() throws T;
	}
	@FunctionalInterface
	public static interface SaveBiFunction<A,B,C,T extends Exception>{
		public abstract C apply(A a,B b) throws T;
	}
	
	static List<ClassLoader> lcl=new ArrayList<>();
	public static void addClassloader(ClassLoader l) {
		lcl.add(l);
	}
	
	public static void removeClassloader(ClassLoader l) {
		lcl.remove(l);
	}
	public static Class<?> forName(String name) throws ClassNotFoundException {
		for(ClassLoader cl:lcl) {
			try {
			return Class.forName(name,true,cl);
			} catch (ClassNotFoundException e) {
			}
		}
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
		}
		return null;
	}

}



package me.pxl.Log;

import java.util.function.Supplier;

public class Logger {

	
	
	public static void log(String message) {
//		Class c=StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
		System.out.println(message);
	}
	
	public static void log(Supplier<String> s) {
		System.out.println(s.get());
	}
}

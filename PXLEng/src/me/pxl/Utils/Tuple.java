package me.pxl.Utils;

public class Tuple<T,C>{
		T t;
		C c;
		public Tuple(T t,C c) {
			this.t=t;
			this.c=c;
		}
		public T getT() {
			return t;
		}
		public void setT(T t) {
			this.t = t;
		}
		public C getC() {
			return c;
		}
		public void setC(C c) {
			this.c = c;
		}
		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Tuple) {
				return this.c.equals(((Tuple)obj).c)&&this.t.equals(((Tuple)obj).t);
			}
			return super.equals(obj);
		}
	
}
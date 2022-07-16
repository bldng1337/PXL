package me.pxl.Backend.Generic.Buffer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Supplier;

import me.pxl.Backend.Generic.Buffer.RByteBuffer.Usage;
import me.pxl.Utils.Utils;

public abstract class RIndexBuffer{

	protected int elements;
	protected Usage data;
	IndexType dtype=IndexType.UINT;
	
	public RIndexBuffer(Usage data,int elements) {
		this.data=data;
		this.elements=elements;
	}

	public abstract Save<IntBuffer> load(int elements);
	public abstract Save<IntBuffer> load();
	public abstract Save<ByteBuffer> loadb();
	public Save<ByteBuffer> loadb(int elemens){
		if(data!=Usage.STREAM) {
			return null;
		}
		this.elements=elemens;
		return loadb();
	}
	public IndexType getDtype() {
		return dtype;
	}

	public void setDtype(IndexType dtype) {
		this.dtype = dtype;
	}

	public enum IndexType{
		USHORT,UINT;
	}
	
	public static class Save<T extends Buffer> implements AutoCloseable{
		Utils.Call c;
		Supplier<T> reload;
		T t;
		public Save(Utils.Call c,T t,Supplier<T> r) {
			this.c=c;
			this.t=t;
			this.reload=r;
		}
		public T getBuf() {
			return t;
		}
		public void unload() {
			if(t!=null)
				c.call();
			t=null;
		}
		public void reload() {
			if(t==null)
				t=reload.get();
		}

		@Override
		public void close() {
			if(t!=null)
				c.call();
		}
	}
	public abstract void destroy();
}

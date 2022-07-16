package me.pxl.Backend.Generic.Buffer;

import java.nio.ByteBuffer;

import me.pxl.Backend.Generic.Buffer.RIndexBuffer.Save;

public abstract class RByteBuffer {

	public enum Usage{
		STREAM, STATIC, DYNAMIC
	}
	public enum DType{
		FLOAT,UBYTE
	}
	public static class Attrib{
		DType t;
		public DType getT() {
			return t;
		}
		public int getSize() {
			return size;
		}
		int size;
		boolean normalized=false;
		public Attrib setNormalized(boolean norm) {
			normalized=norm;
			return this;
		}
		public Attrib(DType t, int size) {
			this.t = t;
			this.size = size;
		}
		public boolean getNormalized() {
			return normalized;
		}
	}
	protected Attrib[] elemsize;
	protected int elements;
	protected Usage data;
	
	public RByteBuffer(Usage data,int elements,Attrib... elemsize) {
		this.data=data;
		this.elemsize=elemsize;
		this.elements=elements;
	}
	
	public abstract Save<ByteBuffer> load(int elements);
	public abstract Save<ByteBuffer> load();
	
	public abstract void destroy();
	
	@Override
	protected void finalize() {
		destroy();
	}
	public Attrib[] elements() {
		return elemsize;
	}
	public abstract int elementsize();
	
	public int size() {
		return elementsize()*elements;
	}
	public int getelnum() {
		return elements;
	}
	
}

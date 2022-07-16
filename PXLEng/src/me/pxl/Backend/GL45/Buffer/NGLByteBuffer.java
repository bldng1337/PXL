package me.pxl.Backend.GL45.Buffer;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL45;

import me.pxl.Backend.GL45.GlStateManager;
import me.pxl.Backend.Generic.Buffer.RByteBuffer;
import me.pxl.Backend.Generic.Buffer.RIndexBuffer.Save;

public class NGLByteBuffer extends RByteBuffer{
	
	int buf;
	@SuppressWarnings("incomplete-switch")
	public NGLByteBuffer(Usage data, int elements, Attrib[] elemsize) {
		super(data, elements, elemsize);
		buf=GL45.glGenBuffers();
		
		GlStateManager.bindVBuf(GL45.GL_ARRAY_BUFFER, buf);
		switch(this.data) {
		case DYNAMIC: GL45.glBufferData(GL45.GL_ARRAY_BUFFER, this.size(), GL45.GL_DYNAMIC_DRAW); break;
		case STATIC: GL45.glBufferData(GL45.GL_ARRAY_BUFFER, this.size(), GL45.GL_STATIC_DRAW); break;
		}
		
		GlStateManager.unbindVBuf(GL45.GL_ARRAY_BUFFER);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Save<ByteBuffer> load() {
		int size=this.size();
		
		GlStateManager.bindVBuf(GL45.GL_ARRAY_BUFFER, buf);
		switch(this.data) {
		case STREAM: GL45.glBufferData(GL45.GL_ARRAY_BUFFER, size, GL45.GL_STREAM_DRAW); break;
		}
		
		ByteBuffer bb=GL45.glMapBuffer(GL45.GL_ARRAY_BUFFER, GL45.GL_WRITE_ONLY, size, null);
//		t.log();
		return new Save<ByteBuffer>(()->{
			GL45.glUnmapBuffer(GL45.GL_ARRAY_BUFFER);
			GlStateManager.unbindVBuf(GL45.GL_ARRAY_BUFFER);
		}, bb,()->{
			GlStateManager.bindVBuf(GL45.GL_ARRAY_BUFFER, buf);
			switch(this.data) {
			case STREAM: GL45.glBufferData(GL45.GL_ARRAY_BUFFER, size, GL45.GL_STREAM_DRAW); break;
			}
			return GL45.glMapBuffer(GL45.GL_ARRAY_BUFFER, GL45.GL_WRITE_ONLY, size, null);
		});
	}
	
	@Override
	public Save<ByteBuffer> load(int elements) {
		if(this.data!=Usage.STREAM)
			return null;
		this.elements=elements;
		return load();
	}
	
	public int getBuf() {
		return buf;
	}

	@Override
	public void destroy() {
		GL45.glDeleteBuffers(buf);
	}

	@Override
	public int elementsize() {
		int c=0;
		for(Attrib a:elemsize)
			c+=getAttribsize(a);
		return c;
	}
	
	public int getAttribsize(Attrib t) {
		return this.getTypesize(t.getT())*t.getSize();
	}
	
	public int getTypesize(DType t) {
		switch(t) {
		case FLOAT:return 4;
		case UBYTE:return 1;
		}
		System.out.println("Returned -1");
		System.exit(1);
		return -1;
	}


}

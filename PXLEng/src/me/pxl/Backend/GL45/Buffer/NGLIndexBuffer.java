package me.pxl.Backend.GL45.Buffer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL45;

import me.pxl.Backend.GL45.GlStateManager;
import me.pxl.Backend.Generic.Buffer.RIndexBuffer;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.Usage;

public class NGLIndexBuffer extends RIndexBuffer{
	
	int buf;	
	
	@SuppressWarnings("incomplete-switch")
	public NGLIndexBuffer(Usage data,int numelem) {
		super(data, numelem);
		buf=GL45.glGenBuffers();
		GlStateManager.bindVBuf(GL45.GL_ELEMENT_ARRAY_BUFFER, buf);
		switch(this.data) {
		case DYNAMIC: GL45.glBufferData(GL45.GL_ELEMENT_ARRAY_BUFFER, this.elements*4l, GL45.GL_DYNAMIC_DRAW); break;
		case STATIC: GL45.glBufferData(GL45.GL_ELEMENT_ARRAY_BUFFER, this.elements*4l, GL45.GL_STATIC_DRAW); break;
		}
		GlStateManager.unbindVBuf(GL45.GL_ELEMENT_ARRAY_BUFFER);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Save<IntBuffer> load() {
		GlStateManager.bindVBuf(GL45.GL_ELEMENT_ARRAY_BUFFER, buf);
		switch(this.data) {
		case STREAM: GL45.glBufferData(GL45.GL_ELEMENT_ARRAY_BUFFER, this.elements*4l, GL45.GL_STREAM_DRAW); break;
		}
		return new Save<IntBuffer>(()->{
			GL45.glUnmapBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER);
			GlStateManager.unbindVBuf(GL45.GL_ELEMENT_ARRAY_BUFFER);
		}, GL45.glMapBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER, GL45.GL_WRITE_ONLY, this.elements*4l, null).asIntBuffer(), ()->{
			GlStateManager.bindVBuf(GL45.GL_ELEMENT_ARRAY_BUFFER, buf);
			switch(this.data) {
			case STREAM: GL45.glBufferData(GL45.GL_ELEMENT_ARRAY_BUFFER, this.elements*4l, GL45.GL_STREAM_DRAW); break;
			}
			return GL45.glMapBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER, GL45.GL_WRITE_ONLY, this.elements*4l, null).asIntBuffer();
		});
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public Save<ByteBuffer> loadb() {
		GlStateManager.bindVBuf(GL45.GL_ELEMENT_ARRAY_BUFFER, buf);
		switch(this.data) {
		case STREAM: GL45.glBufferData(GL45.GL_ELEMENT_ARRAY_BUFFER, this.elements*4l, GL45.GL_STREAM_DRAW); break;
		}
		return new Save<ByteBuffer>(()->{
			GL45.glUnmapBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER);
			GlStateManager.unbindVBuf(GL45.GL_ELEMENT_ARRAY_BUFFER);
		}, GL45.glMapBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER, GL45.GL_WRITE_ONLY, this.elements*4l, null),()->{
			GlStateManager.bindVBuf(GL45.GL_ELEMENT_ARRAY_BUFFER, buf);
			switch(this.data) {
			case STREAM: GL45.glBufferData(GL45.GL_ELEMENT_ARRAY_BUFFER, this.elements*4l, GL45.GL_STREAM_DRAW); break;
			}
			return GL45.glMapBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER, GL45.GL_WRITE_ONLY, this.elements*4l, null);
		});
	}
	
	public int getBuf() {
		return buf;
	}
	
	@Override
	public void destroy() {
		GL45.glDeleteBuffers(buf);
	}

	@Override
	public Save<IntBuffer> load(int elements) {
		if(this.data!=Usage.STREAM)
			return null;
		this.elements=elements;
		return load();
	}

}

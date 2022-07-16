package me.pxl.Backend.Generic;

import org.joml.Vector2f;

import me.pxl.Backend.Generic.Texture.Textureformat;

public abstract class FrameBuffer {
	protected int width,height;
	protected Textureformat[] fattachments;
	protected Textureformat fdepth;
	
	public FrameBuffer(int width,int height,Textureformat depth,Textureformat... attachments) {
		this.width=width;
		this.height=height;
		this.fattachments=attachments;
		this.fdepth=depth;
	}
	
	public abstract Texture[] getAttachments();
	public abstract int readPixel(Vector2f p);
	public abstract int getWidth();
	public abstract int getHeight();
	public abstract Vector2f getSize();
	
	public abstract void setWidth(int Width);
	public abstract void setHeight(int Height);
	public abstract void setSize(Vector2f s);

	public abstract void clear();

	public abstract void setSize(float x, float y);
	
}

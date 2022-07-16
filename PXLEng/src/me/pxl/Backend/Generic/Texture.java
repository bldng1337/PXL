package me.pxl.Backend.Generic;

import java.nio.ByteBuffer;

import org.joml.Vector2f;

public abstract class Texture {
	public enum Textureformat{
		RGBA8,
		DEPTH24STENCIL8,
		RGBA16F,
		RGBA32F,
		BRGBA
	}
	public enum Filter{
		LINEAR,NEAR;
	}
	protected Textureformat txtf;
	public Textureformat getTxtf() {
		return txtf;
	}
	protected int width,height;
	protected Filter fi;
	public Texture(Textureformat f,int width,int height) {
//		this(f, height, height, Filter.NEAR);
		txtf=f;
		this.width=width;
		this.height=height;
		fi=Filter.NEAR;
	}
	public Texture(Textureformat f,int width,int height,Filter fi) {
		txtf=f;
		this.width=width;
		this.height=height;
		this.fi=fi;
	}
	
	public abstract void load(ByteBuffer b);
	public abstract void destroy();
	public abstract int getID();
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public Vector2f getSize() {
		return new Vector2f(width,height);
	}
	
	
}

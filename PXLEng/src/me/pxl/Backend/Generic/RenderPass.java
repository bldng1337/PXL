package me.pxl.Backend.Generic;

import org.joml.Vector4f;

public class RenderPass {
	protected Shader s;
	protected VertexArray v;
	protected FrameBuffer fb;
	protected int count;
	public void setScissor(Vector4f scissor) {
		this.scissor = scissor;
	}

	public void setVertexoffset(int vertexoffset) {
		this.vertexoffset = vertexoffset;
	}

	protected long offset;
	protected Vector4f scissor;
	protected int vertexoffset=-1;

	public RenderPass(Shader s, VertexArray v, FrameBuffer fb) {
		this.s = s;
		this.v = v;
		this.fb = fb;
	}

	public void setS(Shader s) {
		this.s = s;
	}

	public void setV(VertexArray v) {
		this.v = v;
	}

	public void setFb(FrameBuffer fb) {
		this.fb = fb;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Shader getS() {
		return s;
	}

	public VertexArray getV() {
		return v;
	}

	public FrameBuffer getFb() {
		return fb;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}
	
	public Vector4f getScissor() {
		return scissor;
	}

	public int getVertexoffset() {
		return vertexoffset;
	}
}

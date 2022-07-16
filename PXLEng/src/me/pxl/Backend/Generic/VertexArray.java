package me.pxl.Backend.Generic;

import me.pxl.Backend.Generic.Buffer.RByteBuffer;
import me.pxl.Backend.Generic.Buffer.RIndexBuffer;

public abstract class VertexArray {
	
	protected RIndexBuffer indexbuffer;
	protected RByteBuffer[] buf;
	
	public VertexArray(RIndexBuffer indexbuffer,RByteBuffer... buf) {
		this.indexbuffer=indexbuffer;
		this.buf=buf;
	}

	public RByteBuffer getbuf(int slot) {
		return buf[slot];
	}
	public RIndexBuffer getibuf() {
		return indexbuffer;
	}
	public abstract void destroy();
	
	public abstract void deepdestroy();
	
}

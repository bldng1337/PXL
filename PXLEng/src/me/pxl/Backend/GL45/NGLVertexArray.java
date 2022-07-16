package me.pxl.Backend.GL45;

import org.lwjgl.opengl.GL45;

import me.pxl.Backend.GL45.Buffer.NGLByteBuffer;
import me.pxl.Backend.GL45.Buffer.NGLIndexBuffer;
import me.pxl.Backend.Generic.VertexArray;
import me.pxl.Backend.Generic.Buffer.RByteBuffer;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.Attrib;

public class NGLVertexArray extends VertexArray {
	int arr;

	protected NGLVertexArray(NGLIndexBuffer indexbuffer, NGLByteBuffer[] buf) {
		super(indexbuffer, buf);
		arr = GL45.glGenVertexArrays();
		// There could have been an Array with the new id
		GL45.glBindVertexArray(arr);
		GlStateManager.vao=arr;
		GlStateManager.bindVBuf(GL45.GL_ELEMENT_ARRAY_BUFFER, indexbuffer.getBuf());
		int i = 0;
		for (NGLByteBuffer b : buf) {
			GlStateManager.bindVBuf(GL45.GL_ARRAY_BUFFER, b.getBuf());
			int offset=0;
			for(Attrib t:b.elements()) {
				GL45.glEnableVertexAttribArray(i);
				switch(t.getT()) {
				case FLOAT:GL45.glVertexAttribPointer(i, t.getSize(), GL45.GL_FLOAT, t.getNormalized(), b.elementsize(), offset);break;
				case UBYTE:GL45.glVertexAttribPointer(i, t.getSize(), GL45.GL_UNSIGNED_BYTE, t.getNormalized(), b.elementsize(), offset);break;
				}
				offset+=b.getAttribsize(t);
				i++;
			}
		}
	}

	public void bind() {
		GlStateManager.bindVArray(arr);
		GlStateManager.bindVBuf(GL45.GL_ELEMENT_ARRAY_BUFFER,((NGLIndexBuffer)getibuf()).getBuf());
		for(int i=0;i<this.buf.length;i++) {
//			GlStateManager.bindVBuf(GL45.GL_ARRAY_BUFFER,((NGLByteBuffer)getbuf(i)).getBuf());
			GL45.glEnableVertexAttribArray(i);
		}
	}

	public void unbind() {
		for(int i=0;i<this.buf.length;i++)
			GL45.glDisableVertexAttribArray(i);
	}

	@Override
	public void destroy() {
		GL45.glDeleteVertexArrays(this.arr);
	}

	@Override
	public void deepdestroy() {
		destroy();
		getibuf().destroy();
		for(RByteBuffer buf:this.buf) {
			buf.destroy();
		}
	}

}

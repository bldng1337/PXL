package me.pxl.Backend.GL45;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL45;

import me.pxl.Backend.Generic.FrameBuffer;
import me.pxl.Backend.Generic.Texture;
import me.pxl.Backend.Generic.Texture.Textureformat;

public class NGLFrameBuffer extends FrameBuffer{
	int width,height;
	boolean Screen=false;
	public NGLFrameBuffer(int width, int height, Textureformat depth, Textureformat[] attachments) {
		super(width, height, depth, attachments);
		this.width=width;
		this.height=height;
		invalidate();
	}
	public NGLFrameBuffer() {
		super(-1, -1, null, null,null);
		Screen=true;
	}

	int buf=0;
	NGLTexture[] txt;
	NGLTexture depth;
	
	
	public void invalidate() {
		if(Screen)
			return;
		if(buf!=0) {
			GL45.glDeleteFramebuffers(buf);
			for(NGLTexture t:txt)
				t.destroy();
			depth.destroy();
			txt=null;
			depth=null;
		}
		buf=GL45.glCreateFramebuffers();
		GL45.glBindFramebuffer(GL45.GL_FRAMEBUFFER, buf);
		GlStateManager.fbuf=buf;
		if(fdepth!=null) {
			depth=new NGLTexture(fdepth, width, height);
			GL45.glFramebufferTexture2D(GL45.GL_FRAMEBUFFER,GL45.GL_DEPTH_ATTACHMENT,GL45.GL_TEXTURE_2D,depth.txt,0);
		}
		txt=new NGLTexture[fattachments.length];
		int[] buf=new int[fattachments.length];
		for(int i=0;i<txt.length;i++) {
			txt[i]=new NGLTexture(fattachments[i], width, height);
			GL45.glFramebufferTexture2D(GL45.GL_FRAMEBUFFER,GL45.GL_COLOR_ATTACHMENT0+i,GL45.GL_TEXTURE_2D,txt[i].txt,0);
			buf[i]=GL45.GL_COLOR_ATTACHMENT0+i;
		}
		GL45.glDrawBuffers(buf);
	}
	
	@Override
	public int readPixel(Vector2f p) {
		return -1;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Vector2f getSize() {
		return new Vector2f(width,height);
	}

	@Override
	public void setWidth(int Width) {
		width=Width;
		invalidate();
	}

	@Override
	public void setHeight(int Height) {
		height=Height;
		invalidate();
	}

	@Override
	public void setSize(Vector2f s) {
	}

	@Override
	public Texture[] getAttachments() {
		return txt;
	}
	@Override
	public void clear() {
		GlStateManager.bindFramebuffer(GL45.GL_FRAMEBUFFER, buf);
        GL45.glClearColor(0.0f,0.0f, 0.0f, 0.0f);
        GL45.glClear(GL45.GL_COLOR_BUFFER_BIT | GL45.GL_DEPTH_BUFFER_BIT);
	}
	@Override
	public void setSize(float x, float y) {
		width=(int) x;
		height=(int) y;
		invalidate();
	}

}

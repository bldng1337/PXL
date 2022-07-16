package me.pxl.Backend.GL45;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL45;

public class NGLTexture extends me.pxl.Backend.Generic.Texture{

	int txt;
	public NGLTexture(Textureformat f,int width,int height) {
		super(f,width,height);
		txt=GL45.glCreateTextures(GL45.GL_TEXTURE_2D);
		GlStateManager.bindTexture2D(txt);
		switch(f) {
		case DEPTH24STENCIL8: GL45.glTexStorage2D(GL45.GL_TEXTURE_2D,1,GL45.GL_DEPTH24_STENCIL8,width,height);break;
		case RGBA8: GL45.glTexImage2D(GL45.GL_TEXTURE_2D,0,GL45.GL_RGBA8,width,height,0,GL45.GL_RGBA,GL45.GL_UNSIGNED_INT_8_8_8_8_REV,(ByteBuffer)null);break;
		case BRGBA: GL45.glTexImage2D(GL45.GL_TEXTURE_2D,0,GL45.GL_RGBA,width,height,0,GL45.GL_RGBA,GL45.GL_UNSIGNED_BYTE,(ByteBuffer)null);break;
		case RGBA16F: GL45.glTexImage2D(GL45.GL_TEXTURE_2D,0,GL45.GL_RGBA16F,width,height,0,GL45.GL_RGBA,GL45.GL_FLOAT,(ByteBuffer)null);break;
		case RGBA32F: GL45.glTexImage2D(GL45.GL_TEXTURE_2D,0,GL45.GL_RGBA32F,width,height,0,GL45.GL_RGBA,GL45.GL_FLOAT,(ByteBuffer)null);break;
		default: System.err.println("Not implemented");break;
		}
		GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_MIN_FILTER, this.fi.equals(Filter.NEAR)?GL45.GL_NEAREST:GL45.GL_LINEAR);
		GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_MAG_FILTER, this.fi.equals(Filter.NEAR)?GL45.GL_NEAREST:GL45.GL_LINEAR);
		GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_WRAP_S, GL45.GL_CLAMP_TO_EDGE);
		GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_WRAP_R, GL45.GL_CLAMP_TO_EDGE);
		GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_WRAP_T, GL45.GL_CLAMP_TO_EDGE);
	}
	
	public NGLTexture(Textureformat f, int width, int height, Filter fi) {
		super(f, width, height, fi);
		txt=GL45.glCreateTextures(GL45.GL_TEXTURE_2D);
		GlStateManager.bindTexture2D(txt);
		switch(f) {
		case DEPTH24STENCIL8: GL45.glTexStorage2D(GL45.GL_TEXTURE_2D,1,GL45.GL_DEPTH24_STENCIL8,width,height);break;
		case RGBA8: GL45.glTexImage2D(GL45.GL_TEXTURE_2D,0,GL45.GL_RGBA8,width,height,0,GL45.GL_RGBA,GL45.GL_UNSIGNED_INT_8_8_8_8_REV,(ByteBuffer)null);break;
		case BRGBA: GL45.glTexImage2D(GL45.GL_TEXTURE_2D,0,GL45.GL_RGBA,width,height,0,GL45.GL_RGBA,GL45.GL_UNSIGNED_BYTE,(ByteBuffer)null);break;
		case RGBA16F: GL45.glTexImage2D(GL45.GL_TEXTURE_2D,0,GL45.GL_RGBA16F,width,height,0,GL45.GL_RGBA,GL45.GL_FLOAT,(ByteBuffer)null);break;
		case RGBA32F: GL45.glTexImage2D(GL45.GL_TEXTURE_2D,0,GL45.GL_RGBA32F,width,height,0,GL45.GL_RGBA,GL45.GL_FLOAT,(ByteBuffer)null);break;
		default: System.err.println("Not implemented");break;
		}
		
		GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_MIN_FILTER, this.fi.equals(Filter.NEAR)?GL45.GL_NEAREST:GL45.GL_LINEAR);
		GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_MAG_FILTER, this.fi.equals(Filter.NEAR)?GL45.GL_NEAREST:GL45.GL_LINEAR);
		GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_WRAP_S, GL45.GL_CLAMP_TO_EDGE);
		GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_WRAP_R, GL45.GL_CLAMP_TO_EDGE);
		GL45.glTexParameteri(GL45.GL_TEXTURE_2D, GL45.GL_TEXTURE_WRAP_T, GL45.GL_CLAMP_TO_EDGE);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void load(ByteBuffer b) {
		
		switch(this.txtf) {
		case RGBA8: GL45.glTextureSubImage2D(txt, 0, 0, 0, width, height, GL45.GL_RGBA,GL45.GL_UNSIGNED_INT_8_8_8_8_REV, b);break;
		}
		
	}

	@Override
	public void destroy() {
		GL45.glDeleteTextures(txt);
	}
	
	@Override
	public int getID() {
		return txt;
	}

}

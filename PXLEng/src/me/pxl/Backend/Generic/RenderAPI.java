package me.pxl.Backend.Generic;

import me.pxl.Backend.Generic.Buffer.RByteBuffer;
import me.pxl.Backend.Generic.Buffer.RIndexBuffer;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.Attrib;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.Usage;
import me.pxl.Backend.Generic.Shader.Type;
import me.pxl.Backend.Generic.Texture.Filter;
import me.pxl.Backend.Generic.Texture.Textureformat;

public abstract class RenderAPI {
	protected int width;
	protected int height;
	protected int frame_width;
	protected int frame_height;
	
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getFrame_width() {
		return frame_width;
	}

	public int getFrame_height() {
		return frame_height;
	}

	public abstract long init(String Title);
	
	public abstract void begin();
	public abstract void submit(RenderPass rp);
	public abstract void submit(Shader sh,int sx,int sy,int sz);
	public abstract void end();
	public abstract void updateViewport(int x,int y,int width,int height);
	
	public abstract void enableDepth();
	
	public abstract void disableDepth();
	
	
	public abstract FrameBuffer getScreen();
	public abstract FrameBuffer getfb(int width,int height,Textureformat depth,Textureformat... attachments);
	public abstract RenderPass getRp(Shader s, VertexArray v, FrameBuffer fb);
	
	public abstract RIndexBuffer getibuf(Usage data,int elements);
	public abstract RByteBuffer getBbuf(Usage data,int elements,Attrib... elemsize);
	
	public abstract Shader getShader(Type t,String src);
	public abstract Texture getTex(Textureformat f,int width,int height);
	public abstract VertexArray getVArr(RIndexBuffer indexbuffer,RByteBuffer... buf);
	
	public static boolean supported() {return false;}

	public abstract Texture getTex(Textureformat rgba8, int i, int j, Filter linear);
}

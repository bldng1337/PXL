package me.pxl.Backend.GL45;

import me.pxl.Backend.Generic.FrameBuffer;
import me.pxl.Backend.Generic.RenderPass;
import me.pxl.Backend.Generic.Shader;
import me.pxl.Backend.Generic.VertexArray;

public class NGLRenderPass extends RenderPass{

	public NGLRenderPass(Shader s, VertexArray v, FrameBuffer fb) {
		super(s, v, fb);
	}
	public NGLShader getShader() {
		return (NGLShader) s;
	}
	public NGLVertexArray getVArr() {
		return (NGLVertexArray) v;
	}
	public NGLFrameBuffer getFb() {
		return (NGLFrameBuffer) fb;
	}

}

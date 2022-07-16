package me.pxl.editor.System;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2f;

import me.pxl.Engine;
import me.pxl.Backend.Generic.RenderPass;
import me.pxl.Backend.Generic.Shader;
import me.pxl.Backend.Generic.VertexArray;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.Attrib;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.DType;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.Usage;
import me.pxl.Backend.Generic.Buffer.RIndexBuffer.Save;
import me.pxl.Backend.Generic.Shader.Type;
import me.pxl.Utils.IOUtils;

public class SEditor extends me.pxl.ECS.Systems.System{

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public boolean editor() {
		return true;
	}
	Shader s;
	VertexArray va;
	Vector2f size;
	RenderPass rp;
	@Override
	public void init(Engine em) {
		va=em.getRenderAPI().getVArr(em.getRenderAPI().getibuf(Usage.STATIC, 6), 
				em.getRenderAPI().getBbuf(Usage.STATIC, 4, new Attrib(DType.FLOAT,2)),//POS
				em.getRenderAPI().getBbuf(Usage.DYNAMIC, 4, new Attrib(DType.FLOAT,2)));//RPOS
		size=em.getFinalBuffer().getSize();
		try(Save<IntBuffer> ib=va.getibuf().load();
				Save<ByteBuffer> bb=va.getbuf(0).load();
				){
			ib.getBuf().put(new int[] {0,1,2,1,3,2});
			bb.getBuf().asFloatBuffer().put(new float[] {-1,-1, -1,1, 1,-1, 1,1});
		}
		try(Save<ByteBuffer> bb2=va.getbuf(1).load();){
			bb2.getBuf().asFloatBuffer().put(new float[] {0,0, 0,size.y, size.x,0, size.x,size.y});
		}
		s=em.getRenderAPI().getShader(Type.VERTEXFRAGMENT, 
				IOUtils.stream2string(IOUtils.getinfromclasspath(SEditor.class, "editor.glsl")));
		rp=em.getRenderAPI().getRp(s, va, em.getFinalBuffer());
		rp.setCount(6);
	}

	@Override
	public void update(Engine em) {
		rp.setFb(em.getFinalBuffer());
		if(size.x!=em.getFinalBuffer().getWidth()||size.y!=em.getFinalBuffer().getHeight()) {
			size=em.getFinalBuffer().getSize();
			try(Save<ByteBuffer> bb2=va.getbuf(1).load();){
				bb2.getBuf().asFloatBuffer().put(new float[] {0,0, 0,size.y, size.x,0, size.x,size.y});
			}
		}
		s.setVal("transform", em.em.getTranslation());
		em.getRenderAPI().submit(rp);
	}

}

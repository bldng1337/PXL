package me.pxl.Post.post.Bloom;

import me.pxl.Engine;
import me.pxl.Backend.Generic.Shader;
import me.pxl.Backend.Generic.Texture;
import me.pxl.Backend.Generic.Shader.Type;
import me.pxl.Backend.Generic.Texture.Filter;
import me.pxl.Backend.Generic.Texture.Textureformat;
import me.pxl.Post.Post;
import me.pxl.Utils.IOUtils;

public class Bloom extends Post{
	Shader sh_filter,sh_upscale,sh_gaus;
	public float Threshhold=1f;
	Texture filter;
	Texture t_chain[];
	float bw,bh;
	public boolean enalbed=false;
	public Bloom() {
		Engine e=Engine.getEngine();
		sh_filter=e.getRenderAPI().getShader(Type.COMPUTE, IOUtils.stream2string(IOUtils.getinfromclasspath(Bloom.class, "Filter.glsl")));
		sh_upscale=e.getRenderAPI().getShader(Type.COMPUTE, IOUtils.stream2string(IOUtils.getinfromclasspath(Bloom.class, "final.glsl")));
		sh_gaus=e.getRenderAPI().getShader(Type.COMPUTE, IOUtils.stream2string(IOUtils.getinfromclasspath(Bloom.class, "VGaussian.glsl")));
		t_chain=new Texture[5];
		updateTextures(e);
	}
	
	private void updateTextures(Engine e) {
		bw=e.getFinalBuffer().getWidth();
		bh=e.getFinalBuffer().getHeight();
		filter=e.getRenderAPI().getTex(Textureformat.RGBA16F, e.getFinalBuffer().getWidth()/2, e.getFinalBuffer().getHeight()/2, Filter.LINEAR);
		int w=filter.getWidth();
		int h=filter.getHeight();
		for(int i=0;i<t_chain.length;i++) {
			if(t_chain[i]!=null)
				t_chain[i].destroy();
			t_chain[i]=null;
			w/=2;
			h/=2;
			t_chain[i]=e.getRenderAPI().getTex(Textureformat.RGBA16F, w, h,Filter.LINEAR);
		}
	}
	public int test=6;

	@Override
	public void process(Engine e, Texture t) {
		if(!enalbed)
			return;
		if(bw!=e.getFinalBuffer().getWidth()||bh!=e.getFinalBuffer().getHeight()) {
			updateTextures(e);
		}
		
		sh_filter.setImage(0, t);
		sh_filter.setImage(1, filter);
		sh_filter.setVal("shouldsan", 1);
		sh_filter.setVal("threshhold", Threshhold);
		e.getRenderAPI().submit(sh_filter, filter.getWidth()/8, filter.getHeight()/8, 1);
		sh_filter.setVal("shouldsan", 0);
		Texture lastt=filter;
		for(Texture tch:this.t_chain) {
			sh_filter.setImage(0, lastt);
			sh_filter.setImage(1, tch);
			lastt=tch;
			e.getRenderAPI().submit(sh_filter, tch.getWidth()/8, tch.getHeight()/8, 1);
			sh_gaus.setImage(0, lastt);
			e.getRenderAPI().submit(sh_gaus, tch.getWidth()/8, tch.getHeight()/8, 1);
		}
		sh_upscale.setVal("test", test);
		sh_upscale.setImage(0, t);
		sh_upscale.setTexture(1, filter);
		for(int i=0;i<t_chain.length;i++)
			sh_upscale.setTexture(2+i, t_chain[i]);
		e.getRenderAPI().submit(sh_upscale, t.getWidth()/8, t.getHeight()/8, 1);
	}

}

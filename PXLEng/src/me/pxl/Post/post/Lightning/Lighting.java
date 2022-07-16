package me.pxl.Post.post.Lightning;

import java.util.List;

import me.pxl.Engine;
import me.pxl.Backend.Generic.Shader;
import me.pxl.Backend.Generic.Texture;
import me.pxl.Backend.Generic.Shader.Type;
import me.pxl.Backend.Generic.Texture.Filter;
import me.pxl.Backend.Generic.Texture.Textureformat;
import me.pxl.ECS.Component;
import me.pxl.ECS.Components.CLight;
import me.pxl.Post.Post;
import me.pxl.Utils.IOUtils;

public class Lighting extends Post{
	Shader sh,sh2;
	public static Texture Lightmap;
	public int quality=5;
	public boolean enabled=false;
	public float GlobalLight=0.2f;
	public Lighting() {
		Engine e=Engine.getEngine();
		Lightmap = e.getRenderAPI().getTex(Textureformat.RGBA8, 64*4, 30,Filter.LINEAR);
		sh2=e.getRenderAPI().getShader(Type.COMPUTE, IOUtils.stream2string(IOUtils.getinfromclasspath(Lighting.class, "Light.glsl")));
		sh=e.getRenderAPI().getShader(Type.COMPUTE, IOUtils.stream2string(IOUtils.getinfromclasspath(Lighting.class, "Lighting.glsl")));
//		lights=new float[Lightmap.getHeight()*3];
//		for(int i=0;i<Lightmap.getHeight();i++) {
//			lights[i*3]=(float) (Math.random()*1200f);
//			lights[i*3+1]=(float) (Math.random()*700f);
//			lights[i*3+2]=100f+(float) (Math.random()*100);
//		}
	}
	
	@Override
	public void process(Engine e, Texture t) {
		if(!enabled)
			return;
		List<Component> comps=e.em.getComponents(CLight.class);
		float[] lights=null;
		if(!comps.isEmpty()) {
			lights=new float[comps.size()*3];
			if(Lightmap.getHeight()!=comps.size()||quality!=Lightmap.getWidth()/64) {
				Lightmap.destroy();
				Lightmap=e.getRenderAPI().getTex(Textureformat.RGBA8, (int) (64*quality), comps.size(),Filter.LINEAR);
			}
			for(int i=0;i<comps.size();i++) {
				CLight c=(CLight) comps.get(i);
				lights[i*3]=c.lightpos.x+e.em.getTranslation().x+c.getEntity().getPos().x;
				lights[i*3+1]=c.lightpos.y+e.em.getTranslation().y+c.getEntity().getPos().y;
				lights[i*3+2]=c.size;
			}
			
			sh.setImage(0, e.getFinalBuffer().getAttachments()[1]);
			sh.setImage(1, Lightmap);
			sh.setVal("lightpos", (byte)3, lights);
			e.getRenderAPI().submit(sh, Lightmap.getWidth()/64, Lightmap.getHeight(), 1);
		}else {
			lights=new float[3];
		}
		
		sh2.setVal("GlobalLight", GlobalLight);
		sh2.setImage(0, t);
		sh2.setImage(1, Lightmap);
		sh.setImage(2, e.getFinalBuffer().getAttachments()[1]);
		sh2.setVal("lightpos", (byte)3, lights);
		sh2.setVal("lights", comps.size());
		e.getRenderAPI().submit(sh2, t.getWidth()/8, t.getHeight()/8, 1);
	}
	
	

}

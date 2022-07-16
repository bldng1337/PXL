package me.pxl.Post.post.Tonemap;

import me.pxl.Engine;
import me.pxl.Backend.Generic.Shader;
import me.pxl.Backend.Generic.Texture;
import me.pxl.Backend.Generic.Shader.Type;
import me.pxl.Post.Post;
import me.pxl.Utils.IOUtils;

public class Tonemap extends Post{
	Shader test;
	public boolean Gamma=false;
	public ToneType TType=ToneType.NONE;
	private ToneType LType=ToneType.NONE;
	public float exposure=0.5f;
	private boolean LGamma=false;
	private String shsrc;
	public static enum ToneType{
		REINHARD(1),EXPOSURE(2),ACES(3),NONE(0);
		int i;
		ToneType(int i){
			this.i=i;
		}
		public int getID() {
			return i;
		}
	}
	
	public Tonemap() {
		Engine e=Engine.getEngine();
		shsrc=IOUtils.stream2string(IOUtils.getinfromclasspath(Tonemap.class, "Tonemap.glsl"));
		test=e.getRenderAPI().getShader(Type.COMPUTE, (Gamma?"#define GAMMA\n":"")+"#define TONEMODE "+TType.getID()+"\n"+shsrc);
	}
	
	@Override
	public void process(Engine e,Texture t) {
		try {
			this.getClass().getField("exposure").setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e1) {
			e1.printStackTrace();
		}
		if(TType!=LType||Gamma!=LGamma) {//Pos Process Mode Changed so we have to recompile
			LType=TType;
			LGamma=Gamma;
			test=e.getRenderAPI().getShader(Type.COMPUTE, (Gamma?"#define GAMMA\n":"")+"#define TONEMODE "+TType.getID()+"\n"+shsrc);
		}
		Texture in=t;
		Texture out=e.getFinalImage();
		if(TType.equals(ToneType.EXPOSURE))
			test.setVal("exposure", exposure/100f);
		test.setImage(0, in);
		test.setImage(1, out);
		e.getRenderAPI().submit(test, out.getWidth()/8, out.getHeight()/8, 1);
	}

}

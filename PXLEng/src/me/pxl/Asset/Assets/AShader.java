package me.pxl.Asset.Assets;

import java.nio.file.Path;

import me.pxl.Asset.Asset;
import me.pxl.Backend.Generic.RenderAPI;
import me.pxl.Backend.Generic.Shader;
import me.pxl.Utils.IOUtils;

public class AShader extends Asset{
	public static String[] ext={"glsl"};
	public class DShader extends Asset.DataAsset {
		public DShader(RenderAPI r) {
			super(r);
		}

		Shader s,s1;
		
		@Override
		protected void unload() {
			if(s!=null)
				s.destroy();
			s=null;
			if(s1!=null)
				s1.destroy();
			s1=null;
		}
		
		@Override
		protected void load(Path f) {
			super.load(f);
			String src=IOUtils.includeget(f);
			s=r.getShader(src.contains("FRAG")?Shader.Type.VERTEXFRAGMENT:Shader.Type.COMPUTE, src);
		}

		@Override
		protected void reload(Path f) {
			String src=IOUtils.includeget(f);
			s1=r.getShader(src.contains("FRAG")?Shader.Type.VERTEXFRAGMENT:Shader.Type.COMPUTE, src);
		}

		@Override
		protected void swap() {
			if(s1!=null) {
				s.destroy();
				s=s1;
				s1=null;
			}
		}
	}
	
	public Shader getShader() {
		return ((DShader)getAs()).s;
	}
	
	public AShader(RenderAPI r) {
		super(r);
		this.as=new DShader(r);
	}

	public AShader(DataAsset dsa) {
		super(dsa);
	}
	


}

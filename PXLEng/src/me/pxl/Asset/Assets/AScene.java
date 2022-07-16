package me.pxl.Asset.Assets;

import java.nio.file.Path;

import me.pxl.Asset.Asset;
import me.pxl.Backend.Generic.RenderAPI;
import me.pxl.Utils.IOUtils;

public class AScene extends Asset{
	public static String[] ext={"scene"};
	public class DScene extends Asset.DataAsset{
		public DScene(RenderAPI r) {
			super(r);
		}

		String s,s1;
		@Override
		protected void unload() {
			s=null;
		}
		
		@Override
		protected void load(Path f) {
			s=IOUtils.stringfromFile(f);
		}

		@Override
		protected void reload(Path f) {
			s1=IOUtils.stringfromFile(f);
		}

		@Override
		protected void swap() {
			s=s1;
		}
	}

	public AScene(DataAsset dsa) {
		super(dsa);
	}
	
	public AScene(RenderAPI r) {
		super(r);
		this.as=new DScene(r);
	}

	public String getScene() {
		return ((DScene)getAs()).s;
	}

}

package me.pxl.Asset.Assets;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

import me.pxl.Asset.Asset;
import me.pxl.Backend.Generic.RenderAPI;
import me.pxl.Utils.Utils;
/**
 * Class holding Java Code as Asset
 * @author bldng
 *
 */
public class AJava extends Asset{
	public static String[] ext={"jar","out"};
	public class DJava extends Asset.DataAsset{
		public DJava(RenderAPI r) {
			super(r);
		}
		private URLClassLoader cl;
		Class<?> c;
		Object o;
		
		@Override
		protected void load(Path f) {
			try {
				cl = new URLClassLoader(new URL[] {f.toRealPath().toUri().toURL()});
				c=cl.loadClass("Init");
				o=c.getConstructor().newInstance();
				c.getMethod("init",URLClassLoader.class).invoke(o, cl);
				Utils.addClassloader(cl);
			} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			super.load(f);
		}

		@Override
		protected void unload() {
			try {
				cl.close();
				Utils.removeClassloader(cl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void reload(Path f) {
			//prob not
		}

		@Override
		protected void swap() {
			//prob not
		}
	}
	

	public AJava(RenderAPI r) {
		super(r);
		this.as=new DJava(r);
	}
	
	public AJava(DataAsset dsa) {
		super(dsa);
	}
	

}

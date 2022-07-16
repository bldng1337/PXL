package me.pxl.Asset.Assets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import me.pxl.Asset.Asset;
import me.pxl.Backend.Generic.RenderAPI;
import me.pxl.Backend.Generic.Texture;
import me.pxl.Backend.Generic.Texture.Textureformat;

public class ATexture extends Asset{
	public static String[] ext={"png","jpg"};
	public class DTexture extends Asset.DataAsset{
		protected float width;
		protected float height;
		public DTexture(RenderAPI r) {
			super(r);
		}

		Texture t;
		@Override
		protected void unload() {
			t.destroy();
			t=null;
		}
		
		ByteBuffer bf;
		@Override
		protected void reload(Path f) {
			try {
				bf = img(getImage(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		protected void swap() {
//			t.destroy();
			t.load(bf);
		}
		
		ByteBuffer bb;
		@Override
		protected void load(Path f) {
			super.load(f);
			try {
				BufferedImage bf=getImage(f);
				bb=img(bf);
				t=r.getTex(Textureformat.RGBA8, bf.getWidth(), bf.getHeight());
				width=bf.getWidth();
				height=bf.getHeight();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		protected BufferedImage getImage(Path f) throws IOException {
			return ImageIO.read(f.toFile());
		}
		
		protected ByteBuffer img(BufferedImage bf) {
			int[] pixels = new int[bf.getWidth() * bf.getHeight()];
			bf.getRGB(0, 0, bf.getWidth(), bf.getHeight(), pixels, 0, bf.getWidth());
			ByteBuffer ib=BufferUtils.createByteBuffer(bf.getHeight() * bf.getWidth()*4);
			for (int i = 0; i < bf.getHeight() * bf.getWidth(); i++) {
				int a = (pixels[i] & 0xff000000) >> 24;
				int r = (pixels[i] & 0xff0000) >> 16;
				int g = (pixels[i] & 0xff00) >> 8;
				int b = (pixels[i] & 0xff);
				ib.putInt(a << 24 | b << 16 | g << 8 | r);
			}
			ib.flip();
			return ib;
		}

		@Override
		protected void finalizeloading() {
			super.finalizeloading();
			t.load(bb);
		}
	}
	
	public ATexture(RenderAPI r) {
		super(r);
		this.as=new DTexture(r);
	}
	public ATexture(DataAsset dsa) {
		super(dsa);
	}
	public Texture getT() {
		return ((DTexture)getAs()).t;
	}

	protected float[] f=new float[] {0,0,1,1};
	public float[] getTextureCoords() {
		return f;
	}
}

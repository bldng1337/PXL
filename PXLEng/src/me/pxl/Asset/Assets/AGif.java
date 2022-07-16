package me.pxl.Asset.Assets;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import me.pxl.Backend.Generic.RenderAPI;
import me.pxl.Serialize.Serialization;

public class AGif extends AAnimation{
	public static String[] ext={"gif"};
	
	public class DGif extends ATexture.DTexture{
		public DGif(RenderAPI r) {
			super(r);
		}
		int w,h,frames;
		
		@Override
		protected BufferedImage getImage(Path f) throws IOException {
			ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
		    ImageInputStream ciis = ImageIO.createImageInputStream(f.toFile());
		    reader.setInput(ciis, false);
		    int frames = reader.getNumImages(true);
		    BufferedImage bfi = reader.read(0);
		    int width=bfi.getWidth()*frames;
		    int nwidth=bfi.getWidth();
		    int height=bfi.getHeight();
		    w=width;
		    h=height;
		    this.frames=frames;
		    BufferedImage bf=new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		    Graphics g=bf.getGraphics();
		    for(int i=0;i<frames;i++)
		    	g.drawImage(reader.read(i), nwidth*i, 0, null);
		    g.dispose();
			return bf;
		}
	}
	@Override
	public void finalizeloading() {
		update();
	}

	public AGif(RenderAPI r) {
		super(r);
		this.as=new DGif(r);
	}
	public AGif(DataAsset dsa) {
		super(dsa);
		setCurrTex(txt);
	}
	
	@Serialization(MethodName = "update")
	public int txt=0;
	@Override
	public void update() 
	{
		setCurrTex(txt);
	}
	
	public void setCurrTex(int currtxt) {
		DGif g=(DGif) getAs();
		currtxt=Math.abs(currtxt)%g.frames;
		float fe=((float)g.w/(g.frames*g.w));
		f=new float[] {(fe*currtxt),0,(fe*(currtxt+1)),1};
	}
	
	

}

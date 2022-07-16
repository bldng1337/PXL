package me.pxl.editor.Utils;

import java.awt.FileDialog;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.lwjgl.BufferUtils;

import me.pxl.Engine;
import me.pxl.Backend.Generic.Texture;

public class EditorUtils {

	public static Path showFileDialog(Path dir) {
		FileDialog fd = new FileDialog(new JFrame(), "Choose a file", FileDialog.LOAD);
		fd.setDirectory(dir.toAbsolutePath().toString());
		fd.setVisible(true);
		if(fd.getFiles()==null)
			return null;
		if(fd.getFiles()[0]==null)
			return null;
		return fd.getFiles()[0].toPath();
	}
	
	public static Path showFileDialog(Path dir,String end) {
		FileDialog fd = new FileDialog(new JFrame(), "Choose a file", FileDialog.LOAD);
		fd.setDirectory(dir.toAbsolutePath().toString());
		fd.setFile("*."+end);
		fd.setVisible(true);
		if(fd.getFiles()[0]==null)
			return null;
		return fd.getFiles()[0].toPath();
	}
	
	public static ByteBuffer img(BufferedImage bf) {
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
	
	public static Texture getTex(Engine e,InputStream s) {
		try {
			BufferedImage bf=ImageIO.read(s);
			if(bf==null)
				throw new Exception();
			Texture t=e.getRenderAPI().getTex(Texture.Textureformat.RGBA8,bf.getWidth(),bf.getHeight());
			t.load(img(bf));
			return t;
		} catch (Exception e1) {
			return e.getRenderAPI().getTex(Texture.Textureformat.RGBA8,0,0);
		}
	}
	
}

package me.pxl.Backend;

import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.stb.STBTruetype.stbtt_PackBegin;
import static org.lwjgl.stb.STBTruetype.stbtt_PackEnd;
import static org.lwjgl.stb.STBTruetype.stbtt_PackFontRange;
import static org.lwjgl.stb.STBTruetype.stbtt_PackSetOversampling;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import me.pxl.Backend.Generic.RenderAPI;
import me.pxl.Backend.Generic.Texture;
import me.pxl.Backend.Generic.Texture.Textureformat;
import me.pxl.Utils.IOUtils;

public class STBFont {
	STBTTFontinfo fontInfo;
	STBTTPackedchar.Buffer cdata;
    public Texture t;
    float scale;
    float descent;
    int BITMAP_W = 1024;
    int BITMAP_H = 1024;
    int fontheight;
    ByteBuffer bb;
    public STBFont(RenderAPI r,String resource,int fontheight) throws IOException {
    	fontInfo = STBTTFontinfo.create();
    	cdata    = STBTTPackedchar.create(95);
    	this.t   = r.getTex(Textureformat.RGBA8, BITMAP_W, BITMAP_H);
    	bb=IOUtils.ioResourceToByteBuffer(resource, 512 * 1024);
    	try (MemoryStack stack = stackPush()) {
            stbtt_InitFont(fontInfo, bb);
            scale = stbtt_ScaleForPixelHeight(fontInfo, fontheight);

            IntBuffer d = stack.mallocInt(1);
            stbtt_GetFontVMetrics(fontInfo, null, d, null);
            descent = d.get(0) * scale;

            ByteBuffer bitmap = memAlloc(BITMAP_W * BITMAP_H);

            STBTTPackContext pc = STBTTPackContext.malloc(stack);
            stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, NULL);
            stbtt_PackSetOversampling(pc, 4, 4);
            stbtt_PackFontRange(pc, bb, 0, fontheight, 32, cdata);
            stbtt_PackEnd(pc);

            // Convert R8 to RGBA8
            ByteBuffer texture = memAlloc(BITMAP_W * BITMAP_H * 4);
            for (int i = 0; i < bitmap.capacity(); i++)
                texture.putInt((bitmap.get(i) << 24) | 0x00FFFFFF);
            texture.flip();
            this.t.load(texture);
            memFree(texture);
            memFree(bitmap);
        }
    	this.fontheight=fontheight;
    }
    
    public int getFontheight() {
		return fontheight;
	}

	public STBTTFontinfo getFontInfo() {
		return fontInfo;
	}

	public STBTTPackedchar.Buffer getCdata() {
		return cdata;
	}

	public Texture getT() {
		return t;
	}

	public float getScale() {
		return scale;
	}

	public float getDescent() {
		return descent;
	}

	public int getBITMAP_W() {
		return BITMAP_W;
	}

	public int getBITMAP_H() {
		return BITMAP_H;
	}
}

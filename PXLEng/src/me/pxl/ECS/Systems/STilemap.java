package me.pxl.ECS.Systems;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import me.pxl.Engine;
import me.pxl.Asset.Assets.ATexture;
import me.pxl.Backend.Generic.RenderPass;
import me.pxl.Backend.Generic.Shader;
import me.pxl.Backend.Generic.Texture;
import me.pxl.Backend.Generic.VertexArray;
import me.pxl.Backend.Generic.Buffer.RByteBuffer;
import me.pxl.Backend.Generic.Buffer.RIndexBuffer;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.Attrib;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.DType;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.Usage;
import me.pxl.Backend.Generic.Buffer.RIndexBuffer.Save;
import me.pxl.Backend.Generic.Shader.Type;
import me.pxl.ECS.Component;
import me.pxl.ECS.Components.CTilemap;
import me.pxl.ECS.Components.CTilemap.Tile;

public class STilemap extends System {

	public final int MAXDRAW = 1000;
	public final int MAXTEX = 10;

	protected RByteBuffer rbb;
	protected RIndexBuffer rib;
	protected VertexArray varr;
	protected RenderPass rp;
	protected Shader std;

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public boolean editor() {
		return true;
	}

	@Override
	public void init(Engine e) {
		rbb = e.getRenderAPI().getBbuf(Usage.STREAM, MAXDRAW * 4, new Attrib(DType.FLOAT, 3),
				new Attrib(DType.FLOAT, 2), new Attrib(DType.FLOAT, 1));
		rib = e.getRenderAPI().getibuf(Usage.STREAM, MAXDRAW * 6);
		varr = e.getRenderAPI().getVArr(rib, rbb);
		std = e.getRenderAPI().getShader(Type.VERTEXFRAGMENT, SRenderer.SHADERCODE);
		rp = e.getRenderAPI().getRp(std, varr, e.getRenderAPI().getScreen());
		rp.setOffset(0);
	}

	@Override
	public void update(Engine em) {
		rp.setFb(em.getFinalBuffer());
		List<Component> clist = em.em.getComponents(CTilemap.class);
		int tex = 0;
		int i=0;
		Texture curr = null;
		Matrix4f m = new Matrix4f();
		m.identity();
		m.ortho(0, em.getFinalBuffer().getWidth(), 0, em.getFinalBuffer().getHeight(),-20,20);
		Matrix4f n=new Matrix4f();
		n.identity();
		Vector3f v=em.em.getTranslation();
		n.translate(v);
		m.mul(n);
		rp.getS().setVal("proj", m);
		Vector3f temp=new Vector3f();
		em.getRenderAPI().disableDepth();
		try (Save<ByteBuffer> b = rbb.load(); Save<IntBuffer> ib = rib.load()) {
			for (Component c : clist) {
				CTilemap ct = (CTilemap) c;
				if(ct.l.isEmpty())
					continue;
				for (int x = 0; x < ct.getMap().length; x++) {
					for (int y = 0; y < ct.getMap()[0].length; y++) {
						if(ct.getMap()[x][y]<0)
							continue;
						if (ct.l.get(ct.getMap()[x][y]) == null)
							continue;
						if(ct.l.size()<=ct.getMap()[x][y])
							continue;
						ATexture t = ((Tile)ct.l.get(ct.getMap()[x][y])).getTexture(x,y);
						if (t == null)
							continue;
						if (tex > MAXTEX) {
							tex = 0;
							{
								// RERENDER
								b.unload();
								ib.unload();
								rp.setCount(i * 6);
								em.getRenderAPI().submit(rp);
								b.reload();
								ib.reload();
								i = 0;
							}
							rp.getS().setTexture(0, curr);
						}
						if(!t.getT().equals(curr)) {//This Component uses another Texture so we have to update the current Texture
							tex++;
							curr=t.getT();
							rp.getS().setTexture(tex, curr);
						}
						float[] uv=t.getTextureCoords();
						
						//0 0
						ct.getEntity().pos
						.add(x*(ct.getEntity().getSize().x/ct.numtiles.x),y*(ct.getEntity().getSize().y/ct.numtiles.y),0,temp)
						.get(b.getBuf());
						b.getBuf().position(b.getBuf().position()+3*4);
						b.getBuf().putFloat(uv[0]).putFloat(uv[1]);
						b.getBuf().putFloat(tex);
						
						//0 1
						ct.getEntity().pos
						.add(x*(ct.getEntity().getSize().x/ct.numtiles.x),y*(ct.getEntity().getSize().y/ct.numtiles.y),0,temp)
						.add(0,(ct.getEntity().getSize().y/ct.numtiles.y),0,temp)
						.get(b.getBuf());
						b.getBuf().position(b.getBuf().position()+3*4);
						b.getBuf().putFloat(uv[0]).putFloat(uv[3]);
						b.getBuf().putFloat(tex);
						//1 0
						ct.getEntity().pos
						.add(x*(ct.getEntity().getSize().x/ct.numtiles.x),y*(ct.getEntity().getSize().y/ct.numtiles.y),0,temp)
						.add((ct.getEntity().getSize().x/ct.numtiles.x),0,0,temp)
						.get(b.getBuf());
						b.getBuf().position(b.getBuf().position()+3*4);
						b.getBuf().putFloat(uv[2]).putFloat(uv[1]);
						b.getBuf().putFloat(tex);
						//1 1
						ct.getEntity().pos
						.add(x*(ct.getEntity().getSize().x/ct.numtiles.x),y*(ct.getEntity().getSize().y/ct.numtiles.y),0,temp)
						.add((ct.getEntity().getSize().x/ct.numtiles.x),(ct.getEntity().getSize().y/ct.numtiles.y),0,temp)
						.get(b.getBuf());
						b.getBuf().position(b.getBuf().position()+3*4);
						b.getBuf().putFloat(uv[2]).putFloat(uv[3]);
						b.getBuf().putFloat(tex);
						
						ib.getBuf().put(new int[] {i*4+0,i*4+1,i*4+2,i*4+1,i*4+3,i*4+2});
						i++;
					}
				}
			}
		}
		if(i>0) {
			rp.setCount(i*6);
			em.getRenderAPI().submit(rp);
		}
	}

}

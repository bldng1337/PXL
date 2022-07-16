package me.pxl.ECS.Systems;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Comparator;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import me.pxl.Engine;
import me.pxl.Asset.Assets.AShader;
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
import me.pxl.ECS.Components.CRender;

public class SRenderer extends System{//Will prob get reworked down the Line
	
	public static final String SHADERCODE="struct Vertexdata {\r\n"
			+ "	//Texture\r\n"
			+ "	float t_index;\r\n"
			+ "	vec2 t_coord;\r\n"
			+ "};\r\n"
			+ "//Vertex Shader\r\n"
			+ "#if VERT \r\n"
			+ "layout (location = 0) in vec3 a_Pos;\r\n"
			+ "layout (location = 1) in vec2 a_Texc;\r\n"
			+ "layout (location = 2) in float a_Tex;\r\n"
			+ "uniform mat4 proj;\r\n"
			+ "out Vertexdata vdata;\r\n"
			+ "\r\n"
			+ "void main()\r\n"
			+ "{\r\n"
			+ "    vdata.t_index=a_Tex;\r\n"
			+ "	vdata.t_coord=a_Texc;\r\n"
			+ " 	gl_Position=proj*vec4(a_Pos,1.0f);\r\n"
			+ "}\r\n"
			+ "#endif\r\n"
			+ "#if FRAG\r\n"
			+ "layout (location = 0) out vec4 o_Color;\r\n"
			+ "layout (binding = 0) uniform sampler2D u_Textures[32];\r\n"
			+ "in Vertexdata vdata;\r\n"
			+ "void main()\r\n"
			+ "{\r\n"
			+ "    vec4 alb=texture(u_Textures[int(vdata.t_index)],vdata.t_coord);\r\n"
			+ "    if(alb.a<=0)\r\n"
			+ "		discard;\r\n"
			+ "    o_Color=alb;\r\n"
			+ "}\r\n"
			+ "#endif";

	public final int MAXDRAW=1000;
	public final int MAXTEX=10;
	private int drawcalls=0;
	
	public int getDrawcalls() {
		return drawcalls;
	}
	protected RByteBuffer rbb;
	protected RIndexBuffer rib;
	protected VertexArray varr;
	protected RenderPass rp;
	Vector3f temp=new Vector3f();
	
	@Override
	public boolean editor() {
		return true;
	}

	@Override
	public void init(Engine e) {
		rbb=e.getRenderAPI().getBbuf(Usage.STREAM, MAXDRAW*4,new Attrib(DType.FLOAT,3),new Attrib(DType.FLOAT,2),new Attrib(DType.FLOAT, 1));
		rib=e.getRenderAPI().getibuf(Usage.STREAM, MAXDRAW*6);
		varr=e.getRenderAPI().getVArr(rib, rbb);
		rp=e.getRenderAPI().getRp(null, varr, e.getRenderAPI().getScreen());
		rp.setOffset(0);
		std=e.getRenderAPI().getShader(Type.VERTEXFRAGMENT, SHADERCODE);
	}
	Shader std;
	private Shader getShader(AShader a) {
		if(a==null)
			return std;
		return a.getShader();
	}

	@Override
	public void update(Engine e) {
		drawcalls=0;
		rp.setFb(e.getFinalBuffer());
		e.em.getComponents(CRender.class).sort(new Comparator<Component>() {//Sorts the List before Rendering so that Components requiring the same Shader are next to each other
			@Override
			public int compare(Component o1, Component o2) {
				CRender c1=(CRender) o1;
				CRender c2=(CRender) o2;
				if(c1.sh==null||c2.sh==null||c1.txt==null||c2.txt==null)
					return 0;
				int i=getShader(c1.sh).getID()-getShader(c2.sh).getID();
				if(i!=0)
					return i;
				return c1.txt.getT().getID()-c2.txt.getT().getID();
			}
		});
		int i=0;
		int tex=-1;
		Texture currtxt=null;
		rp.setS(null);
		Matrix4f m = new Matrix4f();
		m.identity();
		m.ortho(0, e.getFinalBuffer().getWidth(), 0, e.getFinalBuffer().getHeight(),-20,20);
		Matrix4f n=new Matrix4f();
		n.identity();
		Vector3f v=e.em.getTranslation();
		n.translate(v);
		m.mul(n);
		e.getRenderAPI().enableDepth();
		Matrix3f rot=new Matrix3f();
		try(Save<ByteBuffer> b=rbb.load();Save<IntBuffer> ib=rib.load()){
			for(Component c:e.em.getComponents(CRender.class)) {
				CRender cr=(CRender)c;
				if(cr.txt==null)
					continue;
				if(i>=MAXDRAW) {//We filled the buffer so we have to flush it
					{
						drawcalls++;
						//RERENDER
						b.unload();
						ib.unload();
						rp.setCount(i*6);
						e.getRenderAPI().submit(rp);
						b.reload();
						ib.reload();
						i=0;
					}
				}
				if(tex>MAXTEX) {//Used more than MAX Textures Textures so we have to split drawcalls
					tex=0;
					{
						drawcalls++;
						//RERENDER
						b.unload();
						ib.unload();
						rp.setCount(i*6);
						e.getRenderAPI().submit(rp);
						b.reload();
						ib.reload();
						i=0;
					}
					rp.getS().setTexture(0, currtxt);
				}
				if(!getShader(cr.sh).equals(rp.getS())) {//This Component uses another Shader so we have to split drawcalls
					if(rp.getS()!=null)
					{
						drawcalls++;
						//RERENDER
						b.unload();
						ib.unload();
						rp.setCount(i*6);
						e.getRenderAPI().submit(rp);
						b.reload();
						ib.reload();
						i=0;
					}
					getShader(cr.sh).setVal("proj", m);
					rp.setS(getShader(cr.sh));
					getShader(cr.sh).clearTextures();
					if(currtxt!=null) {
						tex=0;
						rp.getS().setTexture(0, currtxt);
					}
				}
				
				if(!cr.txt.getT().equals(currtxt)) {//This Component uses another Texture so we have to update the current Texture
					tex++;
					currtxt=cr.txt.getT();
					rp.getS().setTexture(tex, currtxt);
				}
				float[] uv=cr.txt.getTextureCoords();
				rot.identity();
				rot.rotateLocalZ((float) (cr.getEntity().rotation/360*Math.PI*2));
				//0 0
				
				cr.getEntity().getPos()
				.sub(cr.getEntity().getPos(), temp)
				.mul(rot, temp)
				.add(cr.getEntity().getPos(), temp)
				.get(b.getBuf());
				b.getBuf().position(b.getBuf().position()+3*4);
				b.getBuf().putFloat(uv[0]).putFloat(uv[1]);
				b.getBuf().putFloat(tex);
				//0 1
				cr.getEntity().getPos().add(0, cr.getEntity().getSize().y, 0, temp)
				.sub(cr.getEntity().getPos(), temp)
				.mul(rot, temp)
				.add(cr.getEntity().getPos(), temp)
				.get(b.getBuf());
				b.getBuf().position(b.getBuf().position()+3*4);
				b.getBuf().putFloat(uv[0]).putFloat(uv[3]);
				b.getBuf().putFloat(tex);
				//1 0
				cr.getEntity().getPos().add(cr.getEntity().getSize().x, 0, 0, temp)
				.sub(cr.getEntity().getPos(), temp)
				.mul(rot, temp)
				.add(cr.getEntity().getPos(), temp)
				.get(b.getBuf());
				b.getBuf().position(b.getBuf().position()+3*4);
				b.getBuf().putFloat(uv[2]).putFloat(uv[1]);
				b.getBuf().putFloat(tex);
				//1 1
				cr.getEntity().getPos().add(cr.getEntity().getSize().x, cr.getEntity().getSize().y, 0, temp)
				.sub(cr.getEntity().getPos(), temp)
				.mul(rot, temp)
				.add(cr.getEntity().getPos(), temp)
				.get(b.getBuf());
				b.getBuf().position(b.getBuf().position()+3*4);
				b.getBuf().putFloat(uv[2]).putFloat(uv[3]);
				b.getBuf().putFloat(tex);
				
				ib.getBuf().put(new int[] {i*4+0,i*4+1,i*4+2,i*4+1,i*4+3,i*4+2});
				i++;
			}
		}
		if(i>0) {
			rp.setCount(i*6);
			e.getRenderAPI().submit(rp);
		}
		e.getRenderAPI().disableDepth();
	}

	@Override
	public int priority() {
		return 100;
	}

}

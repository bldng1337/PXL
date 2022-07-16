package me.pxl.Backend.GL45;


import java.nio.FloatBuffer;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL45;

import me.pxl.Backend.GL45.Buffer.NGLByteBuffer;
import me.pxl.Backend.Generic.Shader;
import me.pxl.Backend.Generic.Texture;
import me.pxl.Backend.Generic.Buffer.RByteBuffer;

public class NGLShader extends Shader{
	int prog;
	int v,f,c;
	HashMap<String, Uniform> uniformmap;
	
	static interface Uniform{
		public abstract void set(String Name,int prog);
	}
	
	static class BufferUniform implements Uniform{
		NGLByteBuffer b;
		private int id;
		public BufferUniform(RByteBuffer bb,int a) {
			this.id=a;
			b=(NGLByteBuffer)bb;
		}
		@Override
		public void set(String Name, int prog) {
			GL45.glBindBufferBase(GL45.GL_SHADER_STORAGE_BUFFER, id, b.getBuf());
		}
	}
	
	static class ImageUniform implements Uniform{
		int unit;
		NGLTexture val;
		public ImageUniform(int unit,NGLTexture val) {
			this.unit=unit;
			this.val=val;
		}
		
		@Override
		public void set(String Name,int prog) {
			GL45.glBindTextureUnit(unit, val.getID());
			switch(val.getTxtf()) {
			case BRGBA:
				GL45.glBindImageTexture(unit,val.getID(),0,false,0,GL45.GL_READ_WRITE,GL45.GL_RGBA);
				break;
			case DEPTH24STENCIL8:
				GL45.glBindImageTexture(unit,val.getID(),0,false,0,GL45.GL_READ_WRITE,GL45.GL_DEPTH24_STENCIL8);
				break;
			case RGBA16F:
				GL45.glBindImageTexture(unit,val.getID(),0,false,0,GL45.GL_READ_WRITE,GL45.GL_RGBA16F);
				break;
			case RGBA32F:
				GL45.glBindImageTexture(unit,val.getID(),0,false,0,GL45.GL_READ_WRITE,GL45.GL_RGBA32F);
				break;
			case RGBA8:
				GL45.glBindImageTexture(unit,val.getID(),0,false,0,GL45.GL_READ_WRITE,GL45.GL_RGBA8);
				break;
			default:
				break;
			
			}
			
		}
	}
	
	static class TextureUniform implements Uniform{
		int unit;
		int val;
		public TextureUniform(int unit,int val) {
			this.unit=unit;
			this.val=val;
		}
		
		@Override
		public void set(String Name,int prog) {
			GL45.glBindTextureUnit(unit, val);
			
//			GL45.glBindSampler(0, prog);
//			int[] i=new int[] {1};
//			GL45.glGetIntegerv(GL45.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS,i);
		}
	}
	static class IntUniform implements Uniform{
		int[] data;
		int size;
		public IntUniform(int size,int... data) {
			this.data=data;
			this.size=size;
		}
		@Override
		public void set(String Name,int prog) {
			int loc=GL45.glGetUniformLocation(prog, Name);
			if(loc!=-1)
				switch(size) {
				case 1:
					if(data.length<=size)
						GL45.glUniform1i(loc, data[0]);
					else
						GL45.glUniform1iv(loc, data);
					break;
				case 2:
					if(data.length<=size)
						GL45.glUniform2i(loc,data[0],data[1]);
					else
						GL45.glUniform2iv(loc, data);
					break;
				case 3:
					if(data.length<=size)
						GL45.glUniform3i(loc,data[0],data[1],data[2]);
					else
						GL45.glUniform3iv(loc, data);
					break;
				case 4:
					if(data.length<=size)
						GL45.glUniform4i(loc, data[0],data[1],data[2],data[3]);
					else
						GL45.glUniform4iv(loc, data);
					
					break;
				default:
					GL45.glUniform1iv(loc, data);
					break;
				}
		}
	}
	static class FloatUniform implements Uniform{
		float[] data;
		int size;
		public FloatUniform(int size,float... data) {
			this.data=data;
			this.size=size;
		}
		@Override
		public void set(String Name,int prog) {
			int loc=GL45.glGetUniformLocation(prog, Name);
			if(loc!=-1)
				switch(size) {
				case 1:
					if(data.length<=size)
						GL45.glUniform1f(loc, data[0]);
					else
						GL45.glUniform1fv(loc, data);
					break;
				case 2:
					if(data.length<=size)
						GL45.glUniform2f(loc,data[0],data[1]);
					else
						GL45.glUniform2fv(loc, data);
					break;
				case 3:
					if(data.length<=size)
						GL45.glUniform3f(loc,data[0],data[1],data[2]);
					else
						GL45.glUniform3fv(loc, data);
					break;
				case 4:
					if(data.length<=size)
						GL45.glUniform4f(loc, data[0],data[1],data[2],data[3]);
					else
						GL45.glUniform4fv(loc, data);
					
					break;
				default:
					GL45.glUniform1fv(loc, data);
					break;
				}
		}
	}
	
	static class MatUniform implements Uniform{
		Matrix4f data;
		public MatUniform(Matrix4f data) {
			this.data=data;
		}
		@Override
		public void set(String Name,int prog) {
			int loc=GL45.glGetUniformLocation(prog, Name);
			if(loc!=-1) {
				FloatBuffer fb=BufferUtils.createFloatBuffer(16);
				data.get(fb);
				GL45.glUniformMatrix4fv(loc,false, fb);
			}
		}
	}
	
	public NGLShader(Type t, String src) {
		super(t, src);
		uniformmap=new HashMap<>();
		switch(t) {
		case COMPUTE:
			c=this.createShader("#version 450\n#define FRAG 0\n#define COMP 1\n#define VERT 0\n#define OPENGL 1\n"+src, GL45.GL_COMPUTE_SHADER);
			prog=GL45.glCreateProgram();
			if (prog == 0) {
				System.out.println("Shader prog creation failed!");
				return;
			}
			GL45.glAttachShader(prog, c);
			GL45.glLinkProgram(prog);
			break;
		case VERTEXFRAGMENT:
			f=this.createShader("#version 450\n#define FRAG 1\n#define COMP 0\n#define VERT 0\n#define OPENGL 1\n"+src, GL45.GL_FRAGMENT_SHADER);
			v=this.createShader("#version 450\n#define FRAG 0\n#define COMP 0\n#define VERT 1\n#define OPENGL 1\n"+src, GL45.GL_VERTEX_SHADER);
			prog=GL45.glCreateProgram();
			if (prog == 0) {
				System.out.println("Shader prog creation failed!");
				return;
			}
			GL45.glAttachShader(prog, v);
			GL45.glAttachShader(prog, f);
			GL45.glLinkProgram(prog);
//			if (ARBShaderObjects.glGetObjectParameteriARB(prog,
//					ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL45.GL_FALSE) {
//				System.out.println("Error glLink\n"+this.toString());
//				prog=0;
//				return;
//			}
			GL45.glValidateProgram(prog);
//			if (ARBShaderObjects.glGetObjectParameteriARB(prog,
//					ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL45.GL_FALSE) {
//				System.out.println("Error glValidate\n"+this.toString());
//				prog=0;
//			}
			break;
		}
	}
	
	private int createShader(String shaderSource, int shaderType) {
		int shader = 0;
		shader = GL45.glCreateShader(shaderType);
		if (shader == 0)
			return 0;
		GL45.glShaderSource(shader, shaderSource);
		GL45.glCompileShader(shader);
		if (GL45.glGetShaderi(shader,
				GL45.GL_COMPILE_STATUS) == GL45.GL_FALSE) {
			String error = GL45.glGetProgramInfoLog(shader, GL45.GL_INFO_LOG_LENGTH);
			System.err.println(shaderSource+"\nFailed to create Shader: "+error);
			return 0;
		}
		return shader;
	}
	
	public void setupUniform() {
		uniformmap.forEach((name,uni)->{
			uni.set(name, prog);
		});
	}

	@Override
	public void setVal(String Name, int... val) {
		uniformmap.put(Name, new IntUniform(1,val));
	}
	
	@Override
	public void setVal(String Name,byte siz,int... val) {
		uniformmap.put(Name, new IntUniform(siz,val));
	}

	@Override
	public void setVal(String Name, float... val) {
		uniformmap.put(Name, new FloatUniform(1,val));
	}
	
	@Override
	public void setVal(String Name,byte siz,float... val) {
		uniformmap.put(Name, new FloatUniform(siz,val));
	}

	@Override
	public void setVal(String Name, Vector2f val) {
		uniformmap.put(Name, new FloatUniform(2,val.x,val.y));
	}

	@Override
	public void setVal(String Name, Vector3f val) {
		uniformmap.put(Name, new FloatUniform(3,val.x,val.y,val.z));
	}

	@Override
	public void setVal(String Name, Vector4f val) {
		uniformmap.put(Name, new FloatUniform(4,val.x,val.y,val.z,val.w));
	}

	@Override
	public void setVal(String Name, Matrix4f val) {
		uniformmap.put(Name, new MatUniform(val));
	}
	
	public void clearTextures() {
		for(int i=0;i<32;i++)
			uniformmap.remove("TXT"+i);
	}
	public void setBuffer(RByteBuffer b,int id) {
		uniformmap.put("BUF"+id, new BufferUniform(b, id));
	}

	@Override
	public void setTexture(int unit,Texture val) {
		if(val==null)
			uniformmap.remove("TXT"+unit);
		else
			setTexture(unit, val.getID());
	}
	
	@Override
	public void setImage(int unit,Texture val) {
		if(val==null)
			uniformmap.remove("TXT"+unit);
		else
			uniformmap.put("TXT"+unit, new ImageUniform(unit, (NGLTexture) val));
	}
	
	public void setTexture(int unit,int val) {
		uniformmap.put("TXT"+unit, new TextureUniform(unit, val));
	}

	@Override
	public void destroy() {
		GL45.glDeleteProgram(prog);
		GL45.glDeleteShader(f);
		GL45.glDeleteShader(v);
	}

	@Override
	public Texture getTexture(int unit) {
//		if(uniformmap.containsKey("TXT"+unit))
			return null;
//		return ((TextureUniform)uniformmap.get("TXT"+unit)).val;
	}

	@Override
	public int getID() {
		return prog;
	}

}

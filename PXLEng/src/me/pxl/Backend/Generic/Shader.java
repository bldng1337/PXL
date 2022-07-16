package me.pxl.Backend.Generic;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class Shader {
	public enum Type{
		COMPUTE,VERTEXFRAGMENT
	}
	public Shader(Type t,String src) {
	}
	
	public abstract void setVal(String Name,int... val);
	public abstract void setVal(String Name,byte size,int... val);
	public abstract void setVal(String Name,float... val);
	public abstract void setVal(String Name,byte size,float... val);
	public abstract void setVal(String Name,Vector2f val);
	public abstract void setVal(String Name,Vector3f val);
	public abstract void setVal(String Name,Vector4f val);
	public abstract void setVal(String Name,Matrix4f val);
	public abstract void setTexture(int unit,Texture val);
	public abstract void setTexture(int unit,int id);
	public abstract Texture getTexture(int unit);
	public abstract int getID();
	public abstract void destroy();

	public abstract void clearTextures();

	public abstract void setImage(int unit, Texture val);
}

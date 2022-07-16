package me.pxl.Backend.GL45;

import static org.lwjgl.opengl.GL15C.glBindBuffer;

import java.util.HashMap;

import org.lwjgl.opengl.GL45;

public class GlStateManager {
private GlStateManager() {}
	
	/**
	 * Hasmap that keeps Track of the States of the GL Flags
	 */
	static HashMap<Integer, Boolean> states=new HashMap<>();
	static HashMap<Integer, Boolean> vbo=new HashMap<>();
	/**
	 * Keeps Track which texture, shader or Vertexarray is curently bound
	 */
	static int txt=-1,shader=0,vao=-1;
	
	/**
	 * Enables an flag in the OpenGL Context
	 * @param flag The flag to enable
	 */
	public static void enable(int flag) {
		if(states.containsKey(flag)) {
			if(!states.get(flag))
				GL45.glEnable(flag);
		}else {
			states.put(flag, true);
			GL45.glEnable(flag);
		}
	}
	
	/**
	 * Disables an flag in the OpenGL Context
	 * @param flag The flag to disable
	 */
	public static void disable(int flag) {
		if(states.containsKey(flag)) {
			if(states.get(flag)) {
				GL45.glDisable(flag);
				states.put(flag, false);
			}
		}else {
			states.put(flag, false);
			GL45.glDisable(flag);
		}
	}
	
	public static void disable(int... flag) {
		for(int i:flag)
			disable(i);
	}
	
	public static void enable(int... flag) {
		for(int i:flag)
			enable(i);
	}
	
	/**
	 * Binds an Texture
	 * @param id The Texture to be bound
	 */
	public static void bindTexture2D(int id) {
		if(txt!=id)
			GL45.glBindTexture(GL45.GL_TEXTURE_2D, id);
	}
	
	
	/**
	 * Unbinds the currently bound Texture
	 */
	public static void unbindTexture2D() {
		if(txt!=0)
			GL45.glBindTexture(GL45.GL_TEXTURE_2D, 0);
	}
	static int tx,ty,tw,th;
	public static void setViewport(int x,int y,int w,int h) {
		if(tx==x&&ty==y&&tw==w&&th==h)
			return;
		tx=x;
		ty=y;
		tw=w;
		th=h;
		GL45.glViewport(x, y, w, h);
	}
	
	/**
	 * Binds an Shader
	 * @param s The ShaderID of the Shader
	 */
	public static void bindShader(int s) {
		if(shader!=s)
			GL45.glUseProgram(s);
	}
	
	/**
	 * Unbinds the currently bound Shader
	 */
	public static void unbindShader() {
		if(shader!=0)
			GL45.glUseProgram(0);
	}
	
	/**
	 * Binds an Vertex Array
	 * @param vvao The Vertex Array that should be bound
	 */
	public static void bindVArray(int vvao) {
		if(vao!=vvao) {
			GL45.glBindVertexArray(vvao);
		}
		vao=vvao;
	}
	
	/**
	 * Unbinds the currently bound VertexArray
	 */
	public static void unbindVArray() {
		if(vao!=0)
			GL45.glBindVertexArray(0);
		vao=0;
	}
	
	public static void bindVBuf(int TYPE,int vvb) {
		if(vbo.containsKey(TYPE)) {
			if(!vbo.get(TYPE))
				glBindBuffer(TYPE, vvb);
		}else {
			states.put(TYPE, true);
			glBindBuffer(TYPE, vvb);
		}
	}
	
	public static void unbindVBuf(int TYPE) {
		if(vbo.containsKey(TYPE)&&vbo.get(TYPE))
			glBindBuffer(TYPE, 0);
		states.put(TYPE, false);
	}
	static int fbuf=0,ftype=0;
	public static boolean bindFramebuffer(int type,int buf) {
		if(fbuf!=buf) {
			fbuf=buf;
			GL45.glBindFramebuffer(type, buf);
			return true;
		}
		return false;
	}
	
	public static boolean unbindFramebuffer() {
		return bindFramebuffer(GL45.GL_FRAMEBUFFER,0);
	}
}

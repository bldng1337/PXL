package me.pxl.Backend.GL45;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.ARBDebugOutput.glDebugMessageControlARB;
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL14C.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14C.glBlendEquation;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.opengl.KHRDebug;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import me.pxl.Backend.GL45.Buffer.NGLByteBuffer;
import me.pxl.Backend.GL45.Buffer.NGLIndexBuffer;
import me.pxl.Backend.Generic.FrameBuffer;
import me.pxl.Backend.Generic.RenderAPI;
import me.pxl.Backend.Generic.RenderPass;
import me.pxl.Backend.Generic.Shader;
import me.pxl.Backend.Generic.Texture;
import me.pxl.Backend.Generic.VertexArray;
import me.pxl.Backend.Generic.Buffer.RByteBuffer;
import me.pxl.Backend.Generic.Buffer.RIndexBuffer;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.Attrib;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.Usage;
import me.pxl.Backend.Generic.Shader.Type;
import me.pxl.Backend.Generic.Texture.Filter;
import me.pxl.Backend.Generic.Texture.Textureformat;

public class NGLRenderAPI extends RenderAPI{
	//TODO: Implement a destroy for cleanup
	private long win=-1;
	private FrameBuffer screen;
	Callback debugProc;
	
	@Override
	public long init(String Title) {
		GLFWErrorCallback.createPrint().set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize glfw");
        }
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        
        win = glfwCreateWindow(640, 640, Title, NULL, NULL);
        if (win == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        
        glfwMakeContextCurrent(win);
        GLCapabilities caps      = GL.createCapabilities();
        debugProc = GLUtil.setupDebugMessageCallback();
        screen=new NGLFrameBuffer();
        if (caps.OpenGL43) {
            GL43.glDebugMessageControl(GL43.GL_DEBUG_SOURCE_API, GL43.GL_DEBUG_TYPE_OTHER, GL43.GL_DEBUG_SEVERITY_NOTIFICATION, (IntBuffer)null, true);
        } else if (caps.GL_KHR_debug) {
            KHRDebug.glDebugMessageControl(
            		GL45.GL_DONT_CARE,
            		GL45.GL_DONT_CARE,
            		GL45.GL_DONT_CARE,
                (IntBuffer)null,
                true
            );
        } else if (caps.GL_ARB_debug_output) {
            glDebugMessageControlARB(GL45.GL_DONT_CARE , GL45.GL_DONT_CARE , GL45.GL_DONT_CARE , (IntBuffer)null, true);
        }
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disable(GL45.GL_CULL_FACE,GL45.GL_DEPTH_TEST);
        begin();
        end();
        GLFW.glfwShowWindow(win);
        return win;
	}
	
	public void enableDepth() {
		GL45.glEnable(GL45.GL_DEPTH_TEST);
		GL45.glDepthFunc(GL45.GL_LESS);
		
	}
	
	public void disableDepth() {
		GL45.glDisable(GL45.GL_DEPTH_TEST);
	}
	
	@Override
	public void begin() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            GLFW.glfwGetWindowSize(win, w, h);
            width = w.get(0);
            height = h.get(0);

            GLFW.glfwGetFramebufferSize(win, w, h);
            frame_width = w.get(0);
            frame_height = h.get(0);
            
            screen.setWidth(frame_width);
            screen.setHeight(frame_height);
            GL45.glScissor(0, 0, frame_width, frame_height);
            GlStateManager.disable(GL45.GL_SCISSOR_TEST);
            GL45.glViewport(0, 0, frame_width, frame_height);
            GL45.glClearColor(0.5f,0.5f, 0.5f, 0.0f);
            GL45.glClear(GL45.GL_COLOR_BUFFER_BIT | GL45.GL_DEPTH_BUFFER_BIT);  
        }
	}

	@Override
	public void submit(RenderPass rp) {
		NGLRenderPass nrp=(NGLRenderPass) rp;
		if(!nrp.getFb().Screen) {
			GlStateManager.bindFramebuffer(GL45.GL_FRAMEBUFFER, nrp.getFb().buf);
//				this.updateViewport(0, 0, nrp.getFb().width, nrp.getFb().height);
		}else {
			GlStateManager.unbindFramebuffer();
//				this.updateViewport(0, 0, nrp.getFb().width, nrp.getFb().height);
		}
		GlStateManager.setViewport(0,0,nrp.getFb().getWidth(),nrp.getFb().getHeight());
        GlStateManager.bindShader(nrp.getShader().prog);
        if(nrp.getScissor()!=null) {
        	GlStateManager.enable(GL45.GL_SCISSOR_TEST);
        	GL45.glScissor((int)nrp.getScissor().x, (int)nrp.getScissor().y, (int)nrp.getScissor().z, (int)nrp.getScissor().w);
        }else {
        	GlStateManager.disable(GL45.GL_SCISSOR_TEST);
        }
        
        nrp.getVArr().bind();
        if(nrp.getShader()!=null)
        	nrp.getShader().setupUniform();
		int dtype=-1;
		switch(nrp.getVArr().getibuf().getDtype()) {
		case UINT:
			dtype=GL45.GL_UNSIGNED_INT;
			break;
		case USHORT:
			dtype=GL45.GL_UNSIGNED_SHORT;
			break;
		}
		if(nrp.getVertexoffset()!=-1) {
			GL45.glDrawElementsBaseVertex(GL45.GL_TRIANGLES, nrp.getCount(), dtype, nrp.getOffset(), nrp.getVertexoffset());
		}else {
			GL45.glDrawElements(GL45.GL_TRIANGLES,nrp.getCount(),dtype,nrp.getOffset());
		}
//		nrp.getVArr().unbind();
	}

	@Override
	public void end() {
		GLFW.glfwSwapInterval(0);
		GLFW.glfwSwapBuffers(win);
	}

	@Override
	public FrameBuffer getScreen() {
		return screen;
	}

	@Override
	public FrameBuffer getfb(int width, int height, Textureformat depth, Textureformat... attachments) {
		return new NGLFrameBuffer(width, height, depth, attachments);
	}

	@Override
	public RenderPass getRp(Shader s, VertexArray v, FrameBuffer fb) {
		return new NGLRenderPass(s, v, fb);
	}

	@Override
	public RIndexBuffer getibuf(Usage data, int elements) {
		return new NGLIndexBuffer(data, elements);
	}

	@Override
	public RByteBuffer getBbuf(Usage data, int elements, Attrib... elemsize) {
		return new NGLByteBuffer(data, elements, elemsize);
	}

	@Override
	public Shader getShader(Type t, String src) {
		return new NGLShader(t, src);
	}

	@Override
	public Texture getTex(Textureformat f, int width, int height) {
		return new NGLTexture(f, width, height);
	}

	@Override
	public VertexArray getVArr(RIndexBuffer indexbuffer, RByteBuffer... buf) {
		NGLByteBuffer[] nbuf=new NGLByteBuffer[buf.length];
		for(int i=0;i<buf.length;i++)
			nbuf[i]=(NGLByteBuffer) buf[i];
		return new NGLVertexArray((NGLIndexBuffer)indexbuffer, nbuf);
	}

	@Override
	public void updateViewport(int x, int y, int width, int height) {
		GlStateManager.setViewport(x,y,width,height);
	}

	@Override
	public void submit(Shader sh,int sx,int sy,int sz) {
		NGLShader ngls=(NGLShader) sh;
		GL45.glUseProgram(ngls.getID());
//		GlStateManager.bindShader(sh.getID());
		ngls.setupUniform();
		GL45.glDispatchCompute(sx, sy, sz);
		GL45.glMemoryBarrier(GL45.GL_TEXTURE_FETCH_BARRIER_BIT|GL45.GL_TEXTURE_UPDATE_BARRIER_BIT);
		//GL_ALL_BARRIER_BITS
	}

	@Override
	public Texture getTex(Textureformat f, int width, int height, Filter linear) {
		return new NGLTexture(f, width, height,linear);
	}
	

}

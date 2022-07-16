package me.pxl;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import me.pxl.Asset.AssetManager;
import me.pxl.Asset.DAssetManager;
import me.pxl.Backend.GL45.NGLRenderAPI;
import me.pxl.Backend.Generic.FrameBuffer;
import me.pxl.Backend.Generic.RenderAPI;
import me.pxl.Backend.Generic.Texture;
import me.pxl.Backend.Generic.Texture.Textureformat;
import me.pxl.ECS.EntityManager;
import me.pxl.ECS.Systems.SPhysics;
import me.pxl.ECS.Systems.SRenderer;
import me.pxl.ECS.Systems.STilemap;
import me.pxl.ECS.Systems.SUpdate;
import me.pxl.Event.EventManager;
import me.pxl.Event.Events.EChar;
import me.pxl.Event.Events.EInput;
import me.pxl.Event.Events.EKey;
import me.pxl.Event.Events.EMouseButton;
import me.pxl.Event.Events.EMousePos;
import me.pxl.Event.Events.EPostRender;
import me.pxl.Event.Events.ERender;
import me.pxl.Event.Events.EResize;
import me.pxl.Event.Events.EScroll;
import me.pxl.Log.MultipleTimer;
import me.pxl.Log.Timer;
import me.pxl.Post.PostProcessor;

public class Engine {
	
	private PostProcessor pst;
	private static Engine eng;
	public EntityManager em;
	protected RenderAPI rapi;
	private Texture finalImage;
	private FrameBuffer finalBuffer;
	boolean editor;
	long win;
	AssetManager am;
	
	@SuppressWarnings("unchecked")
	public <T extends me.pxl.ECS.Systems.System> T getSystem(Class<T> cs) {
		for(me.pxl.ECS.Systems.System s:l)
			if(s.getClass().equals(cs))
				return (T)s;
		return null;
	}
	
	public List<me.pxl.ECS.Systems.System> getSystems() {
		return l;
	}

	List<me.pxl.ECS.Systems.System> l;
	
	private Engine() {
		eng=this;
		init();
	}
	
	protected void init() {
		Timer t=new Timer();
		rapi=new NGLRenderAPI();//TODO Choose RenderAPI
		win=rapi.init("PXL - Engine");
		finalBuffer=rapi.getScreen();
		am=new DAssetManager(win, Paths.get("../demo/"), rapi);//TODO: Choose AssetManager
		finalBuffer = getRenderAPI().getfb(rapi.getFrame_width(), rapi.getFrame_height(),
				Textureformat.DEPTH24STENCIL8, Textureformat.RGBA16F,Textureformat.RGBA8);
		finalImage=rapi.getTex(Textureformat.RGBA8, rapi.getFrame_width(), rapi.getFrame_height());
		em=new EntityManager();
		l=new ArrayList<>();
		pst=new PostProcessor();
		addSystem(new SRenderer());
		addSystem(new STilemap());
		addSystem(new SUpdate());
		addSystem(new SPhysics());
		GLFW.glfwSetScrollCallback(win, (window, xoffset, yoffset)->EventManager.call(new EScroll(xoffset, yoffset)));
		GLFW.glfwSetCharCallback(win, (window, codepoint)->EventManager.call(new EChar(codepoint)));
		GLFW.glfwSetMouseButtonCallback(win, (window, button, action, mods)->EventManager.call(new EMouseButton(button, action, mods)));
		GLFW.glfwSetCursorPosCallback(win, (window,x,y)->EventManager.call(new EMousePos(x,y)));
		GLFW.glfwSetWindowSizeCallback(win, (window,x,y)->EventManager.call(new EResize(x,y)));
		GLFW.glfwSetKeyCallback(win, (window,key,scancode,action,mods)->{
			String s=GLFW.glfwGetKeyName(key, scancode);
			if(s==null||s.isEmpty())
				s="K"+key+"S"+scancode;
			EventManager.call(new EKey(s,action,mods));
			});
		//Init Event
		System.out.println("Startup took "+t.getmillis()+"ms");
	}
	public void addSystem(me.pxl.ECS.Systems.System e) {
		l.add(e);
		EventManager.register(e);
		e.init(eng);
	}
	public void addSystem(me.pxl.ECS.Systems.System e,int i) {
		l.add(i,e);
		EventManager.register(e);
		e.init(eng);
	}
	
	public void update() {
		while (!GLFW.glfwWindowShouldClose(win)) {
			rapi.begin();
			getFinalBuffer().clear();
//			l.get(0).update(eng);
			l.forEach(s->{
				if(s.editor()||editor)
					s.update(eng);
			});
			EventManager.call(new ERender());
			pst.post(this);
			
			rapi.end();
			EventManager.call(new EPostRender());
			EventManager.call(new EInput(true));
			GLFW.glfwPollEvents();
			EventManager.call(new EInput(false));
			
		}
	}
	
	public void update(MultipleTimer t) {
		while (!GLFW.glfwWindowShouldClose(win)) {
			t.reset();
			t.begin("iFrame");
			t.begin("Init");
			rapi.begin();
			getFinalBuffer().clear();
			t.end("Init");
			l.forEach(s->{
				if(s.editor()||editor) {
					t.begin(s.toString());
					s.update(eng);
					t.end(s.toString());
				}
			});
			EventManager.call(new ERender(),t);
			t.begin("Post");
			pst.post(this);
			t.end("Post");
			t.begin("EndFrame");
			rapi.end();
			t.end("EndFrame");
			EventManager.call(new EPostRender(),t);
			EventManager.call(new EInput(true),t);
			t.begin("Input");
			GLFW.glfwPollEvents();
			t.end("Input");
			EventManager.call(new EInput(false),t);
			t.end("iFrame");
		}
	}
	
	public void destroy() {
		am.destroy();
		eng=null;
	}
	
	public boolean isEditor() {
		return editor;
	}

	public void setEditor(boolean editor) {
		if(editor) {
			for(me.pxl.ECS.Systems.System s:this.l) {
				s.beginPlay(this);
			}
		}
		this.editor = editor;
	}

	public static Engine getEngine() {
		if(eng==null)
			return eng=new Engine();
		return eng;
	}
	
	public RenderAPI getRenderAPI() {
		return rapi;
	}
	
	public long getWin() {
		return win;
	}

	public AssetManager getAssetManager() {
		return am;
	}
	
	public FrameBuffer getFinalBuffer() {
		return finalBuffer;
	}

	public void setFinalBuffer(FrameBuffer finalBuffer) {
		this.finalBuffer = finalBuffer;
	}

	public Texture getFinalImage() {
		return finalImage;
	}
	
	public void setBufferSize(float x,float y) {
		finalBuffer.setSize(x,y);
		finalImage.destroy();
		finalImage=rapi.getTex(Textureformat.RGBA8, (int)x, (int)y);
	}
	public void setBufferSize(Vector2f v) {
		setBufferSize(v.x, v.y);
	}
	public PostProcessor getPostProcessor() {
		return pst;
	}
}

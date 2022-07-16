package me.pxl.editor.EntityInteraction;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import imgui.ImGui;
import imgui.flag.ImGuiMouseCursor;
import imgui.type.ImInt;
import me.pxl.Engine;
import me.pxl.Asset.AssetManager;
import me.pxl.Asset.Assets.AScene;
import me.pxl.Backend.Generic.RenderAPI;
import me.pxl.Backend.Generic.RenderPass;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.Attrib;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.DType;
import me.pxl.Backend.Generic.Buffer.RByteBuffer.Usage;
import me.pxl.Backend.Generic.Buffer.RIndexBuffer.Save;
import me.pxl.Backend.Generic.Shader.Type;
import me.pxl.ECS.Entity;
import me.pxl.ECS.Components.CRender;
import me.pxl.Event.EventManager;
import me.pxl.Event.EventTarget;
import me.pxl.Event.Events.EKey;
import me.pxl.Event.Events.ERender;
import me.pxl.Event.Events.EKey.Action;
import me.pxl.Utils.IOUtils;
import me.pxl.editor.EditorMain;

public class StandartDriver extends EntityInteractionDriver {

	private Vector2f start;
	RenderPass rp;
	public StandartDriver() {
		Engine e=Engine.getEngine();
		RenderAPI rapi=e.getRenderAPI();
		start=new Vector2f();
		EventManager.register(this);
		rp=rapi.getRp(rapi.getShader(Type.VERTEXFRAGMENT, IOUtils.stream2string(IOUtils.getinfromclasspath(StandartDriver.class, "select.glsl"))),
				rapi.getVArr(rapi.getibuf(Usage.STATIC, 6), rapi.getBbuf(Usage.STREAM, 4, new Attrib(DType.FLOAT,3)),rapi.getBbuf(Usage.STREAM, 4, new Attrib(DType.FLOAT,2))),
				e.getFinalBuffer());
		try(Save<IntBuffer> s=rp.getV().getibuf().load()){
			s.getBuf().put(new int[] {0,1,2,1,3,2});
		}
		rp.setCount(6);
	}
	boolean dragging=false;
	int dragent=0;
	String copy="";
	boolean isgrid=false;
	int grid=0;
	@Override
	public void onContextMenu(float mx, float my) {
		Engine eng=Engine.getEngine();
		if(ImGui.checkbox("Grid",isgrid))
			isgrid=!isgrid;
		if(ImGui.beginPopupContextItem()) {
			int[] sl=new int[] {grid};
			if(ImGui.sliderInt("GridSize", sl, 0, 300))
				grid=sl[0];
			
			if(ImGui.beginPopupContextItem()) {
				ImInt i=new ImInt(grid);
				ImGui.inputInt("GridSize", i);
				grid=i.get();
				ImGui.endPopup();
			}
			ImGui.endPopup();
		}
		if(EditorMain.selected!=null&&EditorMain.selected instanceof Entity) {
			Entity sel=(Entity) EditorMain.selected;
			if (ImGui.button("Copy Entity"))
				try {
					copy=eng.em.saveEntity(sel);
				} catch (IOException e) {
					copy="";
				}
			if (ImGui.button("Delete Entity")) {
				eng.em.despawnEntity(sel);
				EditorMain.selected=null;
			}
		}
		if(!copy.isEmpty())
			if (ImGui.button("Paste Entity"))
				try {
					Entity ent=eng.em.loadEntity(copy);
					EditorMain.selected=ent;
					ent.setPos(ent.getPos().set(mx-eng.em.getTranslation().x, my-eng.em.getTranslation().y, ent.pos.z));
				} catch (IOException e) {
					e.printStackTrace();
				}
		if (ImGui.button("Reset Transform"))
			eng.em.translate(0, 0);
		super.onContextMenu(mx, my);
	}
	boolean strg=false;
	public final String strgk="K341S29";
	public final String entfk="K261S339";
	@EventTarget
	public void onKey(EKey k) {
		Engine eng=Engine.getEngine();
//		System.out.println(k.getKey());
		if(k.getAction().equals(Action.PRESS)) {
			if(k.getKey().equals(strgk))
				strg=true;
		}
		if(!k.getAction().equals(Action.RELEASE))
			return;
		switch(k.getKey()) {
		case strgk:
			strg=false;
			break;
		case entfk:
			if(EditorMain.selected!=null&&EditorMain.selected instanceof Entity) {
				eng.em.despawnEntity((Entity) EditorMain.selected);
				EditorMain.selected=null;
			}
			break;
		case "c":
		case "C":
			if(EditorMain.selected!=null&&EditorMain.selected instanceof Entity)
				try {
					copy=eng.em.saveEntity((Entity) EditorMain.selected);
				} catch (IOException e) {
					copy="";
				}
			break;
		case "v":
		case "V":
			if(!copy.isEmpty())
			try {
				Entity ent=eng.em.loadEntity(copy);
				ent.setPos(ent.getPos().add(ent.size.x,ent.size.y,0).add(5f,5f,0f));
				EditorMain.selected=ent;
				copy=eng.em.saveEntity(ent);
			} catch (IOException e) {
				e.printStackTrace();
				copy="";
			}
			break;
		}
	}
	
	@Override
	public void onDown(float mx, float my, int button) {
		Engine e=Engine.getEngine();
		
		if(dragent==0&&EditorMain.selected!=null&&(EditorMain.selected instanceof Entity)&&!strg) {
			Entity se=(Entity) EditorMain.selected;
			if(isHovering(se.getPos().x+e.em.getTranslation().x+se.getSize().x-15,se.getPos().y+e.em.getTranslation().y-15,30,30,mx,my)) {
				dragent=9;
				dragging=true;
				start.set(0);
			}else if(isHovering(se.getPos().x+e.em.getTranslation().x-15,se.getPos().y+se.getSize().y+e.em.getTranslation().y-15,30,30,mx,my)) {
				dragent=8;
				dragging=true;
				start.set(0);
			}else if(isHovering(se.getPos().x+e.em.getTranslation().x-15,se.getPos().y+e.em.getTranslation().y-15,30,30,mx,my)) {
				dragent=7;
				dragging=true;
				start.set(0);
			}else if(isHovering(se.getPos().x+e.em.getTranslation().x+se.getSize().x-15,se.getPos().y+e.em.getTranslation().y+se.getSize().y-15,30,30,mx,my)) {
				dragent=4;
				dragging=true;
				start.set(se.getSize());
			}else if(isHovering(se.getPos().x+e.em.getTranslation().x+se.getSize().x-15,se.getPos().y+e.em.getTranslation().y,30,se.getSize().y,mx,my)) {
				dragent=2;
				dragging=true;
				start.set(se.getSize());
			}else if(isHovering(se.getPos().x+e.em.getTranslation().x,se.getPos().y+e.em.getTranslation().y+se.getSize().y-15,se.getSize().x,30,mx,my)) {
				dragent=3;
				dragging=true;
				start.set(se.getSize());
			}else if(isHovering(se.getPos().x+e.em.getTranslation().x-15,se.getPos().y+e.em.getTranslation().y,30,se.getSize().y,mx,my)) {
				dragent=5;
				dragging=true;
				start.set(0);
			}else if(isHovering(se.getPos().x+e.em.getTranslation().x,se.getPos().y+e.em.getTranslation().y-15,se.getSize().x,30,mx,my)) {
				dragent=6;
				dragging=true;
				start.set(0);
			}else if(isHovering(se.getPos().x+e.em.getTranslation().x,se.getPos().y+e.em.getTranslation().y,se.getSize().x,se.getSize().y,mx,my)) {//Hovering Main
				start.set(se.getPos().x, se.getPos().y);
				dragging=true;
				dragent=1;
			}
		}
		if(ImGui.isMouseDragging(button)) {
			if(dragent>0) {
				Entity ent=(Entity) EditorMain.selected;
				switch(dragent) {
				case 1:
					ent.getPos().set(start.x+ImGui.getMouseDragDelta().x, start.y+ImGui.getMouseDragDelta().y,ent.getPos().z);
					break;
				case 2:
					ent.getSize().set(start.x+ImGui.getMouseDragDelta().x, start.y);
					break;
				case 3:
					ent.getSize().set(start.x, start.y+ImGui.getMouseDragDelta().y);
					break;
				case 5:
					ent.getPos().add((ImGui.getMouseDragDelta().x-start.x), 0, 0);
					ent.getSize().add(-(ImGui.getMouseDragDelta().x-start.x), 0);
					if(ent.getSize().x<0)
						ent.getSize().set(0, ent.getSize().y);
					if(ent.getSize().y<0)
						ent.getSize().set(ent.getSize().x, 0);
					start.set(ImGui.getMouseDragDelta().x, ImGui.getMouseDragDelta().y);
					break;
				case 6:
					ent.getPos().add(0, (ImGui.getMouseDragDelta().y-start.y), 0);
					ent.getSize().add(0, -(ImGui.getMouseDragDelta().y-start.y));
					if(ent.getSize().x<0)
						ent.getSize().set(0, ent.getSize().y);
					if(ent.getSize().y<0)
						ent.getSize().set(ent.getSize().x, 0);
					start.set(ImGui.getMouseDragDelta().x, ImGui.getMouseDragDelta().y);
					break;
				case 7:
					ent.getPos().add((ImGui.getMouseDragDelta().x-start.x), (ImGui.getMouseDragDelta().y-start.y), 0);
					ent.getSize().add(-(ImGui.getMouseDragDelta().x-start.x), -(ImGui.getMouseDragDelta().y-start.y));
					if(ent.getSize().x<0)
						ent.getSize().set(0, ent.getSize().y);
					if(ent.getSize().y<0)
						ent.getSize().set(ent.getSize().x, 0);
					start.set(ImGui.getMouseDragDelta().x, ImGui.getMouseDragDelta().y);
					break;
				case 8:
					ent.getPos().add((ImGui.getMouseDragDelta().x-start.x), 0, 0);
					ent.getSize().add(-(ImGui.getMouseDragDelta().x-start.x), (ImGui.getMouseDragDelta().y-start.y));
					if(ent.getSize().x<0)
						ent.getSize().set(0, ent.getSize().y);
					if(ent.getSize().y<0)
						ent.getSize().set(ent.getSize().x, 0);
					start.set(ImGui.getMouseDragDelta().x, ImGui.getMouseDragDelta().y);
					break;
				case 9:
					ent.getPos().add(0, (ImGui.getMouseDragDelta().y-start.y), 0);
					ent.getSize().add((ImGui.getMouseDragDelta().x-start.x), -(ImGui.getMouseDragDelta().y-start.y));
					if(ent.getSize().x<0)
						ent.getSize().set(0, ent.getSize().y);
					if(ent.getSize().y<0)
						ent.getSize().set(ent.getSize().x, 0);
					start.set(ImGui.getMouseDragDelta().x, ImGui.getMouseDragDelta().y);
					break;
				case 4:
					ent.getSize().set(start.x+ImGui.getMouseDragDelta().x, start.y+ImGui.getMouseDragDelta().y);
					break;
				}
				if(isgrid) {
					ent.getPos().set(
							Math.round(ent.getPos().x/grid)*grid,
							Math.round(ent.getPos().y/grid)*grid,
							ent.getPos().z);
					ent.getSize().set(
							Math.round(ent.getSize().x/grid)*grid,
							Math.round(ent.getSize().y/grid)*grid);
				}
			}else {
				e.em.translate(start.x+ImGui.getMouseDragDelta().x, start.y+ImGui.getMouseDragDelta().y);
				dragging=true;
			}
		}
	}
	
	@Override
	public void onUpdate(float mx, float my) {
		if(EditorMain.selected==null||!(EditorMain.selected instanceof Entity))return;
		Engine e=Engine.getEngine();
		Entity se=(Entity) EditorMain.selected;
		if(!strg)
		if(isHovering(se.getPos().x+e.em.getTranslation().x+se.getSize().x-15,se.getPos().y+e.em.getTranslation().y-15,30,30,mx,my)) {
			ImGui.setMouseCursor(ImGuiMouseCursor.ResizeNESW);
		}else if(isHovering(se.getPos().x+e.em.getTranslation().x-15,se.getPos().y+se.getSize().y+e.em.getTranslation().y-15,30,30,mx,my)) {
			ImGui.setMouseCursor(ImGuiMouseCursor.ResizeNESW);
		}else if(isHovering(se.getPos().x+e.em.getTranslation().x-15,se.getPos().y+e.em.getTranslation().y-15,30,30,mx,my)) {
			ImGui.setMouseCursor(ImGuiMouseCursor.ResizeNWSE);
		}else if(isHovering(se.getPos().x+e.em.getTranslation().x+se.getSize().x-15,se.getPos().y+e.em.getTranslation().y+se.getSize().y-15,30,30,mx,my)) {
			ImGui.setMouseCursor(ImGuiMouseCursor.ResizeNWSE);
		}else if(isHovering(se.getPos().x+e.em.getTranslation().x+se.getSize().x-15,se.getPos().y+e.em.getTranslation().y,30,se.getSize().y,mx,my)) {
			ImGui.setMouseCursor(ImGuiMouseCursor.ResizeEW);
		}else if(isHovering(se.getPos().x+e.em.getTranslation().x-15,se.getPos().y+e.em.getTranslation().y,30,se.getSize().y,mx,my)) {
			ImGui.setMouseCursor(ImGuiMouseCursor.ResizeEW);
		}else if(isHovering(se.getPos().x+e.em.getTranslation().x,se.getPos().y+e.em.getTranslation().y-15,se.getSize().x,30,mx,my)) {
			ImGui.setMouseCursor(ImGuiMouseCursor.ResizeNS);
		}else if(isHovering(se.getPos().x+e.em.getTranslation().x,se.getPos().y+e.em.getTranslation().y+se.getSize().y-15,se.getSize().x,30,mx,my)) {
			ImGui.setMouseCursor(ImGuiMouseCursor.ResizeNS);
		}else if(isHovering(se.getPos().x+e.em.getTranslation().x,se.getPos().y+e.em.getTranslation().y,se.getSize().x,se.getSize().y,mx,my)) {//Hovering Main
			ImGui.setMouseCursor(ImGuiMouseCursor.ResizeAll);
		}
	}
	
	@EventTarget
	public void onRender(ERender re) {
//		ImGui.setMouseCursor(ImGuiMouseCursor.ResizeEW);
		if(EditorMain.selected==null||!(EditorMain.selected instanceof Entity))return;
		Engine e=Engine.getEngine();
		Entity se=(Entity) EditorMain.selected;
		
		rp.setFb(e.getFinalBuffer());
		Matrix4f m=new Matrix4f();
		m.identity();
		m.ortho2D(0, e.getFinalBuffer().getWidth(), 0, e.getFinalBuffer().getHeight());
		Matrix4f n=new Matrix4f();
		n.identity();
		Vector3f v=e.em.getTranslation();
		n.translate(v);
		m.mul(n);
		rp.getS().setVal("proj", m);
		Vector3f temp=new Vector3f();
		try(Save<ByteBuffer> s=rp.getV().getbuf(1).load()){
			s.getBuf().asFloatBuffer().put(new float[] {0,0, 0,se.getSize().y, se.getSize().x,0, se.getSize().x,se.getSize().y});
		}
		rp.getS().setVal("size", se.getSize());
		try(Save<ByteBuffer> b=rp.getV().getbuf(0).load()){
			temp.set(se.getPos());
			se.getPos().get(b.getBuf());
			b.getBuf().position(b.getBuf().position()+3*4);
			//0 1
			se.getPos().add(0, se.getSize().y, 0, temp).get(b.getBuf());
			b.getBuf().position(b.getBuf().position()+3*4);
			//1 0
			se.getPos().add(se.getSize().x, 0, 0, temp).get(b.getBuf());
			b.getBuf().position(b.getBuf().position()+3*4);
			//1 1
			se.getPos().add(se.getSize().x, se.getSize().y, 0, temp).get(b.getBuf());
			b.getBuf().position(b.getBuf().position()+3*4);
		}
		e.getRenderAPI().submit(rp);
	}
	
	@Override
	public Object onRelease(float mx, float my, int mb) {
		Engine e=Engine.getEngine();
		if(!dragging) {
			for(Entity ent:e.em.getEntities()) {
				if(mx>ent.getPos().x+e.em.getTranslation().x&&mx<ent.getPos().x+ent.getSize().x+e.em.getTranslation().x&&
						my>ent.getPos().y+e.em.getTranslation().y&&my<ent.getPos().y+ent.getSize().y+e.em.getTranslation().y) {
					return ent;
				}
			}
		}
		dragging=false;
		dragent=0;
		return null;
	}
	
	@Override
	public void onClick(float x,float y,int button) {
		Engine e=Engine.getEngine();
		if(button==0)
			start=new Vector2f(e.em.getTranslation().x,e.em.getTranslation().y);
		
	}

	@Override
	public String[] shouldDrop() {
		return new String[] {};
	}

	@Override
	public void onDrop(String type, Object o, float x, float y) {}

}

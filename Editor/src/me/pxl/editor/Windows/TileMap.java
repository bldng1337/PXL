package me.pxl.editor.Windows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import me.pxl.Engine;
import me.pxl.Asset.Assets.ATexture;
import me.pxl.ECS.CAsset;
import me.pxl.Serialize.Serialize;
import me.pxl.Serialize.SerializeationAdapter;
import me.pxl.Serialize.JSON.JSONDeserializeAdapter;
import me.pxl.Serialize.JSON.JSONSerializeAdapter;
import me.pxl.Utils.IOUtils;
import me.pxl.editor.FileExplorer.FileWindow;

public class TileMap extends FileWindow {
	public static class SubTextures{
		public SubTextures(ATexture txt) {
			this();
			this.txt=txt;
		}
		public SubTextures() {
			tiles=new ArrayList<>();
		}
		public boolean grid=false;
		public int offset=0,size=10;
		List<Tile> tiles;
		@CAsset(Name="Texture")
		public ATexture txt;
	}
	public static class Tile{
		public Tile(Vector2f pos,Vector2f size) {
			this();
			this.pos=pos;
			this.size=size;
		}
		public Tile() {}
		public Vector2f pos,size;
	}
	List<SubTextures> subtxt=new ArrayList<>();
	
	public TileMap(Path p) {
		super(p);
		try {
			String str=IOUtils.stringfromFile(p);
			if(str.isEmpty())
				return;
			JSONDeserializeAdapter s=new JSONDeserializeAdapter(str);
			deserialize(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void init() {
		flags=ImGuiWindowFlags.MenuBar|ImGuiWindowFlags.NoDocking;
	}
	SubTextures curr;
	Tile currt;
	@Override
	public void update() {
		if(ImGui.beginMenuBar()) {
			if(ImGui.beginMenu("File")) {
				ImGui.menuItem("Add Image");
				ImGui.endMenu();
			}
			
			ImGui.endMenuBar();
		}
		if(ImGui.beginChild("Texures",100,ImGui.getContentRegionAvailY(),true)) {
			SubTextures rem=null;
			for(SubTextures s:subtxt) {
				if(ImGui.imageButton(s.txt.getT().getID(), 100, 100)) {
					curr=s;
					currt=null;
				}
				
				if(ImGui.beginPopupContextItem()) {
					if(ImGui.button("Remove")) {
						rem=s;
					}
					ImGui.endPopup();
				}
			}
			if(rem!=null) {
				subtxt.remove(rem);
				Engine.getEngine().getAssetManager().returnRef(rem.txt);
				if(rem.equals(curr)) {
					curr=null;
					currt=null;
				}
			}
		}
		ImGui.endChild();
		Engine e=Engine.getEngine();
		if (ImGui.beginDragDropTarget()) {
			Path payload = ImGui.acceptDragDropPayload("FE_ATexture");
			if (payload != null) {
				subtxt.add(new SubTextures(e.getAssetManager().getRef(e.getAssetManager().registerAsset(payload))));
			}
		}
		ImGui.sameLine();
		if(ImGui.beginChild("Tilemap",ImGui.getContentRegionAvailX(),ImGui.getContentRegionAvailY(),true)) {
			if(curr!=null) {
				if(ImGui.button("Add Tile")) {
					this.currt=new Tile(
							new Vector2f((float) (Math.random()*curr.txt.getT().getWidth()-30),
									(float) (Math.random()*curr.txt.getT().getHeight()-30)),new Vector2f(30,30));
					curr.tiles.add(currt);
				}
				if(ImGui.beginPopupContextItem()) {
					if(curr.grid)
						if(ImGui.button("Fill Grid with Tiles")) {
							for(float x=0;x<curr.txt.getT().getWidth();x+=curr.size) {
								for(float y=0;y<curr.txt.getT().getHeight();y+=curr.size) {
									this.currt=new Tile(new Vector2f(x,y),new Vector2f(curr.size));
									curr.tiles.add(currt);
								}
							}
						}
					ImGui.endPopup();
				}
				ImGui.sameLine();
				if(ImGui.button("Remove Tile")) {
					if(this.currt!=null)
						curr.tiles.remove(this.currt);
				}
				if(ImGui.beginPopupContextItem()) {
					if(ImGui.button("Remove all Tiles")) {
						currt=null;
						curr.tiles.clear();
					}
					ImGui.endPopup();
				}
				ImGui.sameLine();
				if(ImGui.checkbox("Grid", curr.grid)) {
					curr.grid=!curr.grid;
				}
				if(ImGui.beginPopupContextItem("Size")) {
					int fl[]=new int[1];
					fl[0]=curr.offset;
					if(ImGui.sliderInt("Width", fl, 0, Math.max(curr.txt.getT().getWidth(), curr.txt.getT().getHeight())))
						curr.offset=fl[0];
					fl[0]=curr.size;
					if(ImGui.sliderInt("Size", fl, 10, Math.max(curr.txt.getT().getWidth(), curr.txt.getT().getHeight())))
						curr.size=fl[0];
					ImGui.endPopup();
				}
				ImGui.image(curr.txt.getT().getID(), ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY());
				if(curr.grid) {
					for(float x=0;x<curr.txt.getT().getWidth();x+=curr.size) {
						//
						ImGui.getWindowDrawList().addLine(
								ImGui.getItemRectMin().x+((x)/curr.txt.getT().getWidth())*ImGui.getItemRectSizeX(),
								ImGui.getItemRectMin().y,
								ImGui.getItemRectMin().x+((x)/curr.txt.getT().getWidth())*ImGui.getItemRectSizeX(),
								ImGui.getItemRectMax().y, 0xCCCCCCCC);
					}
					for(float y=0;y<curr.txt.getT().getHeight();y+=curr.size) {
						ImGui.getWindowDrawList().addLine(	
								ImGui.getItemRectMin().x,
								ImGui.getItemRectMin().y+((y)/curr.txt.getT().getHeight())*ImGui.getItemRectSizeY(),
								ImGui.getItemRectMax().x,
								ImGui.getItemRectMin().y+((y)/curr.txt.getT().getHeight())*ImGui.getItemRectSizeY(), 0xCCCCCCCC);
					}
				}
				if(ImGui.isItemHovered()&&ImGui.isMouseDragging(0)&&currt!=null) {
					currt.pos.set(
							ImGui.getMousePosX()-ImGui.getMouseDragDelta().x-ImGui.getItemRectMinX(),
							ImGui.getMousePosY()-ImGui.getMouseDragDelta().y-ImGui.getItemRectMinY());
					currt.size.set(ImGui.getMouseDragDelta().x, ImGui.getMouseDragDelta().y);
					currt.pos.div(ImGui.getItemRectSizeX(),ImGui.getItemRectSizeY()).mul(curr.txt.getT().getSize());
					currt.size.div(ImGui.getItemRectSizeX(),ImGui.getItemRectSizeY()).mul(curr.txt.getT().getSize());
					if(curr.grid) {
						currt.pos.set(Math.round(currt.pos.x/curr.size)*curr.size, Math.round(currt.pos.y/curr.size)*curr.size);
						currt.size.set(Math.round(currt.size.x/curr.size)*curr.size, Math.round(currt.size.y/curr.size)*curr.size);
					}
				}
				for(Tile t:curr.tiles) {
					if(ImGui.isMouseClicked(0)&&
							ImGui.isMouseHoveringRect(
								ImGui.getItemRectMin().x+(t.pos.x/curr.txt.getT().getWidth())*ImGui.getItemRectSizeX(),
								ImGui.getItemRectMin().y+(t.pos.y/curr.txt.getT().getHeight())*ImGui.getItemRectSizeY(),
								ImGui.getItemRectMin().x+((t.pos.x+t.size.x)/curr.txt.getT().getWidth())*ImGui.getItemRectSizeX(),
								ImGui.getItemRectMin().y+((t.pos.y+t.size.y)/curr.txt.getT().getHeight())*ImGui.getItemRectSizeY())) {
						currt=t;
					}
					if(t==currt) {
						ImGui.getWindowDrawList().addRectFilled(
							ImGui.getItemRectMin().x+(t.pos.x/curr.txt.getT().getWidth())*ImGui.getItemRectSizeX(),
							ImGui.getItemRectMin().y+(t.pos.y/curr.txt.getT().getHeight())*ImGui.getItemRectSizeY(),
							ImGui.getItemRectMin().x+((t.pos.x+t.size.x)/curr.txt.getT().getWidth())*ImGui.getItemRectSizeX(),
							ImGui.getItemRectMin().y+((t.pos.y+t.size.y)/curr.txt.getT().getHeight())*ImGui.getItemRectSizeY(), 0x88FFFFFF);
						ImGui.getWindowDrawList().addRect(
							ImGui.getItemRectMin().x+(t.pos.x/curr.txt.getT().getWidth())*ImGui.getItemRectSizeX(),
							ImGui.getItemRectMin().y+(t.pos.y/curr.txt.getT().getHeight())*ImGui.getItemRectSizeY(),
							ImGui.getItemRectMin().x+((t.pos.x+t.size.x)/curr.txt.getT().getWidth())*ImGui.getItemRectSizeX(),
							ImGui.getItemRectMin().y+((t.pos.y+t.size.y)/curr.txt.getT().getHeight())*ImGui.getItemRectSizeY(), 0xFFFFFFFF);
					}else {
						ImGui.getWindowDrawList().addRect(
								ImGui.getItemRectMin().x+(t.pos.x/curr.txt.getT().getWidth())*ImGui.getItemRectSizeX(),
								ImGui.getItemRectMin().y+(t.pos.y/curr.txt.getT().getHeight())*ImGui.getItemRectSizeY(),
								ImGui.getItemRectMin().x+((t.pos.x+t.size.x)/curr.txt.getT().getWidth())*ImGui.getItemRectSizeX(),
								ImGui.getItemRectMin().y+((t.pos.y+t.size.y)/curr.txt.getT().getHeight())*ImGui.getItemRectSizeY(), 0xFFFFFFFF);
						ImGui.getWindowDrawList().addRectFilled(
								ImGui.getItemRectMin().x+(t.pos.x/curr.txt.getT().getWidth())*ImGui.getItemRectSizeX(),
								ImGui.getItemRectMin().y+(t.pos.y/curr.txt.getT().getHeight())*ImGui.getItemRectSizeY(),
								ImGui.getItemRectMin().x+((t.pos.x+t.size.x)/curr.txt.getT().getWidth())*ImGui.getItemRectSizeX(),
								ImGui.getItemRectMin().y+((t.pos.y+t.size.y)/curr.txt.getT().getHeight())*ImGui.getItemRectSizeY(), 0x33FFFFFF);
					}
				}
			}
		}
		ImGui.endChild();
		
		
		
	}

	@Override
	public void destroy() {
		try {
			JSONSerializeAdapter s=new JSONSerializeAdapter();
			serialize(s);
			IOUtils.write(s.get(), p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void serialize(SerializeationAdapter s) throws IOException {
		s.begin();
		s.beginArray("Textures");
		for(SubTextures sub:this.subtxt) {
			Serialize.serializeclass(sub, s,() -> {
				s.beginArray("Tiles");
				for(Tile t:sub.tiles)
					Serialize.serializeclass(t, s);
				s.endArray("Tiles");
			});
		}
		s.endArray("Textures");
		s.exit();
	}
	private void deserialize(JSONDeserializeAdapter s) throws IOException {
		s.begin();
		s.nextName();
		s.beginArray("Textures");
		while(s.hasnext()) {
			this.subtxt.add(Serialize.deserializeclass(SubTextures.class, s,(that,name)->{
				if(name.equals("Tiles")) {
					s.beginArray("Tiles");
					while(s.hasnext())
						that.tiles.add(Serialize.deserializeclass(Tile.class, s));
					s.endArray("Tiles");
					return true;
				}
				return false;
			}));
		}
		s.endArray("Textures");
		s.exit();
	}

}
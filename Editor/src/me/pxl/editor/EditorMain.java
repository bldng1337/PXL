package me.pxl.editor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.joml.Vector3f;

import ImGui.ImUI;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.implot.ImPlot;
import me.pxl.Engine;
import me.pxl.Asset.Asset;
import me.pxl.Asset.AssetManager;
import me.pxl.ECS.Component;
import me.pxl.ECS.Entity;
import me.pxl.ECS.Components.CLight;
import me.pxl.ECS.Components.CRender;
import me.pxl.ECS.Components.CRigidBody;
import me.pxl.ECS.Components.CTilemap;
import me.pxl.ECS.Systems.SRenderer;
import me.pxl.Event.EventManager;
import me.pxl.Event.EventTarget;
import me.pxl.Event.Events.EMouseButton;
import me.pxl.Log.MultipleTimer;
import me.pxl.Log.Timer;
import me.pxl.Post.post.Lightning.Lighting;
import me.pxl.Serialize.Serialize;
import me.pxl.Utils.IOUtils;
import me.pxl.editor.EntityInteraction.EntityInteractionDriver;
import me.pxl.editor.EntityInteraction.SpawnDriver;
import me.pxl.editor.EntityInteraction.StandartDriver;
import me.pxl.editor.EntityInteraction.TilemapDriver;
import me.pxl.editor.FileExplorer.FileExplorer;
import me.pxl.editor.System.SEditor;
import me.pxl.editor.Utils.EditorUtils;
import me.pxl.editor.Windows.ImWindow;

public class EditorMain {
	/**
	 * List of active Editor Windows
	 */
	List<ImWindow> wins=new ArrayList<>();
	/**
	 * List of Windows sceduled to be removed
	 */
	List<ImWindow> rwins=new ArrayList<>();
	/**
	 * List of Drivers managing Interaction with the Viewport
	 */
	ArrayList<EntityInteractionDriver> eid;
	/**
	 * Currently selected Object
	 */
	public static Object selected;
	/**
	 * If the editor is in playstate
	 */
	boolean play = false;
	/**
	 * Saves the currscene while in playstate
	 */
	String currscene;
	/**
	 * If the Editor viewport should show postprocessing
	 */
	boolean showpost=true;
	/**
	 * Timer getting the frametime
	 */
	Timer frametime=new Timer();
	/**
	 * FileExplorer
	 */
	public FileExplorer fp;
	/**
	 * Save path of the current scene if null scene has no save location
	 */
	public static Path savepath = null;
	/**
	 * If the Occluderbuffer should be shown
	 */
	private boolean showocc=false;
	/**
	 * List of static y values for Plotting
	 */
	List<Long> ys;
	/**
	 * Editor instance
	 */
	private static EditorMain editor;
	/**
	 * Returns the active Editor instance
	 * @return the active Editor
	 */
	public static EditorMain getEditor() {
		return editor;
	}
	/**
	 * adds a new Window
	 * @param win Window to be added
	 */
	public void addWindow(ImWindow win) {
		wins.add(win);
	}
	/**
	 * Removes Window 
	 * @param win window to be removed
	 */
	public void removeWindow(ImWindow win) {
		win.destroy();
		rwins.add(win);
	}
	/**
	 * Initializes the Editor
	 */
	private EditorMain() {
		editor=this;
		
		//Plotting
		ys=new ArrayList<Long>();
		for(int i=0;i<100;i++) {
			ys.add((long) i);
		}
		
		
		//Initialise the Engine
		Engine e = Engine.getEngine();
		
		//Initialise Drivers
		eid = new ArrayList<>();
		eid.add(new StandartDriver());
		eid.add(new TilemapDriver());
		eid.add(new SpawnDriver());
		
		//TODO: Rework for this to be a driver
		e.addSystem(new SEditor(), 0);
		
		//Register Components that can be selected using the Editor
		e.em.registerComponent(CLight.class);
		e.em.registerComponent(CRender.class);
		e.em.registerComponent(CTilemap.class);
		e.em.registerComponent(CRigidBody.class);
		
		//Load code from Project
		e.getAssetManager().getRef(e.getAssetManager().registerAsset(Paths.get("out/")));
		e.setEditor(false);//TODO:???
		

		@SuppressWarnings({ "unused", "unchecked" })
		ImUI ui = new ImUI(e, () -> {
			//Update Windows
			for(ImWindow w:rwins)
				wins.remove(w);
			for(ImWindow w:wins)
				w.display();
			
			//ImGui internal windows
			ImGui.showMetricsWindow();
			ImGui.showStackToolWindow();
			ImGui.showDemoWindow();
//			ImGui.showStyleEditor();
			
			renderMenuBar(e);
			
			if (ImGui.begin("Asset Status")) {
				Set<Entry<UUID, Asset>> s=e.getAssetManager().getAssets();
				ImGui.text("Loaded Assets: "+s.size());
				for(Entry<UUID, Asset> a:s) {
					ImGui.text(a.getValue().getReferences()+":"+e.getAssetManager().getPath(a.getKey()));
				}
			}
			ImGui.end();
			//TODO: Make a extension/plugin system
			if (ImGui.begin("Extensions")) {
				if (ImGui.collapsingHeader("Internal")) {
					ImGui.button("Remove");
				}
				ImGui.text("");
				ImGui.text("Add Extension");
			}
			ImGui.end();
			
			//Scene Outline
			if (ImGui.begin("Scene Panel")) {
				for (int i = e.em.getEntities().size() - 1; i >= 0; i--) {
					Entity ent = e.em.getEntities().get(i);
					if (ent.equals(selected))
						ImGui.smallButton(ent.getName().isEmpty() ? "Unnamed Entity" + ent.hashCode() : ent.getName());
					else
						ImGui.text(ent.getName().isEmpty() ? "Unnamed Entity" + ent.hashCode() : ent.getName());

					if (ImGui.isItemClicked(1)) {
						e.em.despawnEntity(ent);
						if (ent.equals(selected))
							selected = null;
					}
					if (ImGui.isItemClicked())
						selected = ent;
				}
			}
			ImGui.end();
			
			if (ImGui.begin("Renderer Stats")) {
				ImGui.text("Dimensions:"+e.getFinalBuffer().getWidth() + "x" + e.getFinalBuffer().getHeight());
				e.getSystems().forEach((a)->{//a bit questionable but will get reworked down the line (Vulkan multithreading)
					if(a instanceof SRenderer)
						ImGui.text("Drawcalls: "+((SRenderer)a).getDrawcalls());
				});
				
				ImGui.text("Frametime: "+frametime.getmillis());
				ImGui.text(e.em.getEntities().size()+" Entities currently loaded");
				ImGui.text(e.getAssetManager().getAssets().size()+" Assets currently loaded");
				
				ImPlot.fitNextPlotAxes();
				List<String> names=new LinkedList<>();
				List<Long> values=new LinkedList<>();
				if(ImGui.collapsingHeader("Time")) {
					if (ImPlot.beginPlot("RenderTime")) {
						for(Entry<String, List<Long>> entry:map.entrySet()) {
							if(!entry.getKey().startsWith("i")) {
								values.add(entry.getValue().get(entry.getValue().size()-1));
								names.add(entry.getKey());
							}
							ImPlot.plotHLines("60fps", new Long[] {(long) 15});
							if(entry.getValue().size()>0)
								ImPlot.plotLine(entry.getKey(), ys.subList(0, entry.getValue().size()).toArray(new Long[entry.getValue().size()]),
										entry.getValue().toArray(new Long[entry.getValue().size()]));
						}
						ImPlot.endPlot();
					}
				}
				frametime.reset();
				ImGui.text("Lightmap:");//Will get removed with the postprocesing rework
				ImGui.image(Lighting.Lightmap.getID(),300,300);
			}
			ImGui.end();
			//Postprocessing Panel will get removed
			if (ImGui.begin("Test")) {
				try {
					e.getPostProcessor().serialize(new ImGuiSerializeAdapter(this));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			ImGui.end();
			
			if (ImGui.begin("Scene")) {
				ImVec2 windowsize = ImGui.getContentRegionAvail();
				if (windowsize.x != e.getFinalBuffer().getWidth() || windowsize.y != e.getFinalBuffer().getHeight())
					e.setBufferSize(windowsize.x, windowsize.y);
				
				int renderid=e.getFinalBuffer().getAttachments()[0].getID();
				if(showpost)
					renderid=e.getFinalImage().getID();
				if(showocc)
					renderid=e.getFinalBuffer().getAttachments()[1].getID();
				ImGui.image(renderid, windowsize.x, windowsize.y);
				float mousex = (ImGui.getMousePos().x - ImGui.getItemRectMin().x);
				float mousey = (ImGui.getMousePos().y - ImGui.getItemRectMin().y);
				if (ImGui.beginPopupContextItem("Scene Popup")) {
					for (EntityInteractionDriver eid : eid) {
						eid.onContextMenu(mousex, mousey);
					}
					ImGui.endPopup();
				}
				if (ImGui.isItemHovered()) {
					for (EntityInteractionDriver eid : eid) {
						for (int mb = 0; mb < 3; mb++) {
							if (ImGui.isMouseClicked(mb))
								eid.onClick(mousex, mousey, mb);
							if (ImGui.isMouseDown(mb))
								eid.onDown(mousex, mousey, mb);
							if (ImGui.isMouseReleased(mb)) {
								Object o = eid.onRelease(mousex, mousey, mb);
								if (o != null)
									selected = o;
							}
							eid.onUpdate(mousex, mousey);
						}
					}
				}
				
				if (ImGui.beginDragDropTarget()) {
					for (EntityInteractionDriver eid : eid) {
						for (String s : eid.shouldDrop()) {
							Object payload = ImGui.acceptDragDropPayload(s);
							if (payload != null) {
								eid.onDrop(s, payload, mousex, mousey);
							}
						}
					}
					ImGui.endDragDropTarget();
				}
			}
			ImGui.end();
			
			if (ImGui.begin("Inspektor") && selected != null) {
				if (selected instanceof Entity) {
					inspectEnt((Entity) selected);
				} else {
					// TODO: Add other Classes
					System.out.println("TODO: Add other Classes");
				}
			}
			ImGui.end();
			fp.update();
		});
		ImPlot.createContext();
		EventManager.register(this);
		fp = new FileExplorer(e);
		//Timed Update
		e.update(new MultipleTimer().setOnReset((a)->{
			for(Entry<String, Long> entr:a) {
				if(!map.containsKey(entr.getKey()))
					map.put(entr.getKey(), new LinkedList<Long>());
				List<Long> l=map.get(entr.getKey());
				l.add(entr.getValue());
				if(l.size()>100)
					l.remove(0);
			}
		}));
		System.exit(0);
	}
	private void renderMenuBar(Engine e) {
		if (ImGui.beginMainMenuBar()) {
			if (ImGui.beginMenu("File")) {
				if (ImGui.menuItem("New", "Ctrl + N")) {
					e.em.clear();
					savepath = null;
				}
				if (ImGui.menuItem("Save", "Ctrl + S", false, savepath != null)) {
					try {
						IOUtils.write(this.play ? this.currscene : e.em.saveScene(), savepath);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				if (ImGui.menuItem("Save As", "Ctrl + Shift + S")) {
					savepath = EditorUtils.showFileDialog(AssetManager.getAssetManager().getPath());
					if (savepath != null)
						try {
							if (!savepath.endsWith("scene"))
								savepath = Paths.get(savepath.toAbsolutePath().toString() + ".scene");
							IOUtils.write(this.play ? this.currscene : e.em.saveScene(), savepath);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				}
				if (ImGui.menuItem("Refresh", "Ctrl + R")) {
					try {
						selected = null;
						e.em.loadScene(e.em.saveScene(), true, new Vector3f());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					savepath = null;
				}
				ImGui.endMenu();
			}
			if(ImGui.beginMenu("Scene")) {
				if(ImGui.menuItem("Show PostProcessing","",showpost))
					showpost=!showpost;
				ImGui.endMenu();
			}
			if(ImGui.beginMenu("Scene")) {
				if(ImGui.menuItem("Show Occluder","",showocc))
					showocc=!showocc;
				ImGui.endMenu();
			}
			if (ImGui.beginMenu("Entity")) {
				if (ImGui.menuItem("Entity")) {
					e.em.instanceEntity(Entity.class);
				}
				for (@SuppressWarnings("rawtypes") Class c : e.em.getElist()) {
					if (ImGui.menuItem(c.getName())) {
						e.em.instanceEntity(c);
					}
				}
				ImGui.endMenu();
			}
			if (ImGui.menuItem(play ? "Stop" : "Play")) {
				play = !play;
				e.setEditor(play);
				if (play) {
					try {
						currscene = e.em.saveScene();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					try {
						selected = null;
						e.em.loadScene(currscene, true, new Vector3f());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			ImGui.endMainMenuBar();
		}
	}
	HashMap<String, List<Long>> map=new HashMap<>();
	

	@EventTarget
	public void onClick(EMouseButton mb) {
	}

	public static void main(String[] args) {
		new EditorMain();
	}

	
	@SuppressWarnings("unchecked")
	private void inspectEnt(Entity selected) {
		ImGuiSerializeAdapter s=new ImGuiSerializeAdapter(this);
		
		Serialize.serializeclass(selected, s);
		for (Component c : selected.getComponents()) {
			if (ImGui.collapsingHeader(c.getClass().getSimpleName())) {
				ComponentInspector.renderComponent(c,s);
			}
		}
		ImGui.text("");
		if (ImGui.beginMenu("Add Component")) {
			for (@SuppressWarnings("rawtypes") Class c : Engine.getEngine().em.getRegisteredComponents())
				if (ImGui.menuItem(c.getSimpleName())) {
					Engine.getEngine().em.attachComponent(selected, c);
				}
			ImGui.endMenu();
		}
	}

}

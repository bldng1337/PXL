package me.pxl.editor.FileExplorer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import imgui.ImGui;
import imgui.flag.ImGuiDragDropFlags;
import me.pxl.Engine;
import me.pxl.Asset.AssetManager;
import me.pxl.Backend.Generic.Texture;
import me.pxl.Utils.IOUtils;
import me.pxl.editor.EditorMain;
import me.pxl.editor.Utils.EditorUtils;
import me.pxl.editor.Windows.TileMap;

public class FileExplorer {

	public Texture folder,file;
	
	public FileExplorer(Engine e) {
		folder=EditorUtils.getTex(e, IOUtils.getinfromclasspath(FileExplorer.class, "folder.png"));
		file=EditorUtils.getTex(e, IOUtils.getinfromclasspath(FileExplorer.class, "file.png"));
		filewindow.put("tile", TileMap.class);
	}
	
	public void update() {
		if(ImGui.begin("File Explorer")) {
			try {
				renderFolder();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ImGui.end();
	}
//	HashMap<String, String> newfile=new HashMap<>();
	HashMap<String, Class<? extends FileWindow>> filewindow=new HashMap<>();
	String path="";
	Path drag;
	private void renderFolder() throws IOException {
		if(ImGui.smallButton("root"))
			path="";
		for(String a:path.split("/")) {
			if(a.isEmpty())
				continue;
			ImGui.sameLine();
			ImGui.text("/");
			ImGui.sameLine();
			if(ImGui.smallButton(a))
				path=path.split(a)[0]+a;
		}
		int id=0;
		Path root=Engine.getEngine().getAssetManager().getPath();
		Path p=Paths.get(root.toString(),path);//TODO: Different Folders
		List<Path> subfolder = Files.walk(p, 1).sorted((p1,p2)->{return (Files.isDirectory(p2)?1:0)-(Files.isDirectory(p1)?1:0);})
        .collect(Collectors.toList());
		subfolder.remove(0);
		
		float size=90;
		float pad=10;
		float width=ImGui.getContentRegionAvail().x;
		ImGui.columns((int) Math.max(1,width/(size+pad)),"",false);
		for(Path sub:subfolder) {
			if(Files.isDirectory(sub)) {
				ImGui.pushID(id++);
				if(ImGui.imageButton(folder.getID(), size, size)) {
					path+="/"+sub.getFileName().toString();
				}
				ImGui.popID();
				if(ImGui.beginDragDropSource(ImGuiDragDropFlags.SourceAllowNullID)) {
					ImGui.setDragDropPayload("FE_FOLDER", sub);
					drag=sub;
		            ImGui.image(folder.getID(), size, size);
		            ImGui.endDragDropSource();
				}
				ImGui.text(sub.getFileName().toString());
				ImGui.nextColumn();
			}else {
				ImGui.pushID(id++);
				
				if(ImGui.imageButton(file.getID(), size, size)) {
					
				}
				ImGui.popID();
				if(ImGui.isMouseDoubleClicked(0)&&ImGui.isItemHovered()) {
					if(filewindow.containsKey(sub.getFileName().toString().split("\\.")[1]))
						try {
							EditorMain.getEditor().addWindow(filewindow.get(sub.getFileName().toString().split("\\.")[1]).getConstructor(Path.class).newInstance(sub));
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException | NoSuchMethodException | SecurityException e) {
							e.printStackTrace();
						}
				}
				if(ImGui.beginDragDropSource(ImGuiDragDropFlags.SourceAllowNullID)) {
					drag=sub;
					if(AssetManager.getAssetManager().getType(drag)!=null) {
						ImGui.setDragDropPayload("FE_"+AssetManager.getAssetManager().getType(drag).getSimpleName(), drag);
					}else
						ImGui.setDragDropPayload("FE_FILE)", sub);
		            ImGui.image(file.getID(), size, size);
		            ImGui.endDragDropSource();
				}
				ImGui.text(sub.getFileName().toString());
				ImGui.nextColumn();
			}
		}
	}
}

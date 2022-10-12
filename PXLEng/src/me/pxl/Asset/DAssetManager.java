package me.pxl.Asset;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map.Entry;

import me.pxl.Asset.Assets.AGif;
import me.pxl.Asset.Assets.AJava;
import me.pxl.Asset.Assets.AScene;
import me.pxl.Asset.Assets.AShader;
import me.pxl.Asset.Assets.ATexture;
import me.pxl.Asset.Assets.ATileMap;
import me.pxl.Backend.Generic.RenderAPI;
import me.pxl.Event.EventManager;
import me.pxl.Event.EventTarget;
import me.pxl.Event.Events.ERender;
import me.pxl.Utils.IOUtils;

import java.util.UUID;

public class DAssetManager extends AssetManager{
	Path p;
	//Development Asset Manager
	public DAssetManager(long mainwin,Path p,RenderAPI r) {
		super(mainwin,p,r);
		this.p=p;
		//Fill the UUID to Asset Map
		try {
			Files.createDirectories(passets.getParent());
			if(Files.notExists(passets))
				Files.createFile(passets);
			if(Files.size(passets)>1) {
				@SuppressWarnings("unchecked")
				HashMap<String,String> s=gson.fromJson(IOUtils.stringfromFile(passets), HashMap.class);
				for(Entry<String, String> ss:s.entrySet())
					softref.put(UUID.fromString(ss.getKey()), ss.getValue());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//Watchservice
		EventManager.register(this);
		try {
			watchService = FileSystems.getDefault().newWatchService();
			k=p.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_DELETE,StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e) {	
			e.printStackTrace();
		}
		
		//Register Assets
		registerAssetType(AShader.class);
		registerAssetType(ATexture.class);
		registerAssetType(AScene.class);
		registerAssetType(AJava.class);
		registerAssetType(AGif.class);
		registerAssetType(ATileMap.class);
	}
	//Reloads Assets that got changed
	WatchService watchService;
	WatchKey k;
	@EventTarget
	public void onUpdate(ERender u) {
		if(k!=null) {
			for(WatchEvent<?> we:k.pollEvents()) {
				System.out.println("Change in "+we.context());
				if(we.context() instanceof Path) {
					UUID uid=this.registerAsset((Path)we.context());
					System.out.println("Is loaded "+this.isAssetLoaded(uid));
					System.out.println("Scheduling reload "+we.context()+""+uid);
					this.reload(uid);
				}
			}
		}
	}

}

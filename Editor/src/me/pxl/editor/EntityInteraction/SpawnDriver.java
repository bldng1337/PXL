package me.pxl.editor.EntityInteraction;

import java.io.IOException;
import java.nio.file.Path;

import org.joml.Vector3f;

import imgui.ImGui;
import me.pxl.Engine;
import me.pxl.Asset.AssetManager;
import me.pxl.Asset.Assets.AScene;
import me.pxl.ECS.Entity;
import me.pxl.ECS.Components.CRender;
import me.pxl.editor.EditorMain;

public class SpawnDriver extends EntityInteractionDriver{

	@Override
	public String[] shouldDrop() {
		return new String[] {"FE_AScene","FE_ATexture"};
	}

	@Override
	public void onDrop(String type, Object o, float x, float y) {
		Path p=(Path) o;
		Engine e=Engine.getEngine();
		switch(type) {
		case "FE_ATexture":
			Entity ent=e.em.instanceEntity(Entity.class);
			ent.setName(p.getFileName().toString());
			ent.pos.set(x-e.em.getTranslation().x, y-e.em.getTranslation().y, 0);
			CRender c=e.em.attachComponent(ent, CRender.class);
			c.txt=AssetManager.getAssetManager().getRef(AssetManager.getAssetManager().registerAsset(p));
		break;
		case "FE_AScene":
			AScene sc=AssetManager.getAssetManager().getRef(AssetManager.getAssetManager().registerAsset(p));
			try {
				if(ImGui.getIO().getKeyShift()) {
					e.em.loadScene(sc.getScene(), false, new Vector3f(x, y, 0).sub(e.em.getTranslation()));
				}else {
					EditorMain.selected=null;
					e.em.loadScene(sc.getScene(), true, new Vector3f(0,0,0));
					EditorMain.savepath=p;
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			AssetManager.getAssetManager().returnRef(sc);
			break;
		}
	}

}

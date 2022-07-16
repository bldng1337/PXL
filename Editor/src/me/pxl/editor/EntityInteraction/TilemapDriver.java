package me.pxl.editor.EntityInteraction;

import java.util.List;

import me.pxl.Engine;
import me.pxl.ECS.Component;
import me.pxl.ECS.Entity;
import me.pxl.ECS.Components.CTilemap;

public class TilemapDriver extends EntityInteractionDriver{

	@Override
	public String[] shouldDrop() {
		return new String[] {"CTile"};
	}

	@Override
	public void onDrop(String type, Object payload, float mx, float my) {
		Engine em=Engine.getEngine();
		List<Component> l=Engine.getEngine().em.getComponents(CTilemap.class);
		for(Component c:l) {
			CTilemap ct=(CTilemap) c;
			Entity e=c.getEntity();
			if(isHovering(e.getPos().x, e.getPos().y, e.size.x, e.size.y, mx, my)) {
				int dx=(int) Math.floor((mx-e.getPos().x-em.em.getTranslation().x)/e.size.x*ct.numtiles.x);
				int dy=(int) Math.floor((my-e.getPos().y-em.em.getTranslation().y)/e.size.y*ct.numtiles.y);
				for(int i=0;i<ct.l.size();i++) {
					if(payload==ct.l.get(i))
					ct.getMap()[dx][dy]=i;
				}
			}
		}
	}
}

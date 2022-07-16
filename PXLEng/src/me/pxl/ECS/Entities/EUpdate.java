package me.pxl.ECS.Entities;

import me.pxl.Engine;
import me.pxl.ECS.Entity;
import me.pxl.ECS.Components.CUpdate;

public abstract class EUpdate extends Entity{
	
	@Override
	public void onSetup() {
		Engine.getEngine().em.attachComponent(this, CUpdate.class).setCall(this::onUpdate);
		super.onSetup();
	}
	
	protected abstract void onUpdate();
}

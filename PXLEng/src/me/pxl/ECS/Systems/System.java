package me.pxl.ECS.Systems;

import me.pxl.Engine;

public abstract class System {
	
	public abstract int priority();
	public abstract boolean editor();
	public void beginPlay(Engine em) {}
	public abstract void init(Engine em);
	public abstract void update(Engine em);
}

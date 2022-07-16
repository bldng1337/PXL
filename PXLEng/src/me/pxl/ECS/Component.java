package me.pxl.ECS;

public class Component {
	protected Entity e;
	
	public Entity getEntity() {
		return e;
	}
	
	public boolean serializable() {
		return true;
	}
	
	public void onDestroy() {}
	
	public void init() {
		
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.hashCode()==obj.hashCode();
	}
}

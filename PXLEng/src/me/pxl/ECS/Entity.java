package me.pxl.ECS;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import me.pxl.Serialize.Range;

public class Entity {
	protected List<Component> complist=new ArrayList<Component>();
	public String Name;
	public Vector3f pos;
	public Vector2f size;
	@Range(max=360f)
	public float rotation;
	Matrix4f m;
	public Entity() {
		Name=this.getClass().getSimpleName();
		pos=new Vector3f(0f);
		size=new Vector2f(100f);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getComponent(Class<T> cc) {
		for(Component c:complist)
			if(c.getClass().equals(cc))
				return (T) c;
		return null;
	}
	
	public Vector3f getPos() {
		return pos;
	}
	
	public void setPos(Vector3f v) {
		pos=v;
	}

	public Vector2f getSize() {
		return size;
	}

	public void onSetup() {
	}

	public String getName() {
		return Name;
	}

	public List<Component> getComponents() {
		return complist;
	}

	public void setName(String string) {
		this.Name=string;
	}
	
}
 
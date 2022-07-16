package me.pxl.editor.Debug;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector2f;

import me.pxl.Event.EventManager;
import me.pxl.Event.EventTarget;
import me.pxl.Event.Events.ERender;

public class DebugDrawUtils {
	static DebugDrawUtils d;
	private DebugDrawUtils() {
		EventManager.register(this);
	}
	List<Primitive> li =new LinkedList<DebugDrawUtils.Primitive>();
	static class Primitive{
		
	}
	static class HollowCircle extends Primitive {
		float size,x,y;
		public HollowCircle(float x,float y,float size) {
			this.size=size;
			this.x=x;
			this.y=y;
		}
	}
	static class Circle extends Primitive {
		float size,x,y;
		public Circle(float x,float y,float size) {
			this.size=size;
			this.x=x;
			this.y=y;
		}
	}
	static class Polygon extends Primitive {
		Vector2f polygon[];
		public Polygon(Vector2f[] points) {
			polygon=points;
		}
	}
	static class Line extends Primitive {
		float x,y,x2,y2,thickness;
		public Line(float x, float y, float x2, float y2, float thickness) {
			this.x=x;
			this.y=y;
			this.x2=x2;
			this.y2=y2;
			this.thickness=thickness;
		}
		
	}
	
	public static void init() {
		d=new DebugDrawUtils();
	}
	
	public static void drawLine(float x,float y,float x2,float y2,float thickness) {
		d.li.add(new Line(x,y,x2,y2,thickness));
	}
	
	public static void drawPolygon(Vector2f[] verts) {
		d.li.add(new Polygon(verts));
	}
	
	public static void drawHollowCircle(float x,float y,float size) {
		d.li.add(new Circle(x, y, size));
	}
	
	public static void drawCircle(float x,float y,float size) {
		d.li.add(new Circle(x, y, size));
	}
	
	@EventTarget
	public void onRender(ERender r) {
		for(Primitive p:li) {
			switch(p.getClass().getSimpleName()) {
			case "Circle":
				
				break;
			}
		}
	}

}

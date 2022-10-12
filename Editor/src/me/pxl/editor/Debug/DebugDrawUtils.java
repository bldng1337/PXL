package me.pxl.editor.Debug;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector2f;

import me.pxl.Event.EventManager;
import me.pxl.Event.EventTarget;
import me.pxl.Event.Events.ERender;
/**
 * Skeleton for Debug Drawing not implemented yet
 */
@Deprecated//Remove Deprecated when fully implemented
public class DebugDrawUtils {
	static DebugDrawUtils d;
	
	/**
	 * Registers this Class to the Event Manager
	 */
	private DebugDrawUtils() {
		EventManager.register(this);
	}
	/**
	 * Init the Debug draw for use in the static functions of the class
	 */
	public static void init() {
		d=new DebugDrawUtils();
	}
	/**
	 * List of Shapes that will be drawn in the Render update
	 */
	List<Primitive> li =new LinkedList<Primitive>();
	
	/**
	 * Superclass of all Primitives
	 */
	static class Primitive{}
	/**
	 * Class describing a Hollow Circle
	 */
	static class HollowCircle extends Primitive {
		float size,x,y;
		public HollowCircle(float x,float y,float size) {
			this.size=size;
			this.x=x;
			this.y=y;
		}
	}
	/**
	 * Class describing a Circle
	 */
	static class Circle extends Primitive {
		float size,x,y;
		public Circle(float x,float y,float size) {
			this.size=size;
			this.x=x;
			this.y=y;
		}
	}
	/**
	 * Class describing a Polygon
	 */
	static class Polygon extends Primitive {
		Vector2f polygon[];
		public Polygon(Vector2f[] points) {
			polygon=points;
		}
	}
	/**
	 * Class describing a Line
	 */
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
	/**
	 * Draws a Line between two Points
	 * @param x x of Point A
	 * @param y y of Point A
	 * @param x2 x of Point B
	 * @param y2 y of Point B
	 * @param thickness Thickness of the Line
	 */
	public static void drawLine(float x,float y,float x2,float y2,float thickness) {
		d.li.add(new Line(x,y,x2,y2,thickness));
	}
	/**
	 * Draws a Polygon 
	 * @param verts Vertecies of a Polygon
	 */
	public static void drawPolygon(Vector2f[] verts) {
		d.li.add(new Polygon(verts));
	}
	/**
	 * Draws a Hollow Circle
	 * @param x x Coordinate of the Circle
	 * @param y y Coordinate of the Circle
	 * @param size size of the Circle
	 */
	public static void drawHollowCircle(float x,float y,float size) {
		d.li.add(new Circle(x, y, size));
	}
	/**
	 * Draws a Circle
	 * @param x x Coordinate of the Circle
	 * @param y y Coordinate of the Circle
	 * @param size size of the Circle
	 */
	public static void drawCircle(float x,float y,float size) {
		d.li.add(new Circle(x, y, size));
	}
	/**
	 * Render Event
	 */
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

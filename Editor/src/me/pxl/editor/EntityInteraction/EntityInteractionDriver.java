package me.pxl.editor.EntityInteraction;

public abstract class EntityInteractionDriver {
	
	public void onContextMenu(float mx, float my) {}
	public void onClick(float mx, float my, int button) {}
	public void onDown(float mx, float my, int button) {}
	public Object onRelease(float mx, float my, int mb) {return null;}
	
	public abstract String[] shouldDrop();
	public abstract void onDrop(String type,Object payload,float mx,float my);
	
	public void onUpdate(float mx, float my) {}
	
	protected boolean isHovering(float x,float y,float width,float height,float mx,float my) {
		return mx>x&&mx<x+width&&
				my>y&&my<y+height;
	}
}

package me.pxl.editor;

import me.pxl.ECS.Component;
import me.pxl.Serialize.Serialize;

public class ComponentInspector {

	public static void renderComponent(Component c, ImGuiSerializeAdapter s) {
		switch(c.getClass().getSimpleName()) {
		default:
			Serialize.serializeclass(c, s);
			break;
		}
	}
}

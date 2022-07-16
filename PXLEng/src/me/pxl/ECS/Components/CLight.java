package me.pxl.ECS.Components;

import org.joml.Vector2f;
import org.joml.Vector3f;

import me.pxl.ECS.Component;

public class CLight extends Component{
	public Vector2f lightpos=new Vector2f();
	public float size=20;
	Vector3f Color;
	float intensity=0;
}

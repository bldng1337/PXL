package me.pxl.ECS.Components;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;

import me.pxl.ECS.Component;
import me.pxl.Serialize.Range;

public class CRigidBody extends Component{
	
	public BodyType type=BodyType.STATIC;
	public boolean FixedRotation=false;
	Body body;
	@Range()
	public float friction;
	@Range()
	public float restitution;
	public boolean bullet;
	public float density;
	
	
	public void init() {
	}
	
	
	public Body getBody() {
		return body;
	}




	public void setBody(Body bd) {
		this.body=bd;
	}
}

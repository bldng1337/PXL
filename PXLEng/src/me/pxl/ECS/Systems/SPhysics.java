package me.pxl.ECS.Systems;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;

import me.pxl.Engine;
import me.pxl.ECS.Component;
import me.pxl.ECS.Components.CRigidBody;
import me.pxl.Log.Timer;

public class SPhysics extends System{

	World w;
	public float Timestep=0.3f;
	public int VIter=6;
	public int PIter=2;
	public Vector2f gravity=new Vector2f(0,981f);
//	float physicsspace=0.2f;
	@Override
	public int priority() {
		return 0;
	}

	@Override
	public boolean editor() {
		return false;
	}
	
	@Override
	public void init(Engine em) {
		
	}
	@Override
	public void beginPlay(Engine em) {
		w=new World(new Vec2(gravity.x,gravity.y));
		for(Component c:em.em.getComponents(CRigidBody.class)) {
			CRigidBody cr=(CRigidBody) c;
			BodyDef bd=new BodyDef();
			bd.type=cr.type;
			bd.bullet=cr.bullet;
			bd.position.set(c.getEntity().getPos().x, c.getEntity().getPos().y);
			bd.fixedRotation=cr.FixedRotation;
			bd.active=true;
			bd.angle=(float) (cr.getEntity().rotation*(Math.PI*2)/360f);
			bd.userData=cr.getEntity();
			cr.setBody(this.w.createBody(bd));
			PolygonShape shape=new PolygonShape();
			shape.setAsBox(c.getEntity().size.x/2, c.getEntity().size.y/2, new Vec2(c.getEntity().size.x/2, c.getEntity().size.y/2), 0);
			FixtureDef def=new FixtureDef();
			def.density=cr.density;
			def.friction=cr.friction;
			def.restitution=cr.restitution;
			def.shape=shape;
			cr.getBody().createFixture(def);
		}
		t=new Timer();
	}
	Timer t;
	@Override
	public void update(Engine em) {
		if(gravity.x!=w.getGravity().x||gravity.y!=w.getGravity().y)
			w.setGravity(new Vec2(gravity.x,gravity.y));
		w.step((float)t.getmillis()/1000f, VIter, PIter);
		t.reset();
		for(Component c:em.em.getComponents(CRigidBody.class)) {
			CRigidBody cr=(CRigidBody) c;
			c.getEntity().getPos()
			.set(
					cr.getBody().getPosition().x,
					cr.getBody().getPosition().y, c.getEntity().getPos().z);
			c.getEntity().rotation=(float) (cr.getBody().getAngle()/(Math.PI*2)*360f);
		}
	}

	public World getWorld() {
		return w;
	}

}

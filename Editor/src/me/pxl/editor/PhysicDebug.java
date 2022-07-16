package me.pxl.editor;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.particle.ParticleColor;
import org.joml.Vector2f;

import me.pxl.editor.Debug.DebugDrawUtils;

public class PhysicDebug extends DebugDraw {

	@Override
	public void drawPoint(Vec2 argPoint, float argRadiusOnScreen, Color3f argColor) {
		DebugDrawUtils.drawCircle(argPoint.x, argPoint.y, argRadiusOnScreen);
	}

	@Override
	public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
		Vector2f[] v=new Vector2f[vertexCount];
		for(int i=0;i<vertexCount;i++) {
			v[i]=new Vector2f(vertices[i].x,vertices[i].y);
		}
		DebugDrawUtils.drawPolygon(v);
	}

	@Override
	public void drawCircle(Vec2 center, float radius, Color3f color) {
		DebugDrawUtils.drawHollowCircle(center.x, center.y, radius);
	}

	@Override
	public void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color3f color) {
		DebugDrawUtils.drawCircle(center.x, center.y, radius);
	}

	@Override
	public void drawSegment(Vec2 p1, Vec2 p2, Color3f color) {
		DebugDrawUtils.drawLine(p1.x, p2.y, p1.x, p2.y, 3f);
	}

	@Override
	public void drawTransform(Transform xf) {
		DebugDrawUtils.drawLine(xf.p.x, xf.p.y, xf.p.x+xf.q.getCos()*5f, xf.p.y+xf.q.getSin()*5f, 2f);
		DebugDrawUtils.drawLine(xf.p.x, xf.p.y, xf.p.x+xf.q.getCos()*5f, xf.p.y, 2f);
		DebugDrawUtils.drawLine(xf.p.x, xf.p.y, xf.p.x, xf.p.y+xf.q.getSin()*5f, 2f);
	}

	@Override
	public void drawString(float x, float y, String s, Color3f color) {
		//TODO draw string
	}

	@Override
	public void drawParticles(Vec2[] centers, float radius, ParticleColor[] colors, int count) {
		for(Vec2 v:centers) {
			DebugDrawUtils.drawCircle(v.x, v.y, radius);
		}
	}

	@Override
	public void drawParticlesWireframe(Vec2[] centers, float radius, ParticleColor[] colors, int count) {
		for(Vec2 v:centers) {
			DebugDrawUtils.drawHollowCircle(v.x, v.y, radius);
		}
	}
}

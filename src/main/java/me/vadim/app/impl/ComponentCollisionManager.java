package me.vadim.app.impl;

import me.vadim.app.CollisionManager;
import me.vadim.app.Entity;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author vadim
 */
public class ComponentCollisionManager implements CollisionManager {

	private final Component component;
	private final int       gridSize;

	public ComponentCollisionManager(Component component, int gridSize) {
		this.component = component;
		this.gridSize  = gridSize;
//		System.out.println(component);
//		System.out.println(component.getSize());
	}

	@Override
	public boolean outOfBounds(Rectangle bb) {
		int w = component.getWidth();
		int h = component.getHeight();
		boolean a = bb.x + bb.width >= component.getWidth();
		boolean b= bb.y + bb.height >= component.getHeight();
		boolean c=  bb.x < 0 || bb.y < 0;
		return a ||
			   b ||
			  c;
	}

	@Override
	public void handleExitBounds(Entity entity) {
		entity.die();
	}

	@Override
	public boolean collides(Rectangle bb, Rectangle with) {
		return with.contains(new Point((int) bb.getCenterX(), (int) bb.getCenterY())) || doOverlap(bb, with);
	}
	// Returns true if two rectangles (l1, r1) and (l2, r2) overlap
	private static  boolean doOverlap(Rectangle s1, Rectangle s2) {
		Point l1 = s1.getLocation();
		Point r1 = new Point(s1.x + s1.width, s1.y + s1.height);
		Point l2 = s2.getLocation();
		Point r2 = new Point(s2.x + s2.width, s2.y + s2.height);

		// To check if either rectangle is actually a line
		// For example :  l1 ={-1,0}  r1={1,1}  l2={0,-1}  r2={0,1}

		if (l1.x == r1.x || l1.y == r1.y || l2.x == r2.x || l2.y == r2.y)
		{
			// the line cannot have positive overlap
			return false;
		}


		// If one rectangle is on left side of other
		if (l1.x >= r2.x || l2.x >= r1.x) {
			return false;
		}

		// If one rectangle is above other
		if (r1.y >= l2.y || r2.y >= l1.y) {
			return false;
		}

		return true;
	}

	@Override
	public Point randomPointOnGrid() {
		Random random = ThreadLocalRandom.current();
		return new Point(random.nextInt(component.getWidth() / gridSize) * gridSize, random.nextInt(component.getHeight() / gridSize) * gridSize);
	}
}

package me.vadim.app.impl;

import me.vadim.app.CollisionManager;
import me.vadim.app.Direction;
import me.vadim.app.Snake;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author vadim
 */
public class JSnake extends JEntity implements Snake {

	private final List<Rectangle> segments = new ArrayList<>();

	private       CollisionManager collisionManager;
	private final int              gridSize;
	private       Direction        direction;

	public JSnake(int size) {
		this(null, new Point(0, 0), size);
	}

	public JSnake(CollisionManager collisionManager, Point origin, int gridSize) {
		super(new Rectangle(origin, new Dimension(gridSize, gridSize)));
		this.collisionManager = collisionManager;
		this.gridSize         = gridSize;
		segments.add(bb());
	}

	public void teleportHead(int x, int y) {
		segments.remove(0);
		x = (x / gridSize) * gridSize;
		y = (y / gridSize) * gridSize;
		segments.add(0, new Rectangle(new Point(x, y), new Dimension(gridSize, gridSize)));
	}

	@Override
	public Rectangle head() { return new Rectangle(segments.get(0)); }

	@Override
	public Rectangle[] segments() { return segments.stream().map(Rectangle::new).toArray(Rectangle[]::new); }

	@Override
	public int length() { return segments.size(); }

	//	@Override
//	public void move(Point point, boolean grow) {
//		if (!grow) //remove last (length is unchanged)
//			segments.remove(length() - 1);
//		segments.add(0, new Point(point));//add new one
//	}

	private Rectangle hitSegment;//the segment that was ran into

	@Override
	public void move(boolean grow) {
		if (!alive()) throw new UnsupportedOperationException("Cannot move dead snake.");

		//synchronize direction change
		dirLock.lock();
		try {
			this.direction = dirty;
		} finally {
			dirLock.unlock();
		}

		if (direction == null) return;

		Rectangle head = head();
		head.x /= gridSize;
		head.y /= gridSize;
		switch (direction) {
			case UP -> head.y--;
			case DOWN -> head.y++;
			case LEFT -> head.x--;
			case RIGHT -> head.x++;
		}
		head.x *= gridSize;
		head.y *= gridSize;

		if (collisionManager != null && collisionManager.outOfBounds(head))
			collisionManager.handleExitBounds(this);
		else {
			if (!grow) //remove last (so that length is unchanged)
				segments.remove(length() - 1);

//			if (collisionManager != null) {
			for (Rectangle segment : segments()) {//check for self-collision
				//if (head.equals(segment)) continue;//in case of teleport todo this line breaks it
				if (collisionManager.collides(head, segment) || collisionManager.collides(segment, head)) {
					hitSegment = segment;
					die();
					return;
				}
			}
//			}

			segments.add(0, head);//add new one
		}

		bb = head();
	}

	private final    ReentrantLock dirLock = new ReentrantLock();
	/*
	 * This one is necessary because of a bug:
	 * e.g. you are going LEFT, and press DOWN and RIGHT before the snake could move,
	 * it would go backwards and run into itself because the "opposite direction" check
	 * would succeed after the first key stroke. All this synchronization fixes it, so that
	 * the real direction is only updated when the snake actually moves.
	 * Way too long of a comment...
	 */
	private volatile Direction     dirty;

	@Override
	public void setDirection(Direction direction) {
		dirLock.lock();
		try {
			this.dirty = direction;
		} finally {
			dirLock.unlock();
		}
	}

	@Override
	public Direction getDirection() {
		//noob multithreading here, probably unnecessary
		dirLock.lock();
		Direction acquired;
		try {
			acquired = direction;
		} finally {
			dirLock.unlock();
		}
		return acquired;
	}

	@Override
	public void setCollisionManager(CollisionManager manager) { this.collisionManager = manager; }

	//	@Override
//	public Point onGrid(int x, int y) { return new Point(x * gridSize, y * gridSize); }

	@Override
	public void render(Graphics2D g2d) {
		//fill
		g2d.setColor(alive() ? new Color(6, 233, 19) : new Color(99, 17, 141));
		segments.forEach(g2d::fill);

		//outline
		g2d.setColor(alive() ? Color.YELLOW : Color.MAGENTA);
		segments.forEach(g2d::draw);

		//different color hit segment or head
		if (hitSegment != null) g2d.fill(hitSegment);
		else g2d.fill(head());
	}
}

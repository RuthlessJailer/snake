package me.vadim.app;

import java.awt.*;

/**
 * @author vadim
 */
public interface CollisionManager {

	boolean outOfBounds(Rectangle bb);

	void handleExitBounds(Entity trespasser);

	boolean collides(Rectangle bb, Rectangle with);

	Point randomPointOnGrid();

}

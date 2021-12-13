package me.vadim.app;

import java.awt.*;

/**
 * @author vadim
 */
public interface Snake extends Entity {

	Rectangle head();

	Rectangle[] segments();

	int length();

//	void move(Point point, boolean grow);
	void move(boolean grow);

	void setDirection(Direction direction);

	Direction getDirection();

	void setCollisionManager(CollisionManager manager);

//	Point onGrid(int x, int y);

}

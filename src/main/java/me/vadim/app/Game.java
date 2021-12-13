package me.vadim.app;

/**
 * @author vadim
 */
public interface Game {

	Snake snake();

	Apple currentApple();

	void setCollisionManager(CollisionManager manager);

	boolean over();

	void restart();
}

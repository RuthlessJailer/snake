package me.vadim.app.impl;

import me.vadim.app.Direction;
import me.vadim.app.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author vadim
 */
public class WASDController implements KeyListener {

	private final Game game;

	public WASDController(Game game) {
		this.game = game;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		Direction dir = Direction.fromChar(e.getKeyChar());
		if (dir != null)
			if (dir.opposite() != game.snake().getDirection())
				game.snake().setDirection(dir);

		if (game.over() && e.getKeyChar() == ' ') {
			game.restart();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
}

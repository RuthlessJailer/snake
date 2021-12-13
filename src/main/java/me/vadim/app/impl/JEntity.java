package me.vadim.app.impl;

import me.vadim.app.Entity;

import java.awt.*;

/**
 * @author vadim
 */
public abstract class JEntity implements Entity {

	protected Rectangle bb;
	private boolean dead = false;

	public JEntity(Rectangle bb) {
		this.bb = new Rectangle(bb);
	}

	@Override
	public Rectangle bb() { return new Rectangle(bb); }

	@Override
	public void die() { this.dead = true; }

	@Override
	public boolean alive() { return !dead; }
}

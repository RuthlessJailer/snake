package me.vadim.app.impl;

import me.vadim.app.Apple;

import java.awt.*;

/**
 * @author vadim
 */
public class JApple extends JEntity implements Apple {

	public JApple(Point origin, int gridSize) {
		super(new Rectangle(origin.x, origin.y, gridSize, gridSize));
	}

	@Override
	public void render(Graphics2D g2d) {
		if(alive()){
			g2d.setColor(new Color(241, 31, 31));
			g2d.fill(bb());
			g2d.setColor(Color.RED);
			g2d.draw(bb());
		}
	}

}

package me.vadim.app;


import java.awt.*;

/**
 * @author vadim
 */
public interface Entity extends Renderable {

	Rectangle bb();

	void die();

	boolean alive();

}

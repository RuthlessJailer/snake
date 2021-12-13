package me.vadim.app.impl;

import me.vadim.app.*;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author vadim
 */
public class SnakeGame implements Game {

	public static final int GRID_SIZE = 30;
	public static final int WIDTH     = 20 * GRID_SIZE;
	public static final int HEIGHT    = 20 * GRID_SIZE;

	private Snake            snake;
	private Apple            apple;
	private CollisionManager collisionManager;
	private Timer[]          timers;

	protected final JFrame frame;

	/**
	 * <a href=https://stackoverflow.com/a/27740330/12344841>Source.</a><p>
	 *
	 * Draw a String centered in the middle of a Rectangle.
	 *
	 * @param g    The Graphics instance.
	 * @param text The String to draw.
	 * @param rect The Rectangle to center the text in.
	 *
	 * @author Daniel Kvist
	 */
	public static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
		// Get the FontMetrics
		FontMetrics metrics = g.getFontMetrics(font);
		// Determine the X coordinate for the text
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		// Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
		// Set the font
		g.setFont(font);
		// Draw the String
		g.drawString(text, x, y);
	}

	public SnakeGame() {
		frame = new JFrame("Snake");
		Container pane = frame.getContentPane();
		frame.setSize(WIDTH, HEIGHT);
		pane.setLayout(new BorderLayout());

		snake = new JSnake(GRID_SIZE);

		//attempt to force v-sync
//		ExtendedBufferCapabilities b = new ExtendedBufferCapabilities(
//				new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), BufferCapabilities.FlipContents.PRIOR),
//				ExtendedBufferCapabilities.VSyncType.VSYNC_ON);

		//try this next
//		GraphicsEnvironment.getLocalGraphicsEnvironment()
//						   .getDefaultScreenDevice()
//						   .getDefaultConfiguration()
//						   .getBufferCapabilities();

//		frame.createBufferStrategy(2, b);

		JPanel render = new JPanel() {
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(Color.BLACK);
				g2d.fillRect(0, 0, getWidth(), getHeight());

				// rendering magic will happen here
				g2d.setStroke(new BasicStroke(5));
				if (apple != null) apple.render(g2d);
				snake.render(g2d);
				if (!snake.alive()) end(g2d);
			}
		};
		pane.add(render, BorderLayout.CENTER);

		frame.addKeyListener(new WASDController(this));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		setCollisionManager(new ComponentCollisionManager(frame.getContentPane(), GRID_SIZE));
		generateApple();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		start();
		timers();
	}

	@Override
	public boolean over() { return !snake.alive(); }

	@Override
	public void restart() {
		snake = new JSnake(GRID_SIZE);
		start();
		timers();
	}

	void timers() {
		if (timers != null)
			for (Timer timer : timers)
				timer.stop();

		timers = new Timer[]{
				new Timer(1000 / 30, e -> frame.repaint()),//refresh rate
				new Timer(1000 / 10, e -> update())//speed at which snake moves
		};

		for (Timer timer : timers)
			timer.start();
	}

	void start() {
		snake.setCollisionManager(collisionManager);
		Dimension size = frame.getContentPane().getSize();
		((JSnake) snake).teleportHead((size.width / 2) - 3 * GRID_SIZE, size.height / 2);
		snake.setDirection(Direction.RIGHT);
		for (int i = 0; i < 2; i++)
			 snake.move(true);
		snake.setDirection(null);
	}

	void end(Graphics2D g2d) {
		Dimension size = frame.getContentPane().getSize();
		g2d.setColor(Color.CYAN);
		drawCenteredString(g2d, "Game Over!", new Rectangle(0, 0, size.width, size.height), new Font("Typescript", Font.PLAIN, 30));
		drawCenteredString(g2d, "press SPACE to restart", new Rectangle(0, 0, size.width, size.height + 60), new Font("Typescript", Font.PLAIN, 20));
	}

	@Override
	public Snake snake() { return this.snake; }

	@Override
	public Apple currentApple() { return apple; }

	void generateApple() {
		if (apple != null) apple.die();
		Point point;
		do {
			point = collisionManager.randomPointOnGrid();
		} while(snakeCollides(point));
		apple = new JApple(point, GRID_SIZE);
	}

	boolean snakeCollides(Point point){
		Rectangle bb = new Rectangle(point, new Dimension(GRID_SIZE, GRID_SIZE));
		for (Rectangle segment : snake.segments()) {
			if(collisionManager.collides(segment, bb)) return true;
		}
		return false;
	}

	@Override
	public void setCollisionManager(CollisionManager manager) {
		this.collisionManager = manager;
		snake.setCollisionManager(manager);
	}

	public ReentrantLock snakeLock = new ReentrantLock();
	void update() {
		//test for apple
		boolean ate = collisionManager.collides(snake.bb(), apple.bb());

		if (ate) {
			generateApple();
		}

		if (snake.alive()) snake.move(ate);
	}
}

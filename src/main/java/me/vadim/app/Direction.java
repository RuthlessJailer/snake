package me.vadim.app;

/**
 * @author vadim
 */
public enum Direction {
	UP('w', 0xff),//11111111
	DOWN('s', 0x00),//00000000
	LEFT('a', 0x0f),//11110000
	RIGHT('d', 0xf0);//00001111

	public final  char c;
	private final byte code;

	Direction(char c, int code) {
		this.c    = c;
		this.code = (byte) code;
	}

	public Direction opposite() {
		for (Direction value : values()) {
			if(value.code == ~code) return value;
		}
		throw new AssertionError("how");
	}

	public static Direction fromChar(char c) {
		for (Direction value : values()) {
			if (value.c == c) return value;
		}
		return null;
	}
}

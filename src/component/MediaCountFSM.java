package component;

public enum MediaCountFSM {
	ZERO, ONE, TWO, THREE, RESET;
	
	public static MediaCountFSM next(MediaCountFSM state) {
		switch (state) {
			case ZERO:
				return ONE;
			case ONE:
				return TWO;
			case TWO:
				return THREE;
			case THREE:
				return THREE;
			default:
				// input is reset.
				return ZERO;
		}
	}
}

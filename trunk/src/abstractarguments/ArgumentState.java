package abstractarguments;

public enum ArgumentState {
	/**
	 * The argument is part of all extensions (skeptical justified).
	 */
	JUSTIFIED,
	/**
	 * The argument is part of some (but not all) extensions (credulously justified).
	 */
	DEFENSIBLE,
	/**
	 * The argument can not be justified in any way.
	 */
	OVERRULED,
	/**
	 * The argument is missing.
	 */
	MISSING;
}

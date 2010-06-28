/**
 * 
 */
package abstractarguments;

/**
 * @author tberariu
 *
 */
public enum ExtensionBasedSemantics {
	/**
	 * A complete extension is a set which is able to defend itself
	 * and includes all arguments it defends.
	 */
	COMPLETE,
	/**
	 * Grounded semantics.
	 */
	GROUNDED,
	/**
	 * A stable extension is one that is able to attack all arguments not included in it.
	 */
	STABLE,
	/**
	 * Preferred semantics state that an extension is as large as possible and able to defend itself from attacks.
	 */
	PREFERRED;
}

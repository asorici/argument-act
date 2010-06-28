/**
 * WORK IN PROGRESS!
 * 1 TODO left
 * 6/28/2010 17:15 PM
 */
package abstractarguments;

/**
 * @author tberariu
 *
 */
public class TestArgumentSystem {

	private AbstractArgumentSystem<String> argSys;
	
	public TestArgumentSystem() {
		argSys = new ArgumentSystem<String>(ExtensionBasedSemantics.COMPLETE);
		// TODO Build some scenario step by step and test the outputs.
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestArgumentSystem t = new TestArgumentSystem();

	}

}

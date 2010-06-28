/**
 * WORK IN PROGRESS!
 * 1 TODO left
 * 6/28/2010 17:15 PM
 */
package abstractarguments;

import java.util.ArrayList;

/**
 * @author tberariu
 *
 */
public class TestArgumentSystem {

	private AbstractArgumentSystem<String> argSys;
	
	public TestArgumentSystem() {
		argSys = new ArgumentSystem<String>(ExtensionBasedSemantics.COMPLETE);
		
		System.out.println("Adding A...");
		argSys.assertArgument("A", new ArrayList<String>());
		
		System.out.println("Adding A -> B");
		argSys.challengeArgument("B", "A");
		System.out.println("Adding B -> C");
		argSys.challengeArgument("C", "B");
		System.out.println("Adding C -> B");
		argSys.challengeArgument("B", "C");
		System.out.println("Adding F -> B");
		argSys.challengeArgument("B", "F");
		System.out.println("Adding B -> E");
		argSys.challengeArgument("E", "B");
		System.out.println("Adding E -> F");
		argSys.challengeArgument("F", "E");
		System.out.println("Adding E -> D");
		argSys.challengeArgument("D", "E");
		System.out.println("Adding C -> D");
		argSys.challengeArgument("D", "C");
		System.out.println("Adding D -> G");
		argSys.challengeArgument("G", "D");
		System.out.println("Adding H -> G");
		argSys.challengeArgument("G", "H");
		System.out.println(argSys.getArgumentsToAttack("G").toString());
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestArgumentSystem t = new TestArgumentSystem();

	}

}

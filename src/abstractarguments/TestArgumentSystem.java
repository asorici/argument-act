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
		
		
		System.out.println("Adding A,B,C,F,E,D,G,H...");
		argSys.assertArgument("A", new ArrayList<String>());
		argSys.challengeArgument("B", "A");
		argSys.challengeArgument("C", "B");
		argSys.challengeArgument("B", "C");
		argSys.challengeArgument("B", "F");
		argSys.challengeArgument("F", "E");
		argSys.challengeArgument("E", "B");
		argSys.challengeArgument("D", "E");
		argSys.challengeArgument("D", "C");
		argSys.challengeArgument("G", "D");
		argSys.challengeArgument("G", "H");
		//Pana aici avem graful din figura before_attack_on_h.jpeg
		System.out.println("Current extensions are: (see before_attack_on_H.jpeg)");
		System.out.println(argSys.getExtensions().toString());
		//Sistemul ne recomanda sa atacm H ca sa il justificam pe G
		System.out.println("What should we attack in order to have G justified?");
		System.out.println(argSys.getArgumentsToAttack("G").toString());
		//Atacam H cu un nou argument I
		System.out.println("Adding I -> H...");
		argSys.challengeArgument("H", "I");
		//Noua extensie este cea din figura after_attack_on_h.jpeg
		System.out.println("The current complete extensions are: (see after_attack_on_h.jpeg)");
		System.out.println(argSys.getExtensions().toString());
		/*
		argSys.assertArgument("A", new ArrayList<String>());
		argSys.assertArgument("B", new ArrayList<String>());
		argSys.assertArgument("C", new ArrayList<String>());
		argSys.assertArgument("D", new ArrayList<String>());
		ArrayList<String> support = new ArrayList<String>();
		support.add("F");
		support.add("G");
		argSys.assertArgument("E", support);
		System.out.println("Adding C -> D");
		argSys.challengeArgument("D", "C");
		System.out.println("Adding D -> C");
		argSys.challengeArgument("C", "D");
		System.out.println("Adding D -> G");
		argSys.challengeArgument("G", "D");
		System.out.println(argSys.getExtensions().toString());
		System.out.println(argSys.getArgumentsToAttack("C").toString());
		*/
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		TestArgumentSystem t = new TestArgumentSystem();

	}

}

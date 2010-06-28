/**
 * WORK IN PROGRESS!!
 * 6 TODOs left
 * 6/28/2010 17:15 PM 
 */

package abstractarguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class ArgumentSystem<T> implements AbstractArgumentSystem<T>{
	/**
	 * The type of extension-based semantics used.
	 */
	private ExtensionBasedSemantics semanticsType;
	/**
	 * The set of known arguments.
	 */
	private ArrayList<T> arguments;
	/**
	 * The sets of arguments that support the argument (key).
	 */
	private HashMap<T,ArrayList<ArrayList<T>>> supports;
	/**
	 * The key attacks the list of arguments given by the value in the hash map.
	 */
	private HashMap<T,ArrayList<T>> attacks;
	/**
	 * The key is attacked by the arguments given by the value in the hash map.
	 */
	private HashMap<T,ArrayList<T>> attackedBy;
	/**
	 * The set of current consistent extensions. 
	 */
	private TreeSet<Extension<T>> extensions;
	
	/**
	 * Class constructor
	 * @param semanticsType The type of extension-based semantics used.
	 */
	public ArgumentSystem(ExtensionBasedSemantics semanticsType) {
		this.semanticsType = semanticsType;
		this.arguments = new ArrayList<T>();
		this.supports = new HashMap<T, ArrayList<ArrayList<T>>>();
		this.attacks = new HashMap<T, ArrayList<T>>();
		this.attackedBy = new HashMap<T, ArrayList<T>>();
	}

	@Override
	public void challengeArgument(T argument, T attack) {
		// If argument is not known ...
		// ... add argument to the set of known arguments.
		if (!arguments.contains(argument)) {
			assertArgument(argument, null);
		}
		// If attack argument is not known ...
		// ... add attack to the set of known arguments.
		if (!arguments.contains(attack)) {
			assertArgument(argument, null);
		}
		if (!attacks.containsKey(attack)) {
			attacks.put(attack, new ArrayList<T>());
		}
		if (!attacks.get(attack).contains(argument)) {
			attacks.get(attack).add(argument);
			if (supports.containsKey(argument)) {
				Iterator<ArrayList<T>> supportSetIterator = supports.get(argument).iterator();
				while (supportSetIterator.hasNext()) {
					Iterator<T> premiseIterator = supportSetIterator.next().iterator();
					while (premiseIterator.hasNext()) {
						challengeArgument(premiseIterator.next(), attack);
					}
				}
			}
		}
		if (!attackedBy.containsKey(argument)) {
			attackedBy.put(argument, new ArrayList<T>());
		}
		if (!attackedBy.get(argument).contains(attack)) {
			attackedBy.get(argument).add(attack);
			if (supports.containsKey(attack)) {
				Iterator<ArrayList<T>> supportSetIterator = supports.get(attack).iterator();
				while (supportSetIterator.hasNext()) {
					Iterator<T> premiseIterator = supportSetIterator.next().iterator();
					while (premiseIterator.hasNext()) {
						challengeArgument(argument,premiseIterator.next());
					}
				}
			}
		}
		
		updateExtensions(argument,attack);
	}

	@Override
	public Collection<T> getArgumentsToAttack(T goalArgument){
		if (getStateOfArgument(goalArgument) == ArgumentState.JUSTIFIED || getStateOfArgument(goalArgument) == ArgumentState.MISSING) {
			return null;
		}
		ArrayList<T> attackableArguments = new ArrayList<T>();
		Iterator<Extension<T>> extensionIterator = extensions.iterator();
		while (extensionIterator.hasNext()) {
			Extension<T> extension = extensionIterator.next();
			if (!extension.contains(goalArgument)) {
				// TODO Get lists of arguments that should be attacked to have the goal argument included in current extension.
			}
		}
		
		return null;
	}

	@Override
	public Collection<Collection<T>> getExtensions() {
		Collection<Collection<T>> serializedExtensions = new ArrayList<Collection<T>>();
		Iterator<Extension<T>> extensionIterator = extensions.iterator();
		while (extensionIterator.hasNext()) {
			serializedExtensions.add(extensionIterator.next().getArguments());
		}
		return serializedExtensions;
	}

	@Override
	public ArgumentState getStateOfArgument(T argument) {
		boolean existing = false, missing = false;
		Iterator<Extension<T>> extensionIterator = extensions.iterator();
		while(extensionIterator.hasNext()) {
			Extension<T> extension = extensionIterator.next();
			if (existing == false && extension.contains(argument)) {
				existing = true;
				if (missing) {
					break;
				}
			}
			if (missing == true && !extension.contains(argument)) {
				missing = true;
				if (existing) {
					break;
				}
			}
		}
		if (existing) {
			if (missing) {
				return ArgumentState.DEFENSIBLE;
			} else {
				return ArgumentState.JUSTIFIED;
			}
		} else {
			if (missing) {
				return ArgumentState.OVERRULED;
			} else {
				return ArgumentState.MISSING;
			}
		}
	}

	@Override
	public void assertArgument(T argument, Collection<T> premises) {
		if (!arguments.contains(argument)) {
			// If argument is not known
			// ... add argument to the set of known arguments.
			arguments.add(argument);
		}
		if (premises != null) {
			Iterator<T> premiseIterator = premises.iterator();
			while (premiseIterator.hasNext()) {
				T premise = premiseIterator.next();
				if (!arguments.contains(premise)) {
					// If premise is not known
					// ... add premise to the set of known arguments.
					arguments.add(premise);
				}
				if (attackedBy.containsKey(premise)) {
					// If the premise is attacked by some argument ...
					// ... that argument also attacks the conclusion.
					Iterator<T> attackerIterator = attackedBy.get(premise).iterator();
					while (attackerIterator.hasNext()) {
						challengeArgument(argument, attackerIterator.next());
					}
				}
				if (attacks.containsKey(premise)) {
					// If the premise attacks some argument ...
					// ... that argument is also attacked by the conclusion.
					Iterator<T> attackedIterator = attacks.get(premise).iterator();
					while (attackedIterator.hasNext()) {
						challengeArgument(attackedIterator.next(),argument);
					}
				}
			}
			if (!supports.containsKey(argument)) {
				supports.put(argument, new ArrayList<ArrayList<T>>());
			}
			boolean smallerSupportSetExists = false;
			ArrayList<ArrayList<T>> existingSupportSets = new ArrayList<ArrayList<T>>();
			ArrayList<T> existingSupportSet;
			Iterator<ArrayList<T>> supportSetIterator = supports.get(argument).iterator();
			while (supportSetIterator.hasNext()) {
				existingSupportSet = supportSetIterator.next();
				if (existingSupportSet.containsAll(premises)) {
					// If a set of premises that includes the current set exists
					// ... add it to existingSupportSets.
					existingSupportSets.add(existingSupportSet);
				}
				if (premises.containsAll(existingSupportSet) && premises.size() > existingSupportSet.size()) {
					// If a smaller support set that is a subset of the current set of premises exists
					// ... there is no use in adding the current support set.
					smallerSupportSetExists = true;
					break;
				}
			}
			if (smallerSupportSetExists == false) {
				// If a smaller support set does not exist,
				// ... discard all larger support sets
				// ... and add the current set of premises as a support set.
				supports.get(argument).removeAll(existingSupportSets);
				supports.get(argument).add(new ArrayList<T>(premises));
			}
		}
		updateExtensions(argument);
	}
	
	private void updateExtensions(T newArgument) {
		Iterator<Extension<T>> extensionIterator = extensions.iterator();
		while (extensionIterator.hasNext()) {
			extensionIterator.next().addArgument(newArgument);
		}
	}
	
	private void updateExtensions(T argument, T attacker) {
		// TODO Updates extensions set after a new argument is asserted.
	}
	
	public void drawGraph(String fileName) {
		// TODO Draw a graph of the current
	}
	
	public void drawGraphWithHints(String fileName, T goalArgument) {
		// TODO Draw a graph of the current set of arguments that should be attacked to get the goal argument justified.
	}
	
	public void drawExtensions(String rootFileName) {
		// TODO Draw the graph, emphasizing each possible extension.
	}
}

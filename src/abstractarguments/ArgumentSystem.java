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

public class ArgumentSystem<T> implements AbstractArgumentSystem<T> {
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
	private HashMap<T, ArrayList<ArrayList<T>>> supports;
	/**
	 * The key attacks the list of arguments given by the value in the hash map.
	 */
	private HashMap<T, ArrayList<T>> attacks;
	/**
	 * The key is attacked by the arguments given by the value in the hash map.
	 */
	private HashMap<T, ArrayList<T>> attackedBy;
	/**
	 * The set of current consistent extensions.
	 */
	private TreeSet<Extension<T>> extensions;

	/**
	 * Class constructor
	 * 
	 * @param semanticsType
	 *            The type of extension-based semantics used.
	 */
	public ArgumentSystem(ExtensionBasedSemantics semanticsType) {
		this.semanticsType = semanticsType;
		/**
		 * JUST FOR NOW!!
		 */
		this.semanticsType = ExtensionBasedSemantics.COMPLETE;
		this.arguments = new ArrayList<T>();
		this.supports = new HashMap<T, ArrayList<ArrayList<T>>>();
		this.attacks = new HashMap<T, ArrayList<T>>();
		this.attackedBy = new HashMap<T, ArrayList<T>>();
		this.extensions = new TreeSet<Extension<T>>();
		this.extensions.add(new Extension<T>());
		// DEBUG
		System.out.println("System up");
	}

	@Override
	public void challengeArgument(T argument, T attack) {
		// DEBUG
		System.out.println("::challengeArgument");
		// If argument is not known ...
		// ... add argument to the set of known arguments.
		if (!arguments.contains(argument)) {
			assertArgument(argument, null);
		}
		// If attack argument is not known ...
		// ... add attack to the set of known arguments.
		if (!arguments.contains(attack)) {
			assertArgument(attack, null);
		}
		if (!attacks.containsKey(attack)) {
			attacks.put(attack, new ArrayList<T>());
		}
		if (!attacks.get(attack).contains(argument)) {
			attacks.get(attack).add(argument);
			if (supports.containsKey(argument)) {
				Iterator<ArrayList<T>> supportSetIterator = supports.get(
						argument).iterator();
				while (supportSetIterator.hasNext()) {
					Iterator<T> premiseIterator = supportSetIterator.next()
							.iterator();
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
				Iterator<ArrayList<T>> supportSetIterator = supports
						.get(attack).iterator();
				while (supportSetIterator.hasNext()) {
					Iterator<T> premiseIterator = supportSetIterator.next()
							.iterator();
					while (premiseIterator.hasNext()) {
						challengeArgument(argument, premiseIterator.next());
					}
				}
			}
		}
		
		updateExtensions(argument, attack);
		// DEBUG
		// System.out.println(argument.toString() + " attacked by " + attack.toString());
	}

	@Override
	public Collection<T> getArgumentsToAttack(T goalArgument) {
		// DEBUG
		// System.out.println("::getArgumentsToAttack");
		if (getStateOfArgument(goalArgument) == ArgumentState.JUSTIFIED
				|| getStateOfArgument(goalArgument) == ArgumentState.MISSING) {
			return new ArrayList<T>();
		}
		if (semanticsType == ExtensionBasedSemantics.COMPLETE) {
			return getArgumentsToAttackInCompleteExtensions(goalArgument);
		}
		ArrayList<T> attackableArguments = new ArrayList<T>();
		Iterator<Extension<T>> extensionIterator = extensions.iterator();
		while (extensionIterator.hasNext()) {
			Extension<T> extension = extensionIterator.next();
			if (!extension.contains(goalArgument)) {
				// TODO Get lists of arguments that should be attacked to have
				// the goal argument included in current extension.
				switch (semanticsType) {
				case GROUNDED:
					break;
				case PREFERRED:
					break;
				case STABLE:
					break;
				}

			}
		}

		return attackableArguments;
	}

	private Collection<T> getArgumentsToAttackInCompleteExtensions(
			T goalArgument) {
		// DEBUG
		// System.out.println("::getArgumentsToAttackInCompleteExtensions");
		ArrayList<T> goodArgs = new ArrayList<T>();
		ArrayList<T> badArgs = new ArrayList<T>();
		ArrayList<T> undecidedArgs = new ArrayList<T>(arguments);
		undecidedArgs.remove(goalArgument);
		goodArgs.add(goalArgument);
		boolean newNodesAdded = true;
		while (!undecidedArgs.isEmpty() && newNodesAdded) {
			newNodesAdded = false;
			for (T arg : goodArgs) {
				if (getStateOfArgument(arg) != ArgumentState.JUSTIFIED) {
					if (attacks.containsKey(arg)) {
						for (T attackedArg : attacks.get(arg)) {
							if (undecidedArgs.contains(attackedArg)) {
								badArgs.add(attackedArg);
								undecidedArgs.remove(attackedArg);
								newNodesAdded = true;
							}
						}
					}
					if (attackedBy.containsKey(arg)) {
						for (T attackerArg : attackedBy.get(arg)) {
							if (undecidedArgs.contains(attackerArg)) {
								badArgs.add(attackerArg);
								undecidedArgs.remove(attackerArg);
								newNodesAdded = true;
							}
						}
					}
				}
			}
			for (T arg : badArgs) {
				if (attacks.containsKey(arg)) {
					for (T attackedArg : attacks.get(arg)) {
						if (undecidedArgs.contains(attackedArg)) {
							goodArgs.add(attackedArg);
							undecidedArgs.remove(attackedArg);
							newNodesAdded = true;
						}
					}
				}
				if (attackedBy.containsKey(arg)) {
					for (T attackerArg : attackedBy.get(arg)) {
						if (undecidedArgs.contains(attackerArg)) {
							goodArgs.add(attackerArg);
							undecidedArgs.remove(attackerArg);
							newNodesAdded = true;
						}
					}
				}
			}
		}
		return badArgs;
	}

	@Override
	public Collection<Collection<T>> getExtensions() {
		// DEBUG
		// System.out.println("::getExtensions");
		Collection<Collection<T>> serializedExtensions = new ArrayList<Collection<T>>();
		Iterator<Extension<T>> extensionIterator = extensions.iterator();
		while (extensionIterator.hasNext()) {
			serializedExtensions.add(extensionIterator.next().getArguments());
		}
		return serializedExtensions;
	}

	@Override
	public ArgumentState getStateOfArgument(T argument) {
		// DEBUG
		// System.out.println("::getStateOfArgument(" + argument.toString() + ")");
		boolean existing = false, missing = false;
		Iterator<Extension<T>> extensionIterator = extensions.iterator();
		while (extensionIterator.hasNext()) {
			Extension<T> extension = extensionIterator.next();
			if (existing == false && extension.contains(argument)) {
				existing = true;
				if (missing) {
					break;
				}
			}
			if (missing == false && !extension.contains(argument)) {
				missing = true;
				if (existing) {
					break;
				}
			}
		}
		if (existing) {
			if (missing) {
				// DEBUG
				// System.out.println("DEFENSIBLE");
				return ArgumentState.DEFENSIBLE;
			} else {
				// DEBUG
				// System.out.println("JUSTIFIED");
				return ArgumentState.JUSTIFIED;
			}
		} else {
			if (missing) {
				// DEBUG
				// System.out.println("OVERRULED");
				return ArgumentState.OVERRULED;
			} else {
				// DEBUG
				// System.out.println("MISSING");
				return ArgumentState.MISSING;
			}
		}
	}

	@Override
	public void assertArgument(T argument, Collection<T> premises) {
		// DEBUG
		/*
		if (premises == null) {
			System.out.println("::assertArgument (" + argument.toString() + ", null)");
		} else {
			System.out.println("::assertArgument (" + argument.toString() + ", " + premises.toString() +")");
		}
		*/
		if (!arguments.contains(argument)) {
			// If argument is not known
			// ... add argument to the set of known arguments.
			arguments.add(argument);
			// DEBUG
			// System.out.println(argument.toString() + " added to system!");
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
					Iterator<T> attackerIterator = attackedBy.get(premise)
							.iterator();
					while (attackerIterator.hasNext()) {
						challengeArgument(argument, attackerIterator.next());
					}
				}
				if (attacks.containsKey(premise)) {
					// If the premise attacks some argument ...
					// ... that argument is also attacked by the conclusion.
					Iterator<T> attackedIterator = attacks.get(premise)
							.iterator();
					while (attackedIterator.hasNext()) {
						challengeArgument(attackedIterator.next(), argument);
					}
				}
			}
			if (!supports.containsKey(argument)) {
				supports.put(argument, new ArrayList<ArrayList<T>>());
			}
			boolean smallerSupportSetExists = false;
			ArrayList<ArrayList<T>> existingSupportSets = new ArrayList<ArrayList<T>>();
			ArrayList<T> existingSupportSet;
			Iterator<ArrayList<T>> supportSetIterator = supports.get(argument)
					.iterator();
			while (supportSetIterator.hasNext()) {
				existingSupportSet = supportSetIterator.next();
				if (existingSupportSet.containsAll(premises)) {
					// If a set of premises that includes the current set exists
					// ... add it to existingSupportSets.
					existingSupportSets.add(existingSupportSet);
				}
				if (premises.containsAll(existingSupportSet)
						&& premises.size() > existingSupportSet.size()) {
					// If a smaller support set that is a subset of the current
					// set of premises exists
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
		// DEBUG
		/*
		if (premises != null && premises.isEmpty() == false) {
			System.out.println(argument.toString() + " supported by " + premises.toString());
		}
		System.out.println("Known arguments are now: " + arguments.toString());
		*/
	}

	private void updateExtensions(T newArgument) {
		// DEBUG
		// System.out.println("::updateExtensions");
		Iterator<Extension<T>> extensionIterator = extensions.iterator();
		while (extensionIterator.hasNext()) {
			extensionIterator.next().addArgument(newArgument);
		}
		// DEBUG
		/* System.out.println("Extensions updated on new argument! Extensions are:");
		for (Extension<T> extension : extensions) {
			System.out.println(extension.toString());
		}
		System.out.println(".");
		*/
	}

	private void updateExtensions(T argument, T attacker) {
		// DEBUG
		// System.out.println("::updateExtensions");
		// TODO Updates extensions set after a new attack relation is asserted.
		switch (semanticsType) {
		case COMPLETE:
			/**
			 * Not to compute all extensions is hard because adding an attack
			 * relation between nodes that are not part of an extension might
			 * cause adding a large set of nodes to that extension.
			 */
			this.extensions = recomputeCompleteExtensions();
			break;
		case GROUNDED:
			break;
		case PREFERRED:
			break;
		case STABLE:
			break;
		}
		// DEBUG
		/*
		System.out.println("Extensions updated on new argument! Extensions are:");
		for (Extension<T> extension : extensions) {
			System.out.println(extension.toString());
		}
		System.out.println(".");
		*/
	}

	private TreeSet<Extension<T>> recomputeCompleteExtensions() {
		// DEBUG
		// System.out.println("::recomputeCompleteExtensions");
		ArrayList<T> unattackedNodes = getUnattackedNodes();
		ArrayList<T> outOfExtensionNodes = getNodesAttackedBy(unattackedNodes);
		ArrayList<T> undecidedNodes = new ArrayList<T>(arguments);
		undecidedNodes.removeAll(unattackedNodes);
		undecidedNodes.removeAll(outOfExtensionNodes);
		ArrayList<Extension<T>> possibleExtensions = getPossibleExtensions(
				unattackedNodes, outOfExtensionNodes, undecidedNodes);
		ArrayList<Extension<T>> finalSetOfExtensions = new ArrayList<Extension<T>>();
		Iterator<Extension<T>> extensionIterator = possibleExtensions
				.iterator();
		while (extensionIterator.hasNext()) {
			boolean toBeAdded = true;
			Extension<T> e = extensionIterator.next();
			Iterator<Extension<T>> alreadyIn = finalSetOfExtensions.iterator();
			ArrayList<Extension<T>> toBeRemoved = new ArrayList<Extension<T>>();
			while (alreadyIn.hasNext()) {
				Extension<T> alreadyInExtension = alreadyIn.next();
				if (e.subExtensionOf(alreadyInExtension)) {
					toBeAdded = false;
					break;
				}
				if (alreadyInExtension.subExtensionOf(e)) {
					toBeRemoved.add(alreadyInExtension);
				}
			}
			if (toBeAdded) {
				finalSetOfExtensions.removeAll(toBeRemoved);
				finalSetOfExtensions.add(e);
			}
		}
		return new TreeSet<Extension<T>>(finalSetOfExtensions);
	}

	private ArrayList<Extension<T>> getPossibleExtensions(ArrayList<T> inNodes,
			ArrayList<T> outNodes, ArrayList<T> undecidedNodes) {
		// DEBUG
		// System.out.println("::getPossibleExtensions");
		ArrayList<Extension<T>> a = new ArrayList<Extension<T>>();
		if (undecidedNodes.isEmpty()) {
			if (validCompleteExtension(inNodes, outNodes)) {
				a.add(new Extension<T>(inNodes));
			}
		} else {
			ArrayList<T> newUndecidedNodes = new ArrayList<T>(undecidedNodes);
			T candidate = newUndecidedNodes.remove(0);
			ArrayList<T> inNodes1 = new ArrayList<T>(inNodes);
			ArrayList<T> inNodes2 = new ArrayList<T>(inNodes);
			ArrayList<T> outNodes1 = new ArrayList<T>(outNodes);
			ArrayList<T> outNodes2 = new ArrayList<T>(outNodes);
			ArrayList<T> undecidedNodes1 = new ArrayList<T>(newUndecidedNodes);
			ArrayList<T> undecidedNodes2 = new ArrayList<T>(newUndecidedNodes);

			inNodes1.add(candidate);
			if (attacks.containsKey(candidate)) {
				outNodes1.addAll(attacks.get(candidate));
				undecidedNodes.removeAll(attacks.get(candidate));
			}
			if (attackedBy.containsKey(candidate)) {
				outNodes1.addAll(attackedBy.get(candidate));
				undecidedNodes.removeAll(attackedBy.get(candidate));
			}
			outNodes2.add(candidate);
			a
					.addAll(getPossibleExtensions(inNodes1, outNodes1,
							undecidedNodes1));
			a
					.addAll(getPossibleExtensions(inNodes2, outNodes2,
							undecidedNodes2));
		}
		return a;
	}

	private boolean validCompleteExtension(ArrayList<T> inNodes,
			ArrayList<T> outNodes) {
		// DEBUG
		// System.out.println("::validCompleteExtension");
		/**
		 * We know that there are no two nodes in the inNodes that attack each
		 * other, so we will check just if all arguments are defended.
		 */
		Iterator<T> inNodeIterator = inNodes.iterator();
		while (inNodeIterator.hasNext()) {
			/**
			 * Check if each node in the extension is acceptable.
			 */
			T inNode = inNodeIterator.next();
			if (attackedBy.containsKey(inNode)) {
				Iterator<T> attackerIterator = attackedBy.get(inNode)
						.iterator();
				while (attackerIterator.hasNext()) {
					/**
					 * Check if each attacker of the argument is attacked from
					 * inside the extension.
					 */
					T attacker = attackerIterator.next();
					boolean defended = false;
					Iterator<T> defenderIterator = inNodes.iterator();
					while (defenderIterator.hasNext()) {
						if (attackedBy.containsKey(attacker)) {
							if (attackedBy.get(attacker).contains(
									defenderIterator.next())) {
								defended = true;
								break;
							}
						}
					}
					if (defended == false)
						return false;
				}
			}
		}
		return true;
	}

	private ArrayList<T> getNodesAttackedBy(ArrayList<T> unattackedNodes) {
		// DEBUG
		// System.out.println("::getNodesAttackedBy");
		ArrayList<T> attackedNodes = new ArrayList<T>();
		Iterator<T> unattackedNodeIterator = unattackedNodes.iterator();
		while (unattackedNodeIterator.hasNext()) {
			T unattackedNode = unattackedNodeIterator.next();
			if (attacks.containsKey(unattackedNode)) {
				Iterator<T> i = attacks.get(unattackedNode).iterator();
				while (i.hasNext()) {
					T a = i.next();
					if (!attackedNodes.contains(a)) {
						attackedNodes.add(a);
					}
				}
			}
		}
		return attackedNodes;
	}

	private ArrayList<T> getUnattackedNodes() {
		// DEBUG
		// System.out.println("::getUnattackedNodes");
		ArrayList<T> unattackedNodes = new ArrayList<T>();
		for (T argument : arguments) {
			if (attackedBy.containsKey(argument)) {
				if (attackedBy.get(argument).isEmpty()) {
					unattackedNodes.add(argument);
				}
			} else {
				unattackedNodes.add(argument);
			}
		}
		return unattackedNodes;
	}

	public void drawGraph(String fileName) {
		// TODO Draw a graph of the current
	}

	public void drawGraphWithHints(String fileName, T goalArgument) {
		// TODO Draw a graph of the current set of arguments that should be
		// attacked to get the goal argument justified.
	}

	public void drawExtensions(String rootFileName) {
		// TODO Draw the graph, emphasizing each possible extensions in separate
		// files.
	}
}

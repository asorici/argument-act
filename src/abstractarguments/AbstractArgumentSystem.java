package abstractarguments;

import java.util.Collection;

public interface AbstractArgumentSystem<T> {
	/**
	 * Adds argument to the set of known arguments or / and adds the premises as a support set for the argument.
	 * @param argument The argument to be added or for which you add a support set.
	 * @param premises The set of premises that build a support set. null for just adding argument without any support set.
	 */
	public void assertArgument(T argument, Collection<T> premises);
	/**
	 * Adds an attack relation between two arguments.
	 * @param argument The argument that is attacked.
	 * @param attack The argument that attacks.
	 */
	public void challengeArgument(T argument, T attack);
	/**
	 * Gets the state of an argument T. 
	 * @param argument The argument for which you want to find out the state.
	 * @return an <code>ArgumentState</code> object.
	 */
	public ArgumentState getStateOfArgument(T argument);
	/**
	 * Gets a list of arguments that should be attacked in order to have a justified goal <code>goalArgument</code>.
	 * @param goalArgument The argument that is the main goal.
	 * @return A <code>Collection</code> of arguments to attack.
	 */
	public Collection<T> getArgumentsToAttack(T goalArgument);
	/**
	 * Gets the current set of valid extensions under the considered extension-based semantic.
	 * @return
	 */
	public Collection<Collection<T>> getExtensions();
}

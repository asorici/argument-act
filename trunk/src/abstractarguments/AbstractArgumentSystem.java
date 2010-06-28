package abstractarguments;

import java.util.Collection;

public interface AbstractArgumentSystem<T> {
	public void assertArgument(T argument, Collection<T> premises);
	public void challengeArgument(T argument, T attack);
	public ArgumentState getStateOfArgument(T argument);
	public Collection<T> getArgumentsToAttack(T goalArgument);
	public Collection<Collection<T>> getExtensions();
}

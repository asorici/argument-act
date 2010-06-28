package abstractarguments;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class Extension<T> {
	TreeSet<T> arguments;
	
	public Extension() {
		arguments = new TreeSet<T>();
	}
	
	public Extension(Set<T> initialArguments) {
		arguments = new TreeSet<T>();
		arguments.addAll(initialArguments);
	}
	
	public void addArgument(T newArgument) {
		if (!arguments.contains(newArgument)) {
			arguments.add(newArgument);
		}
	}
	public boolean contains(T argument) {
		return arguments.contains(argument);
	}
	
	public Collection<T> getArguments() {
		return arguments;
	}
}

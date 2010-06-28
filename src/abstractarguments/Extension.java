package abstractarguments;

import java.util.Collection;
import java.util.TreeSet;

public class Extension<T> {
	TreeSet<T> arguments;
	
	public Extension() {
		arguments = new TreeSet<T>();
	}
	
	public Extension(Collection<T> initialArguments) {
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
	
	public boolean subExtensionOf(Extension<T> e) {
		return e.getArguments().containsAll(this.getArguments());
	}
	
	public String toString() {
		return arguments.toString();
	}
}

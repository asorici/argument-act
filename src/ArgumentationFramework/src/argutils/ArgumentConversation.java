package argutils;

import java.util.ArrayList;
import java.util.List;

import onto.Conclusion;
import onto.I_Node;
import onto.S_Node;
import argproto.SimpleArgumentStructure;

public class ArgumentConversation {
	public static final int NOT_STARTED = 0;
	public static final int RUNNING = 1;
	public static final int ENDED = 2;
	public static final int BROKEN = 3;
	
	private int status;
	private ArgumentationGraph argGraph; 
	
	public ArgumentConversation(){
		status = RUNNING;
		argGraph = new ArgumentationGraph();
	}
	
	public ArgumentConversation(int status, ArgumentationGraph argGraph) {
		this.argGraph = argGraph;
		this.status = status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setArgGraph(ArgumentationGraph argGraph) {
		this.argGraph = argGraph;
	}

	public ArgumentationGraph getArgGraph() {
		return argGraph;
	}	
	
}
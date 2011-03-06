package argutils;

import jade.content.ContentElement;

public class ArgumentationMessage {
	public static final int ARG_ACCEPT = 1001;
	public static final int ARG_CHALLENGE = 1002;
	public static final int ARG_ASSERT = 1003;
	public static final int ARG_INFORM = 1004;
	public static final int ARG_QUESTION = 1005;
	public static final int ARG_START_CONV = 1006;
	public static final int ARG_ACK_CONV = 1007;
	public static final int ARG_END_CONV = 1008;
	public static final int ARG_REFUSE_CONV = 1009;
	public static final int ARG_ACCEPT_END_CONV = 1010;
	
	public static final int ARG_IN_SEQ = 2001;
	public static final int ARG_OUT_OF_SEQ = 2002;
	
	private ContentElement ontologyObject;
	private String objectTypeName;
	
}

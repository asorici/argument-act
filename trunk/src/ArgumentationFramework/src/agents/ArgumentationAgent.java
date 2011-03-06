package agents;

import jade.content.ContentElement;
import jade.content.abs.AbsContentElement;
import jade.content.lang.Codec;
import jade.content.lang.StringCodec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ObjectSchema;
import jade.core.Agent;

import java.util.HashMap;
import java.util.Map;

import argutils.ArgumentConversation;

import onto.AIFOntology;

@SuppressWarnings("serial")
public class ArgumentationAgent extends Agent {
	private Codec commCodec = new SLCodec();
	private Ontology commOntology = AIFOntology.getInstance();
	
	private Map<String, ArgumentConversation> conversationRecords = new HashMap<String, ArgumentConversation>();
	private Map<String, Codec> spokenLanguages = new HashMap<String, Codec>();
	private Map<String, Ontology> knownOntologies = new HashMap<String, Ontology>();
	
	@Override
	protected final void setup() {
		System.out.println("Hello! Argumentation-agent " + getAID().getName() + " is ready.");
		
		getContentManager().registerLanguage(commCodec);
		getContentManager().registerOntology(commOntology);
	
		initArgumentationAgent();
	}
	
	protected void initArgumentationAgent() {
		/*
		String agentType = "con";
		
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			agentType = (String)args[0];
		}
		
		System.out.println(agentType);
		*/
	}
	
	@Override
	protected void takeDown() {
		System.out.println("Argumentation-agent " + getAID().getName() + " terminating.");
	}

	public void setConversationRecords(Map<String, ArgumentConversation> conversationRecords) {
		this.conversationRecords = conversationRecords;
	}

	public Map<String, ArgumentConversation> getConversationRecords() {
		return conversationRecords;
	}
	
	//TODO - incepe o noua conversatie
	
	/**
	 * Registers a <code>Codec</code> for a given content language 
	 * with its default name (i.e.
	 * the name returned by its <code>getName()</code> method.
	 * Since this operation is performed the agent is able to "speak" the language
	 * corresponding to the registered <code>Codec</code>.
	 * @param c the <code>Codec</code> to be registered.
	 */
	public void registerLanguage(Codec c) {
		getContentManager().registerLanguage(c);
	}
	
	/**
	 * Registers a <code>Codec</code> for a given content language 
	 * with a given name.
	 * @param c the <code>Codec</code> to be registered.
	 * @param name the name associated to the registered codec.
	 */
	public void registerLanguage(Codec c, String name) {
		getContentManager().registerLanguage(c, name);
	}
	
	/**
	 * Encodes a domain ontology element using the specified codec and ontology.
	 * @param content The domain ontology element  
	 * @param codec The used codec
	 * @param ontology The used ontology
	 * @return The string representation of the encoded domain ontology element according to the given codec syntax
	 * @throws CodecException
	 * @throws OntologyException
	 */
	public static String encodeToString(ContentElement content, Codec codec, Ontology ontology) throws CodecException, OntologyException {
		String encoded = null;
		Ontology mergedOnto = getMergedOntology(codec, ontology); 
		AbsContentElement abs = (AbsContentElement) mergedOnto.fromObject(content);
		
		validate(abs, mergedOnto);
		encoded = ((StringCodec)codec).encode(mergedOnto, abs);
		
		return encoded;
	}
	
	/**
	 * Decodes a text content from a received message into the appropiate domain ontology element
	 * @param textContent The encoded domain ontology element according to the given codec syntax
	 * @param codec The used codec
	 * @param ontology The used ontology
	 * @return The decoded domain ontology element as parsed from the received content string
	 * @throws CodecException
	 * @throws OntologyException
	 */
	public static ContentElement decodeFromString(String textContent, Codec codec, Ontology ontology) throws CodecException, OntologyException {
		ContentElement decoded = null;
		
		Ontology mergedOnto  = getMergedOntology(codec, ontology);
		AbsContentElement abs = ((StringCodec) codec).decode(mergedOnto, textContent);
		
		validate(abs, mergedOnto);
		decoded = (ContentElement)mergedOnto.toObject(abs);
		
		return decoded;
	}
	
	/**
	 * Decodes a text content from a received message into the appropiate domain ontology element by trying
	 * each combination of known codecs and ontologies
	 * @param textContent The encoded domain ontology element according to the given codec syntax
	 * @return The decoded domain ontology element as parsed from the received content string
	 */
	public static ContentElement decodeFromString(String textContent, Map<String, Codec> spokenLanguages, Map<String, Ontology> knownOntologies) {
		for (Codec codec : spokenLanguages.values()) {
			for (Ontology ontology : knownOntologies.values()) {
				// try until we get one combination right
				try {
					ContentElement decoded = decodeFromString(textContent, codec, ontology);
					if (decoded != null) {
						return decoded;
					}
				} catch (CodecException e) {
					continue;
				} catch (OntologyException e) {
					continue;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Merge the reference ontology with the inner ontology of the 
	 * content language
	 */
	private static Ontology getMergedOntology(Codec c, Ontology o) {
		Ontology ontology = null;
		Ontology langOnto = c.getInnerOntology();
		if (langOnto == null) {
			ontology = o;
		}
		else if (o == null) {
			ontology = langOnto;
		}
		else {
			ontology = new Ontology(null, new Ontology[]{o, langOnto}, null);
		}
		return ontology;
	}
	
	private static void validate(AbsContentElement content, Ontology onto) throws OntologyException { 
		// Validate the content against the ontology
		ObjectSchema schema = onto.getSchema(content.getTypeName());
		if (schema == null) {
			throw new OntologyException("No schema found for type " + content.getTypeName());
		}
		schema.validate(content, onto);		
	}
}

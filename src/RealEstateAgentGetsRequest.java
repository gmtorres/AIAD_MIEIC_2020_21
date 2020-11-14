import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class RealEstateAgentGetsRequest extends AchieveREResponder {
	
	RealEstateAgent REagent;
	
	public RealEstateAgentGetsRequest(RealEstateAgent a, MessageTemplate mt) {
		super(a, mt);
		this.REagent = a;
	}
	
	protected ACLMessage handleRequest(ACLMessage request) {
		ACLMessage reply = request.createReply();
		reply.setPerformative(ACLMessage.AGREE);
		registerPrepareResultNotification(new RealEstateAgentGetSellers(this.REagent,new ACLMessage(ACLMessage.REQUEST),request));
		reply.setContent("Agent Processing Request");
		return reply;
	}
	
}
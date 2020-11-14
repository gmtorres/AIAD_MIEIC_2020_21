package behaviours;
import agents.RealEstateAgency;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class AgencyGetsRequest extends AchieveREResponder {
	
	RealEstateAgency agency;
	
	public AgencyGetsRequest(RealEstateAgency a, MessageTemplate mt) {
		super(a, mt);
		this.agency = a;
	}
	
	protected ACLMessage handleRequest(ACLMessage request) {
		//System.out.println(request);
		ACLMessage reply = request.createReply();
		reply.setPerformative(ACLMessage.AGREE);
		registerPrepareResultNotification(new AgencyRequestsAgent(this.agency,new ACLMessage(ACLMessage.REQUEST),request));
		reply.setContent("Agency Processing Request");
		return reply;
	}
	
	
	

}

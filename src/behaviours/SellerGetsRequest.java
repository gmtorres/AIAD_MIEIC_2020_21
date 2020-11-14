package behaviours;
import agents.Seller;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class SellerGetsRequest extends AchieveREResponder{
	
	Seller seller;
	
	public SellerGetsRequest(Seller a, MessageTemplate mt) {
		super(a, mt);
		this.seller = a;
	}
	
	protected ACLMessage handleRequest(ACLMessage request) {
		ACLMessage reply = request.createReply();
		reply.setPerformative(ACLMessage.AGREE);
		return reply;
	}
	
	protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
		ACLMessage reply = request.createReply();
		reply.setPerformative(ACLMessage.INFORM);
		if(this.seller.getProperty() != null)
			reply.setContent(this.seller.getProperty().toString());
		return reply;
	}
	
}

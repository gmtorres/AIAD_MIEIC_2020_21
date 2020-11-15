package behaviours;
import java.io.IOException;

import agents.Seller;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

public class SellerRespondBuyer extends ContractNetResponder{
	Seller seller;
	SellerRespondBuyer(Seller seller, MessageTemplate mt){
		super(seller,mt);
		this.seller = seller;
	}
	protected ACLMessage handleCfp(ACLMessage cfp) {
		ACLMessage reply = cfp.createReply();
		
		if(seller.getProperty() == null) {
			reply.setPerformative(ACLMessage.REFUSE);
			return reply;
		}
		System.out.println(cfp);
		reply.setPerformative(ACLMessage.PROPOSE);
		
		reply.setContent(Integer.toString(this.seller.getProperty().getPrice()));
		
		return reply;
	}
	
	protected void handleOutOfSequence(ACLMessage msg) {
		System.out.println("Seller received out of sequence");
	}
	
	protected ACLMessage handleAcceptProposal(ACLMessage cfp,ACLMessage propose,ACLMessage accept) throws FailureException{
		
		Integer price_payed = Integer.parseInt(propose.getContent());
		
		ACLMessage reply = accept.createReply();
		reply.setPerformative(ACLMessage.INFORM);
		reply.setContent(propose.getContent());

		this.seller.setProperty(null);
		this.seller.increaseMoney(price_payed);
		//System.out.println("Eu, " + this.seller.getLocalName() + ", fiquei com " + this.seller.getMoney() + "�");
		return reply;
	}
}

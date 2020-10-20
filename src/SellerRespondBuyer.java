import java.io.IOException;

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
		System.out.println("Recebi pedido de compra!");
		System.out.println(cfp);
		ACLMessage reply = cfp.createReply();
		
		if(seller.property == null) {
			reply.setPerformative(ACLMessage.REFUSE);
			return reply;
		}
		
		reply.setPerformative(ACLMessage.PROPOSE);
		
		reply.setContent(Integer.toString(this.seller.property.getPrice()));
		
		return reply;
	}
	
	protected void handleOutOfSequence(ACLMessage msg) {
		
		System.out.println("Seller received out of sequence");
	}
}

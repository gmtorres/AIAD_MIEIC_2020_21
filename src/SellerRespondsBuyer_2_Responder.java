import java.util.Random;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.proto.SSContractNetResponder;

public class SellerRespondsBuyer_2_Responder extends SSContractNetResponder{
	
	Seller seller;
	Integer interactions;
	Integer min_value, max_value;
	
	static Random rnd;
	static {
		rnd = new Random();
	}
	
	
	public SellerRespondsBuyer_2_Responder(Seller seller, ACLMessage cfp) {
		super(seller, cfp);
		this.seller = seller;
		this.interactions = 0;
		this.min_value = this.seller.getPriceFromRelativeDifference(0.9); // Minimum value
		this.max_value = this.seller.getPriceFromRelativeDifference(1.1); // Max value
	}
	
	protected ACLMessage handleCfp(ACLMessage cfp) {
		//System.out.println("Recebi pedido de compra!");
		//System.out.println(cfp);
		ACLMessage reply = cfp.createReply();
		
		if(seller.getProperty() == null) {
			reply.setPerformative(ACLMessage.REFUSE);
			return reply;
		}
		Integer offer = null;
		try {
			offer = Integer.parseInt(cfp.getContent());
		}catch(NumberFormatException e) {
			offer = null;
		}
		//se não houve nenhuma oferta, primeira iteração
		if(offer == null) {
			reply.setPerformative(ACLMessage.PROPOSE);
			Integer proposal = this.max_value;
			reply.setContent(Integer.toString(proposal));
		}else { // analisar o valor proposto pelo comprador e sugerir outro ou aceitar
			
			if(offer < this.min_value) {
				reply.setPerformative(ACLMessage.REFUSE);
			}
			else if(offer >= this.max_value) {
				reply.setPerformative(ACLMessage.PROPOSE);
				Integer propose = offer;
				reply.setContent(Integer.toString(propose));
			}else {
				Integer difference = this.max_value - offer;
				double gaussian = rnd.nextGaussian() * 0.5 + 0.4;
				Integer new_proposal = (int) (offer + difference * gaussian);
				reply.setPerformative(ACLMessage.PROPOSE);
				reply.setContent(Integer.toString(new_proposal));
			}
		}
		
		return reply;
	}
	
	protected void handleOutOfSequence(ACLMessage msg) {
		
		System.out.println("Seller received out of sequence");
	}
	
	protected ACLMessage handleAcceptProposal(ACLMessage cfp,ACLMessage propose,ACLMessage accept) throws FailureException{
		ACLMessage reply = accept.createReply();
		if(this.seller.getProperty() != null) {
			this.seller.setProperty(null);
			System.out.println("Tenho um contrato!");
			//System.out.println(cfp);
			//System.out.println(propose);
			//System.out.println(accept);
			
			Integer price_payed = Integer.parseInt(propose.getContent());
			
			reply.setPerformative(ACLMessage.INFORM);
			reply.setContent(propose.getContent());
			/*try {
				//reply.setContentObject(this.seller.getProperty());
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			System.out.println("A enviar inform");
			this.seller.setProperty(null);
			this.seller.increaseMoney(price_payed);
			System.out.println("Eu, " + this.seller.getLocalName() + ", fiquei com " + this.seller.getMoney() + "€");
		}else {
			System.out.println("A enviar failure");
			reply.setPerformative(ACLMessage.FAILURE);
		}
		return reply;
	}
	
}

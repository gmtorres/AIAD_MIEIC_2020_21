package behaviours;
import java.io.StringReader;
import java.util.Random;

import agents.Seller;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.StringACLCodec;
import jade.lang.acl.ACLCodec.CodecException;
import jade.proto.SSContractNetResponder;
import jade.proto.SSIteratedContractNetResponder;
import utils.Property;

public class SellerRespondsBuyer_2_Responder extends SSIteratedContractNetResponder{
	
	Seller seller;
	Integer interactions;
	Integer min_value, max_value;
	
	AID reagent;
	int rate ;
	
	static Random rnd;
	static {
		rnd = new Random();
	}
	
	
	public SellerRespondsBuyer_2_Responder(Seller seller, ACLMessage cfp) {
		super(seller, cfp);
		this.seller = seller;
		this.interactions = 0;
		if(this.seller.getProperty() != null) {
			this.min_value = this.seller.getPriceFromRelativeDifference(this.seller.getMinDifference()); // Minimum value
			this.max_value = this.seller.getPriceFromRelativeDifference(this.seller.getMaxDifference()); // Max value
			//System.out.println("So aceito até " + this.min_value);
		}
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
			String[] agent_info = cfp.getContent().split(",");
			StringACLCodec codec = new StringACLCodec(new StringReader(agent_info[0]), null);
			try {
				reagent = codec.decodeAID();
			} catch (CodecException e1) {
				e1.printStackTrace();
			}
			rate = Integer.parseInt(agent_info[1]);
		}
		//se não houve nenhuma oferta, primeira iteração
		interactions++;
		if(offer == null) {
			reply.setPerformative(ACLMessage.PROPOSE);
			Integer proposal = this.max_value;
			reply.setContent(Integer.toString(proposal) + "/" + this.seller.getProperty());
		}else { // analisar o valor proposto pelo comprador e sugerir outro ou aceitar
			//System.out.println("Comprador sugeriu outro preço: " + offer);
			if(interactions >= this.seller.getMaxInteractions()) {
				//System.out.println("Cansei me desta negociação, vou desistir");
				reply.setPerformative(ACLMessage.REFUSE);
			}else if(offer < this.min_value * 0.90) {
				//System.out.println("Oferta muito baixa, deve estar a goxar comigo");
				reply.setPerformative(ACLMessage.REFUSE);
			}
			else if(offer >= this.max_value) {
				reply.setPerformative(ACLMessage.PROPOSE);
				Integer propose = offer;
				reply.setContent(Integer.toString(propose) + "/" + this.seller.getProperty());
			}else {
				//System.out.println("Vou sugerir outro preço ao comprador");
				Integer difference = this.max_value - offer;
				double gaussian = this.generateRandomDistribution(5) * this.seller.getPriceRange() 
									+ this.seller.getPriceStart();
				Integer new_proposal = (int) (offer + difference * gaussian);
				max_value = new_proposal;
				reply.setPerformative(ACLMessage.PROPOSE);
				reply.setContent(Integer.toString(new_proposal) + "/" + this.seller.getProperty());
			}
		}
		
		return reply;
	}
	
	protected void handleRejectProposal(ACLMessage cfp,ACLMessage propose,ACLMessage reject) {
		//System.out.println("Seller received reject");
	}
	
	protected void handleOutOfSequence(ACLMessage msg) {
		//System.out.println("Seller received out of sequence");
	}
	
	protected ACLMessage handleAcceptProposal(ACLMessage cfp,ACLMessage propose,ACLMessage accept) throws FailureException{
		ACLMessage reply = accept.createReply();
		if(this.seller.getProperty() != null) {
			
			Integer price_payed = Integer.parseInt(propose.getContent().split("/")[0]);
			
			reply.setPerformative(ACLMessage.INFORM);
			reply.setContent(String.valueOf(price_payed) + "," + this.seller.getProperty().getPrice());
			
			this.seller.sellHouse(this.reagent,this.rate,price_payed);
			//this.seller.increaseMoney(price_payed);
		}else {
			System.out.println("Erro aconteceu");
			reply.setPerformative(ACLMessage.FAILURE);
		}
		return reply;
	}
	
	private double generateRandomDistribution(int times) {
		double result = 0;
		for(int i = 0; i < times; i++) {
			result += rnd.nextDouble();
		}
		return result / times;
	}
	
}

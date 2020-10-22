import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.proto.SSContractNetResponder;

public class SellerRespondsBuyer_2_Responder extends SSContractNetResponder{
	
	Seller seller;
	
	public SellerRespondsBuyer_2_Responder(Seller seller, ACLMessage cfp) {
		super(seller, cfp);
		this.seller = seller;
	}
	
	protected ACLMessage handleCfp(ACLMessage cfp) {
		System.out.println("Recebi pedido de compra!");
		//System.out.println(cfp);
		ACLMessage reply = cfp.createReply();
		
		if(seller.getProperty() == null) {
			reply.setPerformative(ACLMessage.REFUSE);
			return reply;
		}
		
		reply.setPerformative(ACLMessage.PROPOSE);
		Integer offer = this.seller.getProperty().getPrice();
		reply.setContent(Integer.toString(offer));
		
		//this.seller.setBestBuyer(cfp.getSender().getLocalName());
		//this.seller.setBestOffer(offer);
		
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

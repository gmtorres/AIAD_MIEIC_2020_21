import java.util.Vector;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;

public class BuyerAsksSeller extends ContractNetInitiator  {
	private Buyer buyer;
	public BuyerAsksSeller(Buyer buyer, ACLMessage cfp) {
		super(buyer, cfp);
		this.buyer = buyer;
	}
	

	protected Vector<ACLMessage> prepareCfps(ACLMessage cfp) {
		Vector<ACLMessage> v = new Vector<ACLMessage >();
		cfp.setContent("Quero comprar!");
		DFAgentDescription[] sellers = this.buyer.searchForSeller();
		//System.out.println("Vou enviar a proposta a " + sellers.length + " vendedores.");
		for (int i = 0; i < sellers.length; i++) {
			String name = sellers[i].getName().getName();
			//System.out.println(name);
			cfp.addReceiver(sellers[i].getName());
		}
		v.add(cfp);
		//System.out.println("Enviado, siga comprar!");
		return v;		
	}
	
	protected void handleAllResponses(Vector responses, Vector acceptances) {
		Vector<ACLMessage> replys = new Vector<ACLMessage>();
		Integer lowerPrice = null;
		int position = -1;
		for (int i = 0; i < responses.size(); i++) { // analyze all proposes
			//System.out.println(responses.get(i));
			ACLMessage response = (ACLMessage) responses.get(i);
			if(response.getPerformative() != ACLMessage.PROPOSE)
				continue;
			Integer proposed_price = Integer.parseInt(response.getContent());
			//System.out.println("Preço a pagar " + response.getContent());
			if(proposed_price < this.buyer.getMoney() && (lowerPrice == null || proposed_price < lowerPrice)) {
				lowerPrice = proposed_price;
				position = i;
			}
		}
		if(position != -1) {
			ACLMessage accept = ((ACLMessage)responses.get(position)).createReply();
			accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			acceptances.add(accept);
			System.out.println("Aceitei o preço do " + ((ACLMessage)responses.get(position)).getSender());
		}else {
			System.out.println("Não aceitei nenhum preço");
		}
	}
	
	protected void handleFailure(ACLMessage inform) {
		this.reset(new ACLMessage(ACLMessage.CFP));
	}
	
	protected void handleInform(ACLMessage inform) {
		System.out.println("Inform recebido");
		//System.out.println(inform);

		Integer price_payed = Integer.parseInt(inform.getContent());
		this.buyer.increaseMoney(-price_payed);
		System.out.println("Eu, " + this.buyer.getLocalName() + ", fiquei com " + this.buyer.getMoney() + "€, paguei " + price_payed + "€");
	}
	
}	

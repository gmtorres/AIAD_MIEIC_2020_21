import java.util.Vector;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

public class BuyerAsksSeller extends ContractNetInitiator  {
	private Buyer buyer;
	public BuyerAsksSeller(Buyer buyer, ACLMessage cfp) {
		super(buyer, cfp);
		this.buyer = buyer;
	}
	

	protected Vector<ACLMessage > prepareCfps(ACLMessage cfp) {
		Vector<ACLMessage > v = new Vector<ACLMessage >();
		cfp.setContent("Quero comprar!");
		DFAgentDescription[] sellers = this.buyer.searchForSeller();
		System.out.println("Vou enviar a proposta a " + sellers.length + " vendedores.");
		for (int i = 0; i < sellers.length; i++) {
			String name = sellers[i].getName().getName();
			System.out.println(name);
			cfp.addReceiver(sellers[i].getName());
		}
		v.add(cfp);
		System.out.println("Enviado, siga comprar!");
		return v;		
	}
	
	protected void handleAllResponses(Vector responses, Vector acceptances) {
		for (int i = 0; i < responses.size(); i++) {
			System.out.println(responses.get(i));
		}
	}
}	

package behaviours;
import agents.Seller;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SSResponderDispatcher;

public class SellerRespondsBuyer_2 extends SSResponderDispatcher{
	
	Seller seller;
	
	public SellerRespondsBuyer_2(Seller seller, MessageTemplate tpl) {
		super(seller, tpl);
		this.seller = seller;
	}

	@Override
	protected Behaviour createResponder(ACLMessage initiationMsg) {
		return new SellerRespondsBuyer_2_Responder(this.seller, initiationMsg);
	}

}

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

public class Seller extends Agent{
	public void setup() {
		System.out.println("Let's sell");
		register();
		//addBehaviour(new SellerBehavior(this, MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF)));
	}
	
	protected void register() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("seller");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}catch(FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	protected void takeDown() {
		try {
			DFService.deregister(this);
		}catch(FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	class SellerBehavior extends ContractNetResponder {

		public SellerBehavior(Agent a, MessageTemplate mt) {
			super(a, mt);
			
		}
		
		protected ACLMessage handleCfp() {
			return new ACLMessage(0);
		}
		
	}
	
}

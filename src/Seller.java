import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

public class Seller extends Person{
	
	private String bestBuyer = null;
	private Integer bestOffer = null;
	
	private String hurry = "desperate";
	
	public Seller(){
		this.setProperty(new Property());
	}
	
	public void setup() {
		System.out.println("Let's sell this property for: " + this.getProperty().getPrice() + "€");
		SequentialBehaviour seq = new SequentialBehaviour();
		seq.addSubBehaviour(new SellerRespondsBuyer_2(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
		addBehaviour(seq);
		register();
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

	public String getBestBuyer() {
		return bestBuyer;
	}

	public void setBestBuyer(String bestBuyer) {
		this.bestBuyer = bestBuyer;
	}

	public Integer getBestOffer() {
		return bestOffer;
	}

	public void setBestOffer(Integer bestOffer) {
		this.bestOffer = bestOffer;
	}
	
	
}

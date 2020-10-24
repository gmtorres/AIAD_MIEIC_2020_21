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
	
	enum Personality {
		PATIENT,
		NORMAL,
		IMPATIENT
	}
	
	enum MoneyStatus {
		DESPERATE,
		NORMAL
	}
	
	enum PriceChange{
		FLEXIBLE,
		NORMAL
	}
	
	private String bestBuyer = null;
	private Integer bestOffer = null;
	
	private Personality personality;
	private MoneyStatus money_status;
	private PriceChange price_change;
	
	public Seller(){
		this.setProperty(new Property());
		personality = Personality.NORMAL;
		money_status = MoneyStatus.NORMAL;
		price_change = PriceChange.NORMAL;
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
	
	public int getMaxInteractions() {
		switch(personality) {
		case PATIENT:
			return 10;
		case NORMAL:
			return 7;
		case IMPATIENT:
			return 4;
		}
		return 7;
	}
	
	public double getMinDifference(){
		switch(money_status) {
		case DESPERATE:
			return 0.85;
		case NORMAL:
			return 0.9;
		}
		return 0.9;
	}
	
	public double getMaxDifference(){
		switch(money_status) {
		case DESPERATE:
			return 1.05;
		case NORMAL:
			return 1.1;
		}
		return 1.1;
	}
	
	public double getPriceStart(){
		switch(price_change) {
		case FLEXIBLE:
			return 0.3;
		case NORMAL:
			return 0.4;
		}
		return 0.4;
	}
	public double getPriceRange(){
		switch(price_change) {
		case FLEXIBLE:
			return 0.3;
		case NORMAL:
			return 0.4;
		}
		return 0.4;
	}
	
	
}

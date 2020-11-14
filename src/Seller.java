import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

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
	
	private Property old_property = null;
	
	public Seller(int p, int m, int c){
		personality = Personality.values()[p];
		money_status = MoneyStatus.values()[m];
		price_change = PriceChange.values()[c];
	}
	
	public Seller(){
	}
	
	public void setup() {
		this.setProperty(new Property());
		Object[] args = getArguments();
		if(args == null) {
			personality = Personality.NORMAL;
			money_status = MoneyStatus.NORMAL;
			price_change = PriceChange.NORMAL;
		}else {
			personality = Personality.values()[Integer.parseInt(args[0].toString())];
			money_status = MoneyStatus.values()[Integer.parseInt(args[1].toString())];
			price_change = PriceChange.values()[Integer.parseInt(args[2].toString())];
		}
		
		System.out.println(this.getLocalName()+ ": Let's sell this property for: " + this.getProperty().getPrice() + "€");

		addBehaviour(new SellerGetsRequest(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
		addBehaviour(new SellerRespondsBuyer_2(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
		register();
	}
	
	public void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		String str = "";
		str += this.getLocalName()+ ": ";
		if(this.getProperty() != null)
			str += "I was unable to sell this house " + this.getProperty();
		else
			str += "I sold this house " + this.getProperty() + " for " + this.getMoney();
		System.out.println(str);
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
	
	public void sellHouse(){
		this.old_property = this.getProperty();
		this.setProperty(null);
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

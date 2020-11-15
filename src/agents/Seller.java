package agents;
import behaviours.SellerGetsRequest;
import behaviours.SellerRespondsBuyer_2;
import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Property;

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
	private int payed_to_agent = 0;
	
	public Seller(int p, int m, int c){
		personality = Personality.values()[p];
		money_status = MoneyStatus.values()[m];
		price_change = PriceChange.values()[c];
	}
	
	public Seller(){
	}
	
	public void setup() {
		this.setProperty(new Property());
		
		// System.out.println(this.getLocalName()+ ": Let's sell this property for: " + this.getProperty().getPrice() + " euros");

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
			str += "I sold this house " + this.old_property + " for " + this.getMoney() + "�";
		//System.out.println(str);
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
	
	public Property getOldProperty() {
		return this.old_property;
	}
	
	public int getTotalWithRate() {
		if(this.getProperty() == null)
			return this.getMoney();
		return this.getProperty().getPrice();
	}
	public int getTotalWithoutRate() {
		if(this.getProperty() == null)
			return this.getMoney() + this.payed_to_agent;
		return this.getProperty().getPrice();
	}
	
	public void sellHouse(AID reagent,int rate, int price){
		int price_to_agent = rate*price/100;
		payed_to_agent = price_to_agent;
		
		this.old_property = this.getProperty();
		this.setProperty(null);
		this.increaseMoney(price-price_to_agent);
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM_IF);
		AID dest = reagent;
	    msg.addReceiver(dest);
	    msg.setContent(String.valueOf(price_to_agent));
	    send(msg);
	}
	
	public int getMaxInteractions() {
		switch(personality) {
		case PATIENT:
			return 12;
		case NORMAL:
			return 8;
		case IMPATIENT:
			return 4;
		}
		return 7;
	}
	
	public double getMinDifference(){
		switch(money_status) {
		case DESPERATE:
			return 0.7;
		case NORMAL:
			return 0.85;
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

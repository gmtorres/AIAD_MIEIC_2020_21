import java.util.Random;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Buyer extends Person{
	
	enum Looking {
		HURRY,
		CALM,
		BEST
	}
	
	private Looking looking_state;
	
	private Property desired_property;
	private Property property;
	
	public Buyer(){
		Random rnd = new Random();
		//setMoney(rnd.nextInt(60000) + 70000);
		//setMoney(100000);
		
		this.property = null;
		this.desired_property = new Property();
		this.setMoney((int) (this.desired_property.evaluateHouse() * (0.9 + rnd.nextFloat() * 0.5)));
		

	}
	
	public void setup() {
		Object[] args = getArguments();
		if(args == null) {
			looking_state = Looking.CALM;
		}else {
			looking_state = Looking.values()[Integer.parseInt(args[0].toString())];
		}
		
		System.out.println(this.getLocalName() + ": I have " + this.getMoney() + "€");
		addBehaviour(new BuyerAsksSeller(this, new ACLMessage(ACLMessage.CFP)));
	}
	
	public void takeDown() {
		String str = "";
		str += this.getLocalName() + ": I have " + this.getMoney() + "€";
		if(this.getProperty() != null)
			str += " and bought the following house: " + this.getProperty();
		else
			str += " and was unable to buy an house";
		System.out.println(str);
	}
	
	public Property getDesiredProperty() {
		return this.desired_property;
	}
	
	public DFAgentDescription[] searchForSeller() {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("seller");
		template.addServices(sd);
		DFAgentDescription[] result = null;
		try {
			result = DFService.search(this, template);
		}catch(FIPAException fe) {
			fe.printStackTrace();
		}
		return result;
	}
	
	public double getBestFactor() {
		switch(looking_state) {
		case HURRY:
			return 0.9;
		case CALM:
			return 1.1;
		case BEST:
			return 1.2;
		}
		return 1.2;
	}
	
	public int getMinValid() {
		switch(looking_state) {
		case HURRY:
			return 5;
		case CALM:
			return 4;
		case BEST:
			return 2;
		}
		return 1;
	}
	
	public double getWorstFactor() {
		switch(looking_state) {
		case HURRY:
			return 0.80;
		case CALM:
			return 1;
		case BEST:
			return 1.15;
		}
		return 1;
	}
}

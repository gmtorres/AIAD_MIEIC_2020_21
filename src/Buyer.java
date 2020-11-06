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
		setMoney(rnd.nextInt(500000) + 70000);
		setMoney(100000);
		
		this.property = null;
		this.desired_property = new Property();
		this.setMoney((int) (this.desired_property.evaluateHouse() * (0.9 + rnd.nextFloat() * 0.5)));
		
		System.out.println(this.getName() + ": I have " + this.getMoney() + "€");
		
		looking_state = Looking.CALM;
	}
	
	public void setup() {
		addBehaviour(new BuyerAsksSeller(this, new ACLMessage(ACLMessage.CFP)));
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
			return 1;
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
			return 0.85;
		case CALM:
			return 1;
		case BEST:
			return 1.15;
		}
		return 1;
	}
}

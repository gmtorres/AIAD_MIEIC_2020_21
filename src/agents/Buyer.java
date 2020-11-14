package agents;
import java.util.ArrayList;
import java.util.Random;

import behaviours.BuyerAsksSeller;
import behaviours.BuyerContactsAgency;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import utils.Property;

public class Buyer extends Person{
	
	enum Looking {
		HURRY,
		CALM,
		BEST
	}
	
	private Looking looking_state;
	
	private Property desired_property;
	private Property property;
	
	private Behaviour getHouse = null;
	
	private ArrayList<AID> sellers = new ArrayList();
	
	public Buyer(){
		Random rnd = new Random();
		this.property = null;
		this.desired_property = new Property();
		this.setMoney((int) (this.desired_property.evaluateHouse() * (0.9 + rnd.nextFloat() * 0.5)));
	}
	
	public Buyer(int l) {
		this();
		looking_state = Looking.values()[l];
	}
	
	public void initiateSellersInteraction() {
		getHouse = new BuyerAsksSeller(this, new ACLMessage(ACLMessage.CFP));
		addBehaviour(getHouse);
	}
	
	public boolean done() {
		if(getHouse == null)
			return false;
		String a = getHouse.getExecutionState();
		//System.out.println(this.getLocalName() + "  " + a);
		return getHouse.done();
	}
	
	public void setup() {
		Object[] args = getArguments();
		if(args == null) {
			looking_state = Looking.CALM;
		}else {
			looking_state = Looking.values()[Integer.parseInt(args[0].toString())];
		}
		
		this.addBehaviour(new BuyerContactsAgency(this,new ACLMessage(ACLMessage.REQUEST)));
		
		System.out.println(this.getLocalName() + ": I have " + this.getMoney() + "€");
	}
	
	public void takeDown() {
		String str = "";
		str += this.getLocalName() + ": I have " + this.getMoney() + "€";
		if(this.getProperty() != null)
			str += " and bought the following house: " + this.getProperty() + " when looking for this: " + this.getDesiredProperty();
		else
			str += " and was unable to buy an house like this " + this.getDesiredProperty();
		System.out.println(str);
	}
	public ArrayList<AID> getSellers(){
		return this.sellers;
	}
	
	public void addSeller(AID new_aid) {
		for(AID a : sellers) {
			if(a.equals(new_aid))
				return;
		}
		sellers.add(new_aid);
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
	
	public DFAgentDescription[] searchForAgencies() {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("RealEstateAgency");
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

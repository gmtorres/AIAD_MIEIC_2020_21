import java.util.Random;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Buyer extends Person{
	
	public Buyer(){
		Random rnd = new Random();
		setMoney(rnd.nextInt(500000) + 70000);
		setMoney(100000);
		System.out.println(this.getName() + ": I have " + this.getMoney() + "€");
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
}

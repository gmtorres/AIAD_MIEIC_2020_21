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
		setMoney(rnd.nextInt(90000) + 80000);
		System.out.println(this.getLocalName() + ": I have " + this.getMoney() + "€");
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
			if(result.length == 0) {
				System.out.println("No results found");
			}else {
				System.out.println("Look who I found:");
				for(int i = 0; i < result.length; i++) {
					System.out.println("Found " + result[i].getName());
				}
			}
			System.out.println("\n");
		}catch(FIPAException fe) {
			fe.printStackTrace();
		}
		return result;
	}
}

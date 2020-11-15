package agents;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;

import behaviours.AgentAsksAgency;
import behaviours.RealEstateAgentGetsRequest;

public class RealEstateAgent extends Agent{
    private AID agency = null;
    private int agentRate;
    private Behaviour b = null;
    
    private int money = 0;
    
    public RealEstateAgent(){
    	super();
    	Random rnd = new Random();
        this.agentRate = rnd.nextInt(8) + 1;
    }
    
    public void setup() {
    	b = new AgentAsksAgency(this, new ACLMessage(ACLMessage.CFP));
		addBehaviour(b);
		addBehaviour(new RealEstateAgentGetsRequest(this,MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
		addBehaviour(new CyclicBehaviour(this) 
        {
			MessageTemplate mt= MessageTemplate.MatchPerformative(ACLMessage.INFORM_IF);
            public void action() 
            {
				ACLMessage msg= receive(mt);
                if (msg!=null) {
                	String content = msg.getContent();
                	money += Integer.parseInt(content);
                }
                block();
             }
        });
	}
	public boolean done() {
		if(b == null)
			return false;
		String a = b.getExecutionState();
		return b.done();
	}
    
    public void setAgency(AID agency) {
        this.agency = agency;
    }
    
    public int getMoney() {
    	return this.money;
    }
    

    public void removeAgency() {
        this.agency = null;
    }
    
    public int getAgentRate() {
    	return this.agentRate;
    }
    
    public DFAgentDescription[] searchForAgency() {
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

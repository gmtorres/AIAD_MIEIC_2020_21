import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;

public class RealEstateAgent extends Agent{
    private AID agency = null;
    private int agentRate;
    private Behaviour b = null;
    
    public RealEstateAgent(){
    	super();
    	Random rnd = new Random();
        this.agentRate = rnd.nextInt(10);
    }
    
    public void setup() {
    	b = new AgentAsksAgency(this, new ACLMessage(ACLMessage.CFP));
		addBehaviour(b);
		addBehaviour(new RealEstateAgentGetsRequest(this,MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
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

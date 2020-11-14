import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class RealEstateAgent extends Agent{
    private AID agency = null;
    private int agentRate;

    public RealEstateAgent(){
    	super();
    	Random rnd = new Random();
        this.agentRate = rnd.nextInt(10);
    }
    
    public void setup() {
		addBehaviour(new AgentAsksAgency(this, new ACLMessage(ACLMessage.CFP)));
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

}

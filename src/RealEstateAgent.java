import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.Random;

public class RealEstateAgent extends Agent{
    private RealEstateAgency agency = null;
    private int agentRate;

    RealEstateAgent(){
    	Random rnd = new Random();
        this.agentRate = rnd.nextInt(10);
    }

    public void setAgency(RealEstateAgency agency) {
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

package agents;
import java.util.ArrayList;
import java.util.Random;

import behaviours.AgencyGetsRequest;
import behaviours.AgencyRespondsAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RealEstateAgency extends Logger{
    private ArrayList<AID> agents;
    private int minAgentRate;
    private int maxAgentRate;
    private int maxTeamSize;
    private int money = 0;

    public RealEstateAgency() {
    	super();
		Random rnd = new Random();
        agents = new ArrayList<AID>();
        maxTeamSize = rnd.nextInt(5) + 2;
        this.minAgentRate = rnd.nextInt(5);
        this.maxAgentRate = rnd.nextInt(4) + 6;
    }
    
    public void setup() {
		addBehaviour(new AgencyRespondsAgent(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
		addBehaviour(new AgencyGetsRequest(this,MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
		addBehaviour(new CyclicBehaviour(this) 
        {
			MessageTemplate mt= MessageTemplate.MatchPerformative(ACLMessage.INFORM_IF);
            public void action() 
            {
				ACLMessage msg= receive(mt);
                if (msg!=null) {
                	String content = msg.getContent();
                	int transaction_money = Integer.parseInt(content);
                	money += transaction_money;
                }
                block();
             }
        });
		register();
	}
    public int getMoney() {
    	return this.money;
    }
    protected void register() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("RealEstateAgency");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}catch(FIPAException fe) {
			fe.printStackTrace();
		}
	}
    
    protected void takeDown() {
		try {
			DFService.deregister(this);
		}catch(FIPAException fe) {
			fe.printStackTrace();
		}
	}

    public int getTeamSize() {
        return agents.size();
    }

    public void addAgent(AID agent) {
         agents.add(agent);

    }
    
    public int getMaxTeamSize(){
    	return maxTeamSize;
    }
    
    public boolean canAcceptAgents() {
    	return maxTeamSize >= this.getTeamSize();
    }

    public void removeAgent (AID agent) {
        agents.remove(agent);
    }
    
    public AID getRandomAgent () {
    	Random rnd = new Random();
    	if(this.agents.size() == 0)
    		return null;
        return this.agents.get(rnd.nextInt(this.agents.size()));
    }

    public int getAgentMinRate() {
        return this.minAgentRate;
    }

    public int getAgentMaxRate() {
        return this.maxAgentRate;
    }

}
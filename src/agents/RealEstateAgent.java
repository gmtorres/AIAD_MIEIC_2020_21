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

import agents.Seller.PriceChange;
import behaviours.AgentAsksAgency;
import behaviours.RealEstateAgentGetsRequest;

public class RealEstateAgent extends Logger{
    private AID agency = null;
    private int agentRate;
    private Behaviour b = null;
    
    private int money = 0;
    private int max_sellers = 50;
    
    enum Performance{
    	BAD,
    	NORMAL,
    	GOOD
    }
    
    private Performance performance;
    
    public RealEstateAgent(int p){
        this.performance = Performance.values()[p];
        
        this.setRate();
    }
    private void setRate() {
    	Random rnd = new Random();
    	switch(performance) {
    	case BAD:
    		this.agentRate = rnd.nextInt(5) + 1;
    		break;
    	case NORMAL:
    		this.agentRate = rnd.nextInt(4) + 3;
    		break;
    	case GOOD:
    		this.agentRate = rnd.nextInt(4) + 5;
    		break;
    	}
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
                	int transaction_money = Integer.parseInt(content);
                	int my_money = (int) ((double)transaction_money * 0.5);
                	int agency_money = (int) ((double)transaction_money * 0.5);
                	
                	money += my_money;
                	
                	ACLMessage msg_2 = new ACLMessage(ACLMessage.INFORM_IF);
            		AID dest = agency;
            	    msg_2.addReceiver(dest);
            	    msg_2.setContent(String.valueOf(agency_money));
            	    send(msg_2);
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
	
	public int getMaxSellers(){
		switch(performance) {
    	case BAD:
    		return 25;
    	case NORMAL:
    		return 40;
    	case GOOD:
    		return 70;
    	}
		return 40;
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

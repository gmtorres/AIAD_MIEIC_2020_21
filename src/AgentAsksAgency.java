import jade.proto.ProposeInitiator;

import java.util.Vector;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;


public class AgentAsksAgency extends ProposeInitiator {
	
	private RealEstateAgent agent;
	
	
	public AgentAsksAgency(RealEstateAgent agent ,ACLMessage cfp) {
		super(agent, cfp);
		this.agent = agent;
	}
	
	protected Vector<ACLMessage> prepareInitiations(ACLMessage cfp) {
		Vector<ACLMessage> v = new Vector<ACLMessage >();
		cfp.setContent("Quero entrar na agencia! A minha taxa �:" + agent.getAgentRate());
		DFAgentDescription[] agencies = this.agent.searchForAgency();
		for (int i = 0; i < agencies.length; i++) {
			//String name = agencies[i].getName().getName();
			cfp.addReceiver(agencies[i].getName());
		}
		v.add(cfp);
		return v;		
	}
}
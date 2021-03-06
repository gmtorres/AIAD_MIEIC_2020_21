package behaviours;
import jade.proto.ContractNetInitiator;

import java.util.Vector;

import agents.RealEstateAgent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


public class AgentAsksAgency extends ContractNetInitiator {
	
	private RealEstateAgent agent;
	private boolean accepted = false;
	
	
	public AgentAsksAgency(RealEstateAgent agent ,ACLMessage cfp) {
		super(agent, cfp);
		this.agent = agent;
	}
	
	
	protected Vector<ACLMessage> prepareCfps(ACLMessage cfp) {
		Vector<ACLMessage> v = new Vector<ACLMessage >();
		cfp.setContent("Quero entrar na agencia! A minha taxa e:" + agent.getAgentRate());
		// System.out.println("AGENTE: Quero entrar na agencia! A minha taxa: " + agent.getAgentRate() + " euros");
		DFAgentDescription[] agencies = this.agent.searchForAgency();
		for (int i = 0; i < agencies.length; i++) {
			//String name = agencies[i].getName().getName();
			cfp.addReceiver(agencies[i].getName());
		}
		v.add(cfp);
		return v;		
	}
	protected void handleRefuse(ACLMessage refuse) {
		this.agent.log("A agencia: " + refuse.getSender().getLocalName() + " recusou a minha proposta");
	}
	
	protected void handlePropose(ACLMessage response, Vector acceptances) {
				
		if (!accepted) {
			ACLMessage accept = response.createReply();
			accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			acceptances.add(accept);
			this.accepted = true;
			this.agent.log("A agencia: " + response.getSender().getLocalName() + " est� disponivel para mim");
			return;
		}
		else {
			ACLMessage refuse = response.createReply();
			refuse.setPerformative(ACLMessage.REJECT_PROPOSAL);
			acceptances.add(refuse);
			this.agent.log("A agencia: " + response.getSender().getLocalName() + " aceitou mas ja me comprometi");
		}
		
		return;
		
	}
	
	protected void handleInform(ACLMessage inform) {
		this.agent.setAgency(inform.getSender());
		//System.out.println("AGENTE: Tenho nova agencia!");
		this.agent.log("Tenho uma nova agencia: " + inform.getSender().getLocalName());
	}
	
	protected void handleFailure(ACLMessage failure) {
		this.agent.setAgency(null);
		this.accepted = false;
		this.reset(new ACLMessage (ACLMessage.CFP));
	}
}
import jade.proto.ProposeResponder;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class AgencyRespondsAgent extends ProposeResponder {
	
	private RealEstateAgency agency;
	
	public AgencyRespondsAgent(RealEstateAgency agency, MessageTemplate mt) {
		super(agency, mt);
		this.agency = agency;
	}
	
	
	protected ACLMessage prepareResponse(ACLMessage cfp) {
		System.out.println("Recebi proposta do agente!");
		
		int num = Integer.parseInt(cfp.getContent().substring(cfp.getContent().lastIndexOf(":") + 1));
		
		ACLMessage reply = cfp.createReply();
		
		
		if(num < agency.getAgentMinRate() || num > agency.getAgentMaxRate()) {
			System.out.println("AGENCIA: Recusei" +  agency.getAgentMinRate() + "   " + agency.getAgentMaxRate());
			reply.setPerformative(ACLMessage.REFUSE);
			return reply;
		}
		
		reply.setPerformative(ACLMessage.PROPOSE);
		
		reply.setContent("");
				
		return reply;
	}
	
	
} 
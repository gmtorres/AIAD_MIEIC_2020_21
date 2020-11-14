package behaviours;
import agents.RealEstateAgency;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;

public class AgencyRespondsAgent extends ContractNetResponder {
	
	private RealEstateAgency agency;
	
	public AgencyRespondsAgent(RealEstateAgency agency, MessageTemplate mt) {
		super(agency, mt);
		this.agency = agency;
	}
	
	
	protected ACLMessage handleCfp(ACLMessage cfp) {
		System.out.println("Recebi proposta do agente!");
		
		int num = Integer.parseInt(cfp.getContent().substring(cfp.getContent().lastIndexOf(":") + 1));
		
		ACLMessage reply = cfp.createReply();
		
		
		if(num < agency.getAgentMinRate() || num > agency.getAgentMaxRate() && agency.canAcceptAgents()) {
			System.out.println("AGENCIA: Recusei" +  agency.getAgentMinRate() + "   " + agency.getAgentMaxRate());
			reply.setPerformative(ACLMessage.REFUSE);
			return reply;
		}
		
		reply.setPerformative(ACLMessage.PROPOSE);
		
		reply.setContent("");
				
		return reply;
	}
	
	protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
			ACLMessage reply = accept.createReply();

		
			if(this.agency.canAcceptAgents()) {
				this.agency.addAgent(cfp.getSender());
				
				System.out.println("AGENCIA: Tenho novo agente!");

								
				reply.setPerformative(ACLMessage.INFORM);
			}
			
			else {
				reply.setPerformative(ACLMessage.FAILURE);
			}
			
			return reply;
	}
	
	
	
	
} 
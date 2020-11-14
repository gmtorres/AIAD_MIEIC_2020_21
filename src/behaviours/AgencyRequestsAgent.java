package behaviours;
import java.util.Vector;

import agents.RealEstateAgency;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

public class AgencyRequestsAgent extends AchieveREInitiator {
	
	RealEstateAgency agency;
	ACLMessage clientRequest;

	public AgencyRequestsAgent(RealEstateAgency a, ACLMessage msg, ACLMessage request) {
		super(a, msg);
		this.agency = a;
		this.clientRequest = request;
	}
	
	protected Vector prepareRequests(ACLMessage request) {
		System.out.println("\n\nA enviar mensagens");
		Vector requests = new Vector<ACLMessage>();
		ACLMessage r = (ACLMessage) request.clone();
		AID agentID = this.agency.getRandomAgent();
		if(agentID != null) { // agente disponivel
			System.out.println(agentID.getName());
			r.addReceiver(agentID);
			r.setContent(this.clientRequest.getContent());
			requests.add(r);
		}else { //nenhum agente disponivel
			ACLMessage response = clientRequest.createReply();
			response.setPerformative(ACLMessage.INFORM);
			response.setContent("");
			if (parent != null) {
				DataStore ds = getDataStore();
				ds.put(((AchieveREResponder) parent).RESULT_NOTIFICATION_KEY, response);
			} else {
				myAgent.send(response);
			}
		}
		return requests;
	}
	
	protected void handleAgree(ACLMessage agree) {
		//System.out.println(agree);
	}
	
	protected void handleInform(ACLMessage inform) {
		//System.out.println(inform);
		ACLMessage response = clientRequest.createReply();
		response.setPerformative(ACLMessage.INFORM);
		response.setContent(inform.getContent());
		if (parent != null) {
			DataStore ds = getDataStore();
			ds.put(((AchieveREResponder) parent).RESULT_NOTIFICATION_KEY, response);
		} else {
			myAgent.send(response);
		}
	}

}

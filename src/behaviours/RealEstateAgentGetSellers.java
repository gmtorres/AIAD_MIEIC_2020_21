package behaviours;
import java.util.ArrayList;
import java.util.Vector;

import agents.RealEstateAgent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import utils.Property;

public class RealEstateAgentGetSellers extends AchieveREInitiator {
	RealEstateAgent REagent;
	ACLMessage clientRequest;
	boolean done = false;
	
	RealEstateAgentGetSellers(RealEstateAgent re, ACLMessage msg,ACLMessage request){
		super(re,msg);
		this.REagent = re;
		this.clientRequest = request;
	}
	
	protected Vector prepareRequests(ACLMessage request) {
		Vector requests = new Vector<ACLMessage>();
		DFAgentDescription[] sellers = this.REagent.searchForSeller();
		for(DFAgentDescription s : sellers) {
			ACLMessage r = (ACLMessage) request.clone();
			AID aid = s.getName();
			r.addReceiver(aid);
			requests.add(r);
		}
		return requests;
	}
	
	protected void handleAgree(ACLMessage agree) {
		//System.out.println(agree);
	}
	
	protected void handleAllResultNotifications(Vector resultNotifications) {
		String content = "";
		Property desired = new Property(this.clientRequest.getContent());
		int accepted = 0;
		
		for(int i = 0; i < resultNotifications.size(); i++) {
			ACLMessage response = (ACLMessage) resultNotifications.get(i);
			if(response.getPerformative() == ACLMessage.INFORM) {
				String r_content = response.getContent();
				if(r_content == null || (r_content != null && r_content.equals("")))
					continue;
				Property proposed = new Property(r_content);
				double factor = Property.relativePropertyDifference(desired, proposed);
				if(factor >= 0.70 && factor <= 2.2) { //não vou sugerir nem casas fracas nem casas muito bai
					AID sender = response.getSender();
					content+= sender + ",";
					accepted++;
				}
			}
		}
		//System.out.println(accepted);
		
		ACLMessage response = clientRequest.createReply();
		response.setPerformative(ACLMessage.INFORM);
		response.setContent(content + "->" + this.REagent.getAID()+","+this.REagent.getAgentRate());
		
		if (parent != null) {
			DataStore ds = getDataStore();
			ds.put(((AchieveREResponder) parent).RESULT_NOTIFICATION_KEY, response);
		} else {
			myAgent.send(response);
		}
	}
	
	
	/*@Override
	public void action() {
		ACLMessage response = clientRequest.createReply();
		response.setPerformative(ACLMessage.INFORM);
		
		DFAgentDescription[] sellers = this.REagent.searchForSeller();
		String content = "";
		for(DFAgentDescription dfa : sellers) { //do selection of sellers
			content+=dfa.getName() + ",";
		}
		response.setContent(content);
		
		if (parent != null) {
			DataStore ds = getDataStore();
			ds.put(((AchieveREResponder) parent).RESULT_NOTIFICATION_KEY, response);
		} else {
			myAgent.send(response);
		}
		done = true;
	}

	@Override
	public boolean done() {
		return done;
	}*/
}

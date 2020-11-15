package behaviours;
import java.util.ArrayList;
import java.util.Random;
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
		Vector<Integer> possible = new Vector<Integer>();
		for(int i = 0; i < sellers.length;i++)
			possible.add(i);
		Random rnd = new Random();
		for(int i = 0; i < this.REagent.getMaxSellers() && possible.isEmpty() == false;i++) {
			int rand = rnd.nextInt(possible.size());
			int index = possible.get(rand);
			DFAgentDescription s = sellers[index];
			ACLMessage r = (ACLMessage) request.clone();
			AID aid = s.getName();
			r.addReceiver(aid);
			requests.add(r);
			possible.remove(rand);
		}
		this.REagent.log("A pedir casas aos sellers");
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
				if(factor >= 0.7 && factor <= 1.6) { //não vou sugerir nem casas fracas nem casas muito bai
					AID sender = response.getSender();
					content+= sender + ",";
					accepted++;
				}
			}
		}
		
		this.REagent.logTo("A enviar sellers de volta", clientRequest.getSender());
		
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
	
}

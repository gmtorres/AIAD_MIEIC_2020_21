package behaviours;
import java.io.StringReader;
import java.util.Vector;

import agents.Buyer;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLCodec.CodecException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.StringACLCodec;
import jade.proto.AchieveREInitiator;

public class BuyerContactsAgency extends AchieveREInitiator {
	
	Buyer buyer;
	
	public BuyerContactsAgency(Buyer a, ACLMessage msg) {
		super(a, msg);
		buyer = a;
	}
	
	protected Vector prepareRequests(ACLMessage request) {
		Vector requests = new Vector<ACLMessage >();
		DFAgentDescription[] agencies = this.buyer.searchForAgencies();
		for (int i = 0; i < agencies.length; i++) {
			ACLMessage r = (ACLMessage) request.clone();
			String name = agencies[i].getName().getName();
			r.addReceiver(agencies[i].getName());
			r.setContent(this.buyer.getDesiredProperty().toString());
			requests.add(r);
		}
		return requests;
	}
	
	protected void handleAgree(ACLMessage agree) {
		//System.out.println(agree);
	}
	
	protected void handleInform(ACLMessage inform) {
		//System.out.println("BUYER:\n"+inform);
		String content = inform.getContent();
		if(content == null || (content != null && content.equals("")))
			return;
		String[] parts = content.split("->");
		
		String id = parts[1].split(",")[0];
		int rate = Integer.parseInt(parts[1].split(",")[1]);
		
		String[] sellers = parts[0].split(",");
		
		StringACLCodec codec_temp = new StringACLCodec(new StringReader(id), null);
		AID reagent = new AID();
		try {
			reagent = codec_temp.decodeAID();
		} catch (CodecException e) {
			e.printStackTrace();
		}
		
		for(String s: sellers) {
			if(s.isBlank() || s.isEmpty())
				continue;
			StringACLCodec codec = new StringACLCodec(new StringReader(s), null);
			AID new_aid = new AID();
			try {
				new_aid = codec.decodeAID();
			} catch (CodecException e) {
				e.printStackTrace();
			}
			this.buyer.addSeller(new_aid,reagent,rate);
		}
	}
	
	public int onEnd() {
		this.buyer.initiateSellersInteraction();
		return 0;
	}

}

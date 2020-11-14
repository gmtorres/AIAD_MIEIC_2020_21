import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREResponder;

public class Test extends Behaviour {
	ACLMessage clientRequest;
	RealEstateAgency agency;
	
	Test(ACLMessage r, RealEstateAgency a){
		this.clientRequest = r;
		this.agency = a;
	}
	@Override
	public void action() {
		ACLMessage response = clientRequest.createReply();
		response.setPerformative(ACLMessage.INFORM);
		//custom code goes here
		
		response.setContent("A enviar os valores");
	
		if (parent != null) {
			DataStore ds = getDataStore();
			ds.put(((AchieveREResponder) parent).RESULT_NOTIFICATION_KEY, response);
		} else {
			myAgent.send(response);
		}
	}

	@Override
	public boolean done() {
		return true;
	}
}

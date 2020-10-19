import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Buyer extends Agent{
	
	public void setup() {
		searchForSeller();
	}
	
	public void searchForSeller() {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("seller");
		template.addServices(sd);
		try {
			DFAgentDescription [] result = DFService.search(this, template);
			for(int i = 0; i < result.length; i++) {
				System.out.println("Found " + result[i].getName());
			}
			if(result.length == 0) {
				System.out.println("No results found");
			}
		}catch(FIPAException fe) {
			fe.printStackTrace();
		}
	}
}

import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.core.Profile;
import jade.core.ProfileImpl;


public class Main {
	
	private static int n_sellers = 5;
	private static int n_buyers = 1;
	private static int n_reagencies = 0;
	private static int n_reagents = 0;
	
	public static void main(String[] str) throws StaleProxyException {
		Runtime rt = Runtime.instance();
		Profile profile = new ProfileImpl();
		profile.setParameter("gui","true");
		//profile.setParameter();
		ContainerController cc = rt.createMainContainer(profile);
		AgentController agc;
		try {
			agc = cc.createNewAgent("Sniffer","jade.tools.sniffer.Sniffer",null);
			agc.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createSellers(cc);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		createBuyers(cc);
	}
	
	private static void createSellers(ContainerController cc) {
		for(int i = 0; i < n_sellers; i++) {
			AgentController agc;
			try {
				agc = cc.createNewAgent("Vendedor_" + String.valueOf(i),"Seller",null);
				agc.start();
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private static void createBuyers(ContainerController cc) {
		for(int i = 0; i < n_buyers; i++) {
			AgentController agc;
			try {
				agc = cc.createNewAgent("Comprador_" + String.valueOf(i),"Buyer",null);
				agc.start();
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void createAgencies (ContainerController cc) {
		for(int i = 0; i < n_reagencies; i++) {
			AgentController agc;
			try {
				agc = cc.createNewAgent("Agencia_" + String.valueOf(i),"RealEstateAgency",null);
				agc.start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void createRealEstateAgents (ContainerController cc) {
		for(int i = 0; i < n_reagents; i++) {
			AgentController agc;
			try {
				agc = cc.createNewAgent("RealEstateAgent_" + String.valueOf(i), "RealEstateAgent", null);
				agc.start();
			} catch (StaleProxyException e ) {
				e.printStackTrace();
			}
		}
	}
	
}

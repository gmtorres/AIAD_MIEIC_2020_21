import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.core.Profile;
import jade.core.ProfileImpl;


public class Main {
	
	public static void main(String[] str) throws StaleProxyException {
		Runtime rt = Runtime.instance();
		Profile profile = new ProfileImpl();
		profile.setParameter("gui","true");
		//profile.setParameter();
		ContainerController cc = rt.createMainContainer(profile);
		AgentController agc;
		createSeller(cc);
		createBuyer(cc);
	}
	
	private static void createSeller(ContainerController cc) {
		AgentController agc;
		try {
			agc = cc.createNewAgent("Vendedor","Seller",null);
			agc.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void createBuyer(ContainerController cc) {
		AgentController agc;
		try {
			agc = cc.createNewAgent("Comprador","Buyer",null);
			agc.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

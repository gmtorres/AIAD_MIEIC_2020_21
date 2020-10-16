import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jade.core.Profile;
import jade.core.ProfileImpl;


public class Main {
	
	public static void main(String[] str) {
		Runtime rt = Runtime.instance();
		Profile profile = new ProfileImpl();
		profile.setParameter("gui","true");
		//profile.setParameter();
		ContainerController cc = rt.createMainContainer(profile);
		AgentController agc;
		try {
			agc = cc.createNewAgent("Joao","Seller",null);
			agc.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

import jade.core.Agent;
import java.util.Random;

public class RealEstateAgent extends Agent{
    private RealEstateAgency agency = null;
    private int agentRate;

    RealEstateAgent(){
    	Random rnd = new Random();
        this.agentRate = rnd.nextInt(10);
    }

    public void setAgency(RealEstateAgency agency) {
        this.agency = agency;
    }

    public void removeAgency() {
        this.agency = null;
    }
    
    public int getAgentRate() {
    	return this.agentRate;
    }

}

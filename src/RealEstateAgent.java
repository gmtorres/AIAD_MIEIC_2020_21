import jade.core.Agent;

public class RealEstateAgent extends Agent{
    private RealEstateAgency agency = null;
    private float agentRate;

    RealEstateAgent(float agentRate){
        this.agentRate = agentRate;
    }

    public void setAgency(RealEstateAgency agency) {
        this.agency = agency;
    }

    public void removeAgency() {
        this.agency = null;
    }
    
}

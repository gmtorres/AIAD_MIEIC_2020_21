import java.util.ArrayList;
import java.util.List;

import jade.core.Agent;

public class RealEstateAgency extends Agent{
    private List<RealEstateAgent> agents;
    private float minAgentRate;
    private float maxAgentRate;
    private int teamSize;

    RealEstateAgency(int size, float minAgentRate, float maxAgentRate, int teamSize) {
        agents = new ArrayList<RealEstateAgent>();
        this.minAgentRate = minAgentRate;
        this.maxAgentRate = maxAgentRate;
    }

    public int getTeamSize() {
        return agents.size();
    }

    public void addAgent(RealEstateAgent agent) {
        if (this.getTeamSize() < this.teamSize) {
            agents.add(agent);
        }
    }

    public int getMaxSize() {
        return this.teamSize;
    }

    public void removeAgent (RealEstateAgency agent) {
        agents.remove(agent);
    }

    public float getAgentMinRate() {
        return this.minAgentRate;
    }

    public float getMaxAgentRate() {
        return this.maxAgentRate;
    }

}

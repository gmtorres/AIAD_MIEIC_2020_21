import java.util.ArrayList;
import java.util.Random;
import jade.core.Agent;

public class RealEstateAgency extends Agent{
    private ArrayList<RealEstateAgent> agents;
    private int minAgentRate;
    private int maxAgentRate;
    private int teamSize;

    RealEstateAgency(int size, float minAgentRate, int teamSize) {
		Random rnd = new Random();
        agents = new ArrayList<RealEstateAgent>();
        this.minAgentRate = rnd.nextInt(5);
        this.maxAgentRate = rnd.nextInt(4) + 6;
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

    public int getAgentMinRate() {
        return this.minAgentRate;
    }

    public int getAgentMaxRate() {
        return this.maxAgentRate;
    }

}
package agents;

import jade.core.AID;
import jade.core.Agent;

public class Logger extends Agent {
	
	public void log(String str) {
		System.out.println(this.getLocalName() + " SAID: " + str);
	}
	public void logFrom(String str,AID aid) {
		System.out.println(this.getLocalName() + " RECEIVED: " + str + "  FROM: " + aid.getLocalName() );
	}
	public void logTo(String str,AID aid) {
		System.out.println("FROM: " + this.getLocalName() + ": " + str + " TO: " + aid.getLocalName());
	}
	
}

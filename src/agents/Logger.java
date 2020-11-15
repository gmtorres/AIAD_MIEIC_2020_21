package agents;

import jade.core.AID;
import jade.core.Agent;
import main.Main;

public class Logger extends Agent {
	
	public void log(String str) {
		if(Main.DEBUG)
			System.out.println(this.getLocalName() + " SAID: " + str);
	}
	public void logFrom(String str,AID aid) {
		if(Main.DEBUG)
			System.out.println(this.getLocalName() + " RECEIVED: " + str + "  FROM: " + aid.getLocalName() );
	}
	public void logTo(String str,AID aid) {
		if(Main.DEBUG)
			System.out.println("FROM: " + this.getLocalName() + ": " + str + " TO: " + aid.getLocalName());
	}
	
}

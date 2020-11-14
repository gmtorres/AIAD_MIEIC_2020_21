package main;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.Random;

import agents.*;
import jade.core.Profile;
import jade.core.ProfileImpl;


public class Main {
	
	private static int n_sellers = 100; //between 1 and 40
	private static int n_buyers = 20;  //between 1 and 40

	private static int n_reagencies = 1;
	private static int n_reagents = 5;
	
	//seller
	private static double ratio_patient = 0.2; //between 0 and 1
	private static double ratio_normal_patient = 0.5; //between 0 and 1
	private static double ratio_impatient = 1 - ratio_patient - ratio_normal_patient; //between 1 and 40
	private static double ratio_desperate = 0.2;
	private static double ratio_normal_money = 1 - ratio_desperate;
	private static double ratio_flexible = 0.2;
	private static double ratio_normal_change = 1 - ratio_flexible;
	
	//buyer
	private static double ratio_hurry = 0.2; //between 0 and 1
	private static double ratio_normal_calm = 0.5; //between 0 and 1
	private static double ratio_best = 1 - ratio_hurry - ratio_normal_calm; //between 1 and 40

	
	private static ArrayList<Seller> sellers = new ArrayList();
	private static ArrayList<Buyer> buyers = new ArrayList();
	
	private static ArrayList<RealEstateAgent> reagents = new ArrayList();

	static Random rnd;
	static {
		rnd = new Random();
	}
	
	public static void main(String[] str) throws StaleProxyException {
		Runtime rt = Runtime.instance();
		Profile profile = new ProfileImpl();
		profile.setParameter("gui","true");
		//profile.setParameter("ratio_patient", String.valueOf(ratio_patient));
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
		
		createAgencies(cc);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		createAgents(cc);
		
		waitForAgents();
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		createBuyers(cc);
		
		getInteractions();
		
		System.out.println("\n\n");
		cc.kill();
		rt.shutDown();
		
	}
	
	private static void waitForAgents() {
		int reas_done = 0;
		while(reas_done < buyers.size()) {
			reas_done = 0;
			for(RealEstateAgent rea : reagents)
				if(rea.done())
					reas_done++;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(reas_done);
		}
	}
	
	private static void getInteractions() {
		int buyers_done = 0;
		while(buyers_done < buyers.size()) {
			buyers_done = 0;
			for(Buyer b : buyers)
				if(b.done())
					buyers_done++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(buyers_done);
		}
	}
	
	private static void createSellers(ContainerController cc) {
		for(int i = 0; i < n_sellers; i++) {
			AgentController agc;
			double[] temp1 = {ratio_patient,ratio_normal_patient,ratio_impatient};
			int p = getInterval(temp1);
			double[] temp2 = {ratio_desperate,ratio_normal_money};
			int m = getInterval(temp2);
			double[] temp3 = {ratio_flexible,ratio_normal_change};
			int c = getInterval(temp3);
			Seller seller = new Seller(p,m,c);
			try {		
				agc = cc.acceptNewAgent("Vendedor_" + String.valueOf(i),seller);
				agc.start();
				sellers.add(seller);
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private static void createBuyers(ContainerController cc) {
		for(int i = 0; i < n_buyers; i++) {
			AgentController agc;
			double[] temp1 = {ratio_hurry,ratio_normal_calm,ratio_best};
			int p = getInterval(temp1);
			Buyer buyer = new Buyer(p);
			try {
				agc = cc.acceptNewAgent("Comprador_" + String.valueOf(i),buyer);
				agc.start();
				buyers.add(buyer);
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void createAgencies (ContainerController cc) {
		for(int i = 0; i < n_reagencies; i++) {
			AgentController agc;
			RealEstateAgency agency = new RealEstateAgency();
			try {
				agc = cc.acceptNewAgent("Agencia_" + String.valueOf(i),agency);
				agc.start();
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void createAgents (ContainerController cc) {
		for(int i = 0; i < n_reagents; i++) {
			AgentController agc;
			try {
				RealEstateAgent rea = new RealEstateAgent();
				agc = cc.acceptNewAgent("RealEstateAgent_" + String.valueOf(i), rea);
				agc.start();
				reagents.add(rea);
			} catch (StaleProxyException e ) {
				e.printStackTrace();
			}
		}
	}
	
	private static int getInterval(double values[]) {
		double r = rnd.nextDouble();
		int i = 0;
		for(; i < values.length-1;i++) {
			if(r >= values[i] && r <= values[i+1])
				return i;
		}
		return i;
	}
	
}

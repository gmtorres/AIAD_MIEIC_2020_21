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
	
	private static int n_sellers = 200; //between 1 and 40
	private static int n_buyers = 50;  //between 1 and 40

	private static int n_reagencies = 2;
	private static int n_reagents = 10;
	
	//seller
	private static double ratio_patient = 1; //between 0 and 1
	private static double ratio_normal_patient = 0; //between 0 and 1
	private static double ratio_impatient = 1 - ratio_patient - ratio_normal_patient; //between 1 and 40
	private static double ratio_desperate = 1;
	private static double ratio_normal_money = 1 - ratio_desperate;
	private static double ratio_flexible = 1;
	private static double ratio_normal_change = 1 - ratio_flexible;
	
	//buyer
	private static double ratio_hurry = 1; //between 0 and 1
	private static double ratio_normal_calm = 0; //between 0 and 1
	private static double ratio_best = 1 - ratio_hurry - ratio_normal_calm; //between 1 and 40

	
	private static ArrayList<Seller> sellers = new ArrayList<Seller>();
	private static ArrayList<Buyer> buyers = new ArrayList<Buyer>();
	
	private static ArrayList<RealEstateAgent> reagents = new ArrayList<RealEstateAgent>();

	static Random rnd;
	static {
		rnd = new Random();
	}
	
	public static void main(String[] str) throws StaleProxyException, InterruptedException {
		Runtime rt = Runtime.instance();
		Profile profile = new ProfileImpl();
		profile.setParameter("gui","true");
		
		run(rt,profile);
		
		rt.shutDown();
		
	}
	
	private static void run(Runtime rt, Profile profile) throws StaleProxyException, InterruptedException {
		sellers = new ArrayList<Seller>();
		buyers = new ArrayList<Buyer>();
		reagents = new ArrayList<RealEstateAgent>();
		
		ContainerController cc = rt.createMainContainer(profile);
		
		createSellers(cc);
		createAgencies(cc);
		
		Thread.sleep(100);
		
		createAgents(cc);
		
		waitForAgents();
		
		Thread.sleep(100);
		
		createBuyers(cc);
		
		waitInteractions();
		getStatistics();
		
		cc.kill();
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
			//System.out.println(reas_done);
		}
	}
	
	private static void waitInteractions() {
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
			//System.out.println(buyers_done);
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
	
	private static void getStatistics() {
		System.out.println("Buyers: " + buyers.size());
		System.out.println("Sellers: " + sellers.size());
		
		int buyers_with_house = 0;
		for(Buyer b : buyers) {
			if(b.getProperty() != null)
				buyers_with_house++;
		}
		System.out.println("Buyers with property: " + 100*(double)buyers_with_house/(double)buyers.size() + "% which is " + buyers_with_house + " buyers");
		
		int buyers_with_profit = 0;
		for(Buyer b : buyers) {
			if(b.getInitialMoney() < b.getTotalValue())
				buyers_with_profit++;
		}
		System.out.println("Buyers with profit from those who bought: " + 100*(double)buyers_with_profit/(double)buyers_with_house + "%");
		
		int sellers_sold = 0;
		for(Seller s : sellers) {
			if(s.getOldProperty() != null)
				sellers_sold++;
		}
		System.out.println("Sellers that sold the property: " + 100*(double)sellers_sold/(double)sellers.size() + "% which is " + sellers_sold + " sellers");
		
		int sellers_with_profit = 0;
		for(Seller s : sellers) {
			if(s.getOldProperty() != null && s.getOldProperty().getPrice() < s.getMoney())
				sellers_with_profit++;
		}
		System.out.println("Sellers with profit from those who sold their property: " + 100*(double)sellers_with_profit/(double)sellers_sold + "%");
		System.out.println("\n");
		int buyers_money_before = 0;
		int buyers_money_now = 0;
		int buyers_bought_money_before = 0;
		int buyers_bought_money_now = 0;
		
		for(Buyer b : buyers) {
			buyers_money_before += b.getInitialMoney();
			buyers_money_now += b.getTotalValue();
			if(b.getProperty() != null) {//comprou casa
				buyers_bought_money_before += b.getInitialMoney();
				buyers_bought_money_now += b.getTotalValue();
			}
		}
		
		
		System.out.println("Money in buyers before: " + buyers_money_before + "€");
		System.out.println("Money in buyers now: " + buyers_money_now + "€");
		double b_relation = 100*(double)(buyers_money_now - buyers_money_before) / (double)buyers_money_before;
		System.out.println("Money difference in buyers: " + (buyers_money_now - buyers_money_before) + "€ which is " + b_relation + "%");
		
		System.out.println("Money in buyers that bought before: " + buyers_bought_money_before + "€");
		System.out.println("Money in buyers that bought now: " + buyers_bought_money_now + "€");
		double b_relation_bought = 100*(double)(buyers_bought_money_now - buyers_bought_money_before) / (double)buyers_bought_money_before;
		System.out.println("Money difference in buyers that bought: " + (buyers_bought_money_now - buyers_bought_money_before) + "€ which is " + b_relation_bought + "%");
		
		System.out.println("\n");
		
		
		int sellers_money_before = 0;
		int sellers_money_now = 0;
		int sellers_sold_money_before = 0;
		int sellers_sold_money_now = 0;
		
		for(Seller s : sellers) {
			sellers_money_before += (s.getOldProperty() == null) ? s.getProperty().getPrice() : s.getOldProperty().getPrice();
			sellers_money_now += (s.getOldProperty() == null) ? s.getProperty().getPrice() : s.getMoney();
			if(s.getOldProperty() != null) { // vendeu uma casa
				sellers_sold_money_before += s.getOldProperty().getPrice();
				sellers_sold_money_now += s.getMoney();
			}
		}
		System.out.println("Money in sellers before: " + sellers_money_before + "€");
		System.out.println("Money in sellers now: " + sellers_money_now + "€");
		double s_relation = 100*(double)(sellers_money_now - sellers_money_before) / (double)sellers_money_before;
		System.out.println("Money difference in sellers: " + (sellers_money_now - sellers_money_before) + "€ which is " + s_relation + "%");
		
		System.out.println("Money in sellers that sold before: " + sellers_sold_money_before + "€");
		System.out.println("Money in sellers that sold now: " + sellers_sold_money_now + "€");
		double s_relation_sold = 100*(double)(sellers_sold_money_now - sellers_sold_money_before) / (double)sellers_sold_money_before;
		System.out.println("Money difference in sellers that sold: " + (sellers_sold_money_now - sellers_sold_money_before) + "€ which is " + s_relation_sold + "%");
		
		System.out.println("\n");
		
		int rea_money_now = 0;
		for(RealEstateAgent r : reagents) {
			rea_money_now += r.getMoney();
		}
		double rea_mean = (double)rea_money_now/reagents.size();
		double rea_sd = 0;
		for(RealEstateAgent r : reagents) {
			rea_sd += Math.pow(r.getMoney()-rea_mean,2);
		}
		rea_sd = Math.sqrt(rea_sd/reagents.size());
		
		System.out.println("Money in agents: " + rea_money_now + "€ with a mean of " + rea_mean +"€ per agent and a standard deviation of " + rea_sd);
		
		
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

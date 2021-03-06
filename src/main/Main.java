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
	
	// number of agents
	private static int n_sellers = 100; //greater than 0
	private static int n_buyers = 10;  //greater than 0

	private static int n_reagencies = 3; //greater than 0
	private static int n_reagents = 12; //greater than 0
	
	//seller personality
	private static double ratio_patient = 1; //between 0 and 1
	private static double ratio_normal_patient = 0; //between 0 and 1
	private static double ratio_impatient = 1 - ratio_patient - ratio_normal_patient; //between 0 and 1
	
	private static double ratio_desperate = 1; //between 0 and 1
	private static double ratio_normal_money = 1 - ratio_desperate; //between 0 and 1
	
	private static double ratio_flexible = 0; //between 0 and 1
	private static double ratio_normal_change = 1 - ratio_flexible; //between 0 and 1
	
	//buyer personality
	private static double ratio_hurry = 0; //between 0 and 1
	private static double ratio_normal_calm = 0; //between 0 and 1
	private static double ratio_best = 1 - ratio_hurry - ratio_normal_calm; //between 0 and 1
	
	//RealEstateAgent personality
	private static double ratio_bad_perf = 0; //between 0 and 1
	private static double ratio_normal_perf = 0; //between 0 and 1
	private static double ratio_good_perf = 1 - ratio_bad_perf - ratio_normal_perf; //between 0 and 1
	
	
	private static ArrayList<Seller> sellers = new ArrayList<Seller>();
	private static ArrayList<Buyer> buyers = new ArrayList<Buyer>();
	
	private static ArrayList<RealEstateAgent> reagents = new ArrayList<RealEstateAgent>();
	private static ArrayList<RealEstateAgency> reagency = new ArrayList<RealEstateAgency>();

	static Random rnd;
	static {
		rnd = new Random();
	}
	
	public static boolean DEBUG = false;
	
	public static void main(String[] args) throws StaleProxyException, InterruptedException {
		
		if(args.length == 13) {
			n_sellers = Integer.parseInt(args[0]);
			n_buyers = Integer.parseInt(args[1]);
			n_reagencies = Integer.parseInt(args[2]);
			n_reagents = Integer.parseInt(args[3]);
			ratio_patient = Double.parseDouble(args[4]);
			ratio_normal_patient = Double.parseDouble(args[5]);
			ratio_impatient = 1 - ratio_patient - ratio_normal_patient;
			ratio_desperate = Double.parseDouble(args[6]);
			ratio_normal_money = 1 - ratio_desperate;
			ratio_flexible = Double.parseDouble(args[7]);
			ratio_normal_change = 1 - ratio_flexible;
			ratio_hurry = Double.parseDouble(args[8]);
			ratio_normal_calm = Double.parseDouble(args[9]);
			ratio_best = 1 - ratio_hurry - ratio_normal_calm;
			ratio_bad_perf = Double.parseDouble(args[10]);
			ratio_normal_perf = Double.parseDouble(args[11]);
			ratio_good_perf = 1 - ratio_bad_perf - ratio_normal_perf;
			DEBUG = Boolean.valueOf(args[12]);
			if(ratio_impatient > 1 || ratio_impatient < 0 || ratio_best>1 || ratio_best < 0) {
				System.out.println("Sum of ratios must be between 0 and 1");
				System.exit(-2);
			}
		}else if(args.length != 0) {
			System.out.println("Arguments must be: <n_sellers> <n_buyers>"
					+ " <n_agencies> <n_reagents> <ratio_patient> <ratio_normal_patient>"
					+ " <ratio_desperate>  <ratio_flexible> <ratio_hurry> <ratio_normal_calm> <ratio_bad_perf> <ratio_normal_perf> <ratio_good_perf>");
			System.exit(-1);
		}
		
		
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
		reagency = new ArrayList<RealEstateAgency>();
		
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
				reagency.add(agency);
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void createAgents (ContainerController cc) {
		for(int i = 0; i < n_reagents; i++) {
			AgentController agc;
			double[] temp1 = {ratio_bad_perf,ratio_normal_perf,ratio_good_perf};
			int p = getInterval(temp1);
			try {
				RealEstateAgent rea = new RealEstateAgent(p);
				agc = cc.acceptNewAgent("RealEstateAgent_" + String.valueOf(i), rea);
				agc.start();
				reagents.add(rea);
			} catch (StaleProxyException e ) {
				e.printStackTrace();
			}
		}
	}
	
	private static void getStatistics() {
		System.out.println("\n\n");
		
		System.out.print("Patient Sellers: " + 100*ratio_patient + "%");
		System.out.print("\tNormal patience Sellers: " + 100*ratio_normal_patient + "%");
		System.out.println("\tImpatient Sellers: " + 100*ratio_impatient + "%");
		System.out.print("Desperate for money Sellers: " + 100*ratio_desperate + "%");
		System.out.println("\tNormal money Sellers: " + 100*ratio_normal_money + "%");
		System.out.print("Flexible Sellers: " + 100*ratio_flexible + "%");
		System.out.println("\tInflexible Sellers: " + 100*ratio_normal_change + "%");
		
		System.out.print("Hurry buyers: " + 100*ratio_hurry + "%");
		System.out.print("\tCalm buyers: " + 100*ratio_normal_calm + "%");
		System.out.println("\tBest deal buyers: " + 100*ratio_best + "%");
		
		System.out.print("Bad performance agents: " + 100*ratio_bad_perf + "%");
		System.out.print("\tNormal performance agents: " + 100*ratio_normal_perf + "%");
		System.out.println("\tGood performance agents: " + 100*ratio_good_perf + "%");
		
		
		System.out.println("");
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
		System.out.println("");
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
		
		
		System.out.println("Money in buyers before: " + buyers_money_before + "�");
		System.out.println("Money in buyers now: " + buyers_money_now + "�");
		double b_relation = 100*(double)(buyers_money_now - buyers_money_before) / (double)buyers_money_before;
		System.out.println("Money difference in buyers: " + (buyers_money_now - buyers_money_before) + "� which is " + b_relation + "%");
		
		System.out.println("Money in buyers that bought before: " + buyers_bought_money_before + "�");
		System.out.println("Money in buyers that bought now: " + buyers_bought_money_now + "�");
		double b_relation_bought = 100*(double)(buyers_bought_money_now - buyers_bought_money_before) / (double)buyers_bought_money_before;
		System.out.println("Money difference in buyers that bought: " + (buyers_bought_money_now - buyers_bought_money_before) + "� which is " + b_relation_bought + "%");
		
		System.out.println("");
		
		
		int sellers_money_before = 0;
		int sellers_money_now = 0;
		int sellers_sold_money_before = 0;
		int sellers_sold_with_money_now = 0;
		int sellers_sold_without_money_now = 0;
		
		for(Seller s : sellers) {
			sellers_money_before += (s.getOldProperty() == null) ? s.getProperty().getPrice() : s.getOldProperty().getPrice();
			sellers_money_now += (s.getOldProperty() == null) ? s.getProperty().getPrice() : s.getMoney();
			if(s.getOldProperty() != null) { // vendeu uma casa
				sellers_sold_money_before += s.getOldProperty().getPrice();
				sellers_sold_with_money_now += s.getTotalWithRate();
				sellers_sold_without_money_now += s.getTotalWithoutRate();
			}
		}
		System.out.println("Money in sellers before: " + sellers_money_before + "�");
		System.out.println("Money in sellers now: " + sellers_money_now + "�");
		double s_relation = 100*(double)(sellers_money_now - sellers_money_before) / (double)sellers_money_before;
		System.out.println("Money difference in sellers: " + (sellers_money_now - sellers_money_before) + "� which is " + s_relation + "%");
		
		System.out.println("Money in sellers that sold before: " + sellers_sold_money_before + "�");
		System.out.println("Money in sellers that sold now with rates: " + sellers_sold_with_money_now + "�");
		System.out.println("Money in sellers that sold now without rates: " + sellers_sold_without_money_now + "�");
		double s_relation_sold_with = 100*(double)(sellers_sold_with_money_now - sellers_sold_money_before) / (double)sellers_sold_money_before;
		double s_relation_sold_without = 100*(double)(sellers_sold_without_money_now - sellers_sold_money_before) / (double)sellers_sold_money_before;
		System.out.println("Money difference with rates in sellers that sold: " + (sellers_sold_with_money_now - sellers_sold_money_before) + "� which is " + s_relation_sold_with + "%");
		System.out.println("Money difference without rates in sellers that sold: " + (sellers_sold_without_money_now - sellers_sold_money_before) + "� which is " + s_relation_sold_without + "%");
		
		System.out.println("");
		
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
		
		System.out.println("Real estate agents: " + reagents.size());
		System.out.println("Money in agents: " + rea_money_now + "� with a mean of " + rea_mean +"� per agent and a standard deviation of " + rea_sd);
		
		System.out.println("");
		int agen_money_now = 0;
		for(RealEstateAgency r : reagency) {
			agen_money_now += r.getMoney();
		}
		double agen_mean = (double)rea_money_now/reagency.size();
		double agen_sd = 0;
		for(RealEstateAgency r : reagency) {
			agen_sd += Math.pow(r.getMoney()-agen_mean,2);
		}
		agen_sd = Math.sqrt(agen_sd/reagency.size());
		
		System.out.println("Real estate agency: " + reagency.size());
		System.out.println("Money in agents: " + agen_money_now + "� with a mean of " + agen_mean +"� per agency and a standard deviation of " + agen_sd);

		
		
	}
	
	private static int getInterval(double values[]) {
		double r = rnd.nextDouble();
		int i = 0;
		double x = 0;
		for(; i < values.length-1;i++) {
			if(r >= x && r <= x+values[i])
				return i;
			x+=values[i];
		}
		return i;
	}
	
}

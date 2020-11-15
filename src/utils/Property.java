package utils;
import java.util.Random;
import java.util.ArrayList;


public class Property implements java.io.Serializable{
	
	private static Random rnd = new Random();
	
	private int price = 0;
	
	ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	
	public Property(){ // generate a house with random attributes
		initializeAttributes(true);
		calculatePropertyPrice();
	}
	
	/*public Property(int area_built, int area_garden, int num_bed, int num_wc){ // generate a house with attributes passed by parameters
		initializeAttributes(false);
		this.setAttribute("area_built",area_built);
		this.setAttribute("area_garden",area_garden);
		this.setAttribute("num_bedrooms",num_bed);
		this.setAttribute("num_wc",num_wc);
		calculatePropertyPrice();
	}*/
	
	public Property(String description){ // generate a generic house from a string, does not set the house price
		initializeAttributes(false);
		String [] entries = description.split(",");
		for(int i = 0; i < entries.length; i++) {
			this.parseEntry(entries[i]);
		}
	}
	public void setPropertyPrice(int p) {
		this.price = p;
	}
	
	private void calculatePropertyPrice() {
		double usage = rnd.nextDouble() * 0.1 + 0.95; //desgaste ou bom cuidado da casa
		int standard_evaluation = this.evaluateHouse();
		setPrice((int)(standard_evaluation * usage));
	}
	
	private void initializeAttributes(boolean random) {
		attributes.add(new Attribute("area_built",600, random ? rnd.nextInt(350) + 50 : 0 ,0.2));
		attributes.add(new Attribute("area_garden",300, random ? rnd.nextInt(150) + 0 : 0 ,0.2));
		attributes.add(new Attribute("num_bedrooms",1500,random ? rnd.nextInt(5) + 1 : 0 ,0.7));
		attributes.add(new Attribute("num_wc",1000, random ? rnd.nextInt(4) + 1 : 0 ,0.5));
		//attributes.add(new Attribute("attic",2000, random ? rnd.nextInt(2) + 0 : 0 ,0.25));
		//attributes.add(new Attribute("basement",1700, random ? rnd.nextInt(2) + 0 : 0 ,0.25));
		attributes.add(new Attribute("parking_spots",1050, random ? rnd.nextInt(3) + 0 : 0 ,0.5));
		attributes.add(new Attribute("heating",6000, random ? rnd.nextInt(2) + 0 : 0 ,1));
		//attributes.add(new Attribute("energy_efficiency",6000, random ? rnd.nextInt(2) + 0 : 0 ,0.25));
	}
	
	private void setAttribute(String d, int value) {
		for(int i = 0; i < this.attributes.size();i++) {
			Attribute att = this.attributes.get(i);
			if(att.getDescription().equals(d)) {
				att.setValue(value);
				return;
			}
		}
	}
	
	public int evaluateHouse(){
		int evaluation = 0;
		for(int i = 0; i < this.attributes.size();i++) {
			Attribute att = this.attributes.get(i);
			evaluation += att.getValue() * att.getPrice();
		}
		return evaluation;
	}
	
	static public double relativePropertyDifference(Property p1, Property p2) {
		double r = 1;
		for(int i = 0; i < p1.attributes.size();i++) {
			double w = p1.attributes.get(i).getWeight();
			double att1 = (double)p1.attributes.get(i).getValue() * w;
			double att2 = (double)p2.attributes.get(i).getValue() * w;
			if(att1 == 0 || att2 == 0) {
				att1++;
				att2++;
			}
			double relation = Math.sqrt(att2)/ Math.sqrt(att1);
			if(relation > 1) // melhor
				r *= Math.sqrt(relation);
			else //pior
				r *= relation;
		}
		//System.out.println("      "+p1+"  "+p2+"  "+r);
		return r;
	}
	
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	
	private void parseEntry(String description) {
		String [] parts = description.split(":");
		this.setAttribute(parts[0], Integer.parseInt(parts[1]));
	}
	
	public String toString() {
		String str = "";
		for(int i = 0; i < this.attributes.size();i++) {
			Attribute att = this.attributes.get(i);
			if(i!= 0)
				str+=",";
			str+=att.getDescription()+":"+att.getValue();
		}
		return str;
	}
	
	
	class Attribute{
		private String description;
		private int value;
		private int price;
		private double weight;
		
		Attribute(String d, Integer price, Integer vl, double weight){
			this.setDescription(d);
			this.setPrice(price);
			this.setValue(vl);
			this.setWeight(weight);
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Integer getValue() {
			return value;
		}
		public void setValue(Integer value) {
			this.value = value;
		}
		public int getPrice() {
			return price;
		}
		public void setPrice(int price) {
			this.price = price;
		}
		public double getWeight() {
			return 1/weight;
		}
		public void setWeight(double weight) {
			this.weight = weight;
		}
		
	}
	
	
}

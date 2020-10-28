import java.util.Random;
import java.util.ArrayList;


public class Property implements java.io.Serializable{
	
	private static Random rnd = new Random();
	
	private int price;
	
	ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	
	private Integer area_built;
	private Integer area_garden;
	private Integer num_bedrooms;
	private Integer num_wc;
	//private boolean heating;
	
	Property(){ // generate a house with random attributes
		initializeAttributes(true);
		calculatePropertyPrice();
	}
	
	Property(int area_built, int area_garden, int num_bed, int num_wc){ // generate a house with attributes passed by parameters
		initializeAttributes(false);
		this.setAttribute("area_built",area_built);
		this.setAttribute("area_garden",area_garden);
		this.setAttribute("num_bedrooms",num_bed);
		this.setAttribute("num_wc",num_wc);
		calculatePropertyPrice();
	}
	
	Property(String description){ // generate a generic house from a string, does not set the house price
		initializeAttributes(false);
		String [] entries = description.split(",");
		for(int i = 0; i < entries.length; i++) {
			this.parseEntry(entries[i]);
		}
	}
	
	private void calculatePropertyPrice() {
		double usage = rnd.nextDouble() * 0.1 + 0.95;
		int standard_evaluation = this.evaluateHouse();
		setPrice((int)(standard_evaluation * usage));
	}
	
	private void initializeAttributes(boolean random) {
		attributes.add(new Attribute("area_built",1500, random ? rnd.nextInt(350) + 50 : 0 ));
		attributes.add(new Attribute("area_garden",800, random ? rnd.nextInt(150) + 5 : 0 ));
		attributes.add(new Attribute("num_bedrooms",400,random ? rnd.nextInt(2) + 1 : 0 ));
		attributes.add(new Attribute("num_wc",2500, random ? rnd.nextInt(2) + 1 : 0 ));
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
		/*int evaluation = (area_built + area_garden) * 800 +
				area_built * 700 +
				num_bedrooms * 4000 +
				num_wc * 2500;*/
		int evaluation = 0;
		for(int i = 0; i < this.attributes.size();i++) {
			Attribute att = this.attributes.get(i);
			evaluation += att.getValue() * att.getPrice();
		}
		evaluation = 100000;
		return evaluation;
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
		/*Integer value;
		switch(parts[0]) {
		case "built":
			value = Integer.parseInt(parts[1]);
			this.area_built = value;
			break;
		case "garden":
			value = Integer.parseInt(parts[1]);
			this.area_garden = value;
			break;
		case "num_bed":
			value = Integer.parseInt(parts[1]);
			this.num_bedrooms = value;
			break;
		case "num_wc":
			value = Integer.parseInt(parts[1]);
			this.num_wc = value;
			break;
		}*/
	}
	
	public String toString() {
		/*return "built:" + this.area_built + 
				",garden:" + this.area_garden +
				",num_bed:" + this.num_bedrooms + 
				",num_wc:" + this.num_wc;*/
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
		
		Attribute(String d, Integer price, Integer vl){
			this.setDescription(d);
			this.setPrice(price);
			this.setValue(vl);
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
		
	}
	
	
}

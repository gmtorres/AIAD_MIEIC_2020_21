import java.util.Random;

public class Property implements java.io.Serializable{
	
	private static Random rnd = new Random();
	
	private int price;
	
	private Integer area_built;
	private Integer area_garden;
	private Integer num_bedrooms;
	private Integer num_wc;
	//private boolean heating;
	Property(){
		this(rnd.nextInt(350) + 50, rnd.nextInt(150), rnd.nextInt(2) + 1, rnd.nextInt(2) + 1);
	}
	
	Property(String description){
		String [] entries = description.split(",");
		for(int i = 0; i < entries.length; i++) {
			this.parseEntry(entries[i]);
		}
	}
	
	Property(int area_built, int area_garden, int num_bed, int num_wc){
		this.area_built = area_built;
		this.area_garden = area_garden;
		this.num_bedrooms = num_bed;
		this.num_wc = num_wc;
		double usage = rnd.nextDouble() * 0.1 + 0.95;
		int standard_evaluation = this.evaluateHouse();
		setPrice((int)(standard_evaluation * usage));
	}
	
	public int evaluateHouse(){
		int evaluation = (area_built + area_garden) * 800 +
				area_built * 700 +
				num_bedrooms * 4000 +
				num_wc * 2500;
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
		Integer value;
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
		}
	}
	
	public String toString() {
		return "built:" + this.area_built + 
				",garden:" + this.area_garden +
				",num_bed:" + this.num_bedrooms + 
				",num_wc:" + this.num_wc;
	}
}

import java.util.Random;

public class Property implements java.io.Serializable{
	
	private static Random rnd = new Random();
	
	private int price;
	
	private int area_built;
	private int area_garden;
	private int num_bedrooms;
	private int num_wc;
	//private boolean heating;
	Property(){
		this(rnd.nextInt(350) + 50, rnd.nextInt(150), rnd.nextInt(2) + 1, rnd.nextInt(2) + 1);
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
		
		return evaluation;
	}
	
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	
	public String toString() {
		return "This property is evaluated in " + this.getPrice() + "€"; 
	}
}

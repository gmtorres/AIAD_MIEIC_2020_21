import java.util.Random;

public class Property {
	private int price;
	Property(){
		Random rnd = new Random();
		setPrice(rnd.nextInt(100000) + 100000); //100000 - 200000
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
}

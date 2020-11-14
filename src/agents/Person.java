package agents;
import jade.core.Agent;
import utils.Property;

public class Person extends Agent {
	
	private int money;
	private Property property;
	
	public Person() {
		super();
	}
	
	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}
	
	public void increaseMoney(int quantity) {
		this.setMoney(this.getMoney() + quantity);
	}
	
	public Property getProperty() {
		return property;
	}
	
	public void setProperty(Property prop) {
		this.property = prop;
	}
	
	public float getRelativeDifference(int price) {
		return (price - this.getProperty().getPrice()) / this.getProperty().getPrice();
	}
	
	public int getPriceFromRelativeDifference(double relative) {
		return (int) (this.getProperty().getPrice() * relative); 
	}

}

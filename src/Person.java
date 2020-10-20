import jade.core.Agent;

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

}

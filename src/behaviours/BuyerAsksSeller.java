package behaviours;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import agents.Buyer;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import utils.Property;

public class BuyerAsksSeller extends ContractNetInitiator  {
	
	static Random rnd;
	static {
		rnd = new Random();
	}
	
	private Buyer buyer;
	private Property selected_property;
	
	public BuyerAsksSeller(Buyer buyer, ACLMessage cfp) {
		super(buyer, cfp);
		this.buyer = buyer;
		this.selected_property = null;
	}
	

	protected Vector<ACLMessage> prepareCfps(ACLMessage cfp) {
		Vector<ACLMessage> v = new Vector<ACLMessage>();
		//cfp.setContent("Quero comprar!");
		ArrayList<AID> sellers = this.buyer.getSellers();
		for (int i = 0; i < sellers.size();i++) {
			AID s = sellers.get(i);
			ACLMessage msg = (ACLMessage) cfp.clone();
			msg.addReceiver(s);
			msg.setContent(this.buyer.reAgents.get(i) + "," + this.buyer.rates.get(i));
			msg.addUserDefinedParameter(ACLMessage.SF_TIMEOUT, "2000");
			//cfp.addReceiver(s);
			v.add(msg);
		}
		//cfp.addUserDefinedParameter(ACLMessage.SF_TIMEOUT, "2000");
		//v.add(cfp);
		return v;		
	}
	
	protected void handleAllResponses(Vector responses, Vector acceptances) {
		Vector replies = new Vector<>();
		
		double bestFactor = 0;
		int position = -1;
		int valid_proposals = 0;
		
		for (int i = 0; i < responses.size(); i++) { // analyze all proposes
			
			ACLMessage response = (ACLMessage) responses.get(i);
			if(response.getPerformative() != ACLMessage.PROPOSE)
				continue;
			this.buyer.log("O " + response.getSender().getLocalName() + " sugeriu o seguinte preço: " + response.getContent());
			String [] parts = response.getContent().split("/");
			Integer proposed_price = Integer.parseInt(parts[0]);
			Property proposed_property = new Property(parts[1]);
			Integer standard_price = proposed_property.evaluateHouse();
			//System.out.println("Seller sugeriu " + proposed_price);
			
			// diferença relativa entre o preço proposto e o que o buyer tem, se for muito maior, não vale a pena negociar
			double relativeDifference = (double)(proposed_price - this.buyer.getMoney() ) / (double)this.buyer.getMoney();
			double relativePropertyValue = Property.relativePropertyDifference(this.buyer.getDesiredProperty(), proposed_property);
			
			if(relativeDifference > 0.4 || relativePropertyValue < 0.7) { // diferença muito alta ou casa muito diferente, não interessado, não vale a pena negociar
				//System.out.println("Preço muito alto ou casa muito diferente, não vou negociar " + relativeDifference + "  " + relativePropertyValue);
				ACLMessage reply = response.createReply();
				reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
				this.buyer.log("A proposta do" + response.getSender().getLocalName() + " nao me agrada");
				replies.add(reply);
				continue; 
			}else { // negociar ou se diferença < 0 posso aceitar ou negociar
				//System.out.println("Vou negociar");
				double relativePropertyPrice = (double)standard_price / (double)proposed_price;

				double factor = relativePropertyPrice * relativePropertyValue; // quality for the price
				
				if(relativeDifference > 0) { // preciso mesmo de negociar para baixo, não tenho dinheiro
					//System.out.println("Preço acima do que posso pagar, vou tentar descer");
					ACLMessage reply = response.createReply();
					reply.setPerformative(ACLMessage.CFP);
					Integer diff;
				
					diff = (int) (
								( 1 / relativePropertyPrice + 0.25)
								* (this.generateRandomDistribution(5)*  0.125 + 1) 
								* ((0.2-relativeDifference)/0.2) 
								* (proposed_price - 0.98*this.buyer.getMoney()));
					Integer new_proposal = (int) (this.buyer.getMoney() - diff); // possivelmente ser entre 1 e 1.5
					reply.setContent(Integer.toString(new_proposal));
					replies.add(reply);
					
				}else { // tenho dinheiro, posso aceitar ou negociar
					//System.out.println("Tenho dinheiro, vou avaliar a situação");
					valid_proposals++;
					Integer new_proposal;
					if(relativePropertyPrice < 1) { // preço proposto é maior que o preço da propriedade
						new_proposal = (int)(standard_price - (1 + 0.5) * (proposed_price - standard_price));
					}else { // preço proposto é menor que o preço da propriedade, tentar descer a proposta, mas não muito, para não abusar
						new_proposal = (int) (proposed_price - 0.5*(proposed_price - 0.90 * standard_price));
					}
					ACLMessage reply = response.createReply();
					reply.setPerformative(ACLMessage.CFP);
					reply.setContent(Integer.toString(new_proposal));
					replies.add(reply);
					
					if(factor > bestFactor) {
						bestFactor = factor;
						position = i;
					}
				}	
			}
		}
		
		if(position != -1) { // se tiver dinheiro para aceitar alguma proposta e selecionou alguma
			
			if(bestFactor > this.buyer.getBestFactor() || // boa oferta pelo preço da casa
			   (valid_proposals <  this.buyer.getMinValid() && bestFactor > this.buyer.getWorstFactor())) { // se houverem poucas ofertas, mais vale ficar com uma do que a arder
				
				//System.out.println("Vou aceitar a proposta do vendedor");
				acceptances.clear();
				for(int i = 0; i < responses.size();i++) { // se eu aceitar um pedido, rejeitar todos os outros
					ACLMessage response = ((ACLMessage)responses.get(i));
					if(i == position) { // apenas um accept!
						this.selected_property = new Property(response.getContent().split("/")[1]);
						ACLMessage accept = response.createReply();
						accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
						accept.setContent(response.getContent().split("/")[0]);
						acceptances.add(accept);
					}else {
						ACLMessage reply = response.createReply();
						reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
						acceptances.add(reply);
					}
				}
				
				//System.out.println("Aceitei o preço do " + ((ACLMessage)responses.get(position)).getSender());
			}else { // tenho dinheiro, mas nenhuma oferta é sufecientemente boa
				if(replies.isEmpty() == false) {
					//System.out.println("A mandar nova iteração");
					this.newIteration(replies);
				}	
			}
			
		}else { // não tenho dinheiro para nada, a negociar tudo
			if(replies.isEmpty() == false) {
				//System.out.println("A mandar nova iteração");
				this.newIteration(replies);
			}	
		}
	}
	
	protected void handleFailure(ACLMessage inform) {
		System.out.println("Failure recebido");
		this.reset(new ACLMessage(ACLMessage.CFP));
	}
	
	protected void handleInform(ACLMessage inform) {
		//System.out.println("Inform recebido");
		this.buyer.setProperty(selected_property);
		String [] parts = inform.getContent().split(",");
		Integer price_payed = Integer.parseInt(parts[0]);
		this.buyer.getProperty().setPropertyPrice(Integer.parseInt(parts[1]));
		this.buyer.increaseMoney(-price_payed);
		/*System.out.println("Eu, " + this.buyer.getLocalName() + ", fiquei com " + this.buyer.getMoney() + "€, paguei " + price_payed + "€ por esta casa:\n\t\t " +
							this.buyer.getProperty() + ",\nprocurava esta:\n\t\t " + this.buyer.getDesiredProperty()+
							"\ncom um preço estimado de:\n\t\t" + this.buyer.getDesiredProperty().evaluateHouse() );*/
		this.buyer.log("Comprei uma casa ao " + inform.getSender().getLocalName());
	}
	
	private double generateRandomDistribution(int times) {
		double result = 0;
		for(int i = 0; i < times; i++) {
			result += rnd.nextDouble();
		}
		return result / times;
	}
	
}	

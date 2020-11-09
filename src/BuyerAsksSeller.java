import java.util.Random;
import java.util.Vector;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;

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
		Vector<ACLMessage> v = new Vector<ACLMessage >();
		cfp.setContent("Quero comprar!");
		DFAgentDescription[] sellers = this.buyer.searchForSeller();
		//System.out.println("Vou enviar a proposta a " + sellers.length + " vendedores.");
		for (int i = 0; i < sellers.length; i++) {
			String name = sellers[i].getName().getName();
			//System.out.println(name);
			cfp.addReceiver(sellers[i].getName());
		}
		v.add(cfp);
		//System.out.println("Enviado, siga comprar!");
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
			
			String [] parts = response.getContent().split("/");
			Integer proposed_price = Integer.parseInt(parts[0]);
			Property proposed_property = new Property(parts[1]);
			Integer standard_price = proposed_property.evaluateHouse();
			System.out.println("Seller sugeriu " + proposed_price);
			
			// diferen�a relativa entre o pre�o proposto e o que o buyer tem, se for muito maior, n�o vale a pena negociar
			double relativeDifference = (double)(proposed_price - this.buyer.getMoney() ) / (double)this.buyer.getMoney();
			if(relativeDifference > 0.2) { // diferen�a muito alta, n�o interessado, n�o vale a pena negociar
				System.out.println("Pre�o muito alto, n�o vou negociar");
				ACLMessage reply = response.createReply();
				reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
				acceptances.add(reply);
				continue; 
			}else { // negociar ou se diferen�a < 0 posso aceitar ou negociar
				System.out.println("Vou negociar");
				double relativePropertyPrice = (double)standard_price / (double)proposed_price;
				double relativePropertyValue = Property.relativePropertyDifference(this.buyer.getDesiredProperty(), proposed_property);

				double factor = relativePropertyPrice * relativePropertyValue; // quality for the price
				
				if(relativeDifference > 0) { // preciso mesmo de negociar para baixo, n�o tenho dinheiro
					System.out.println("Pre�o acima do que posso pagar, vou tentar descer");
					ACLMessage reply = response.createReply();
					reply.setPerformative(ACLMessage.CFP);
					Integer diff;
					
					//quanto maior a diferen�a de pre�o, menos posso pedir em compara��o com o meu
					System.out.println("");
					
					System.out.println(proposed_price);
					System.out.println(this.buyer.getMoney());
					System.out.println(standard_price);
					System.out.println( (int) ((this.generateRandomDistribution(5)*  0.5 + 1) * (proposed_price - this.buyer.getMoney())));
					
					System.out.println("");
					
					System.out.println(proposed_price - this.buyer.getMoney());
					System.out.println(relativeDifference);
					System.out.println((0.2-relativeDifference)/0.2);
					System.out.println(((0.2-relativeDifference)/0.2) * (proposed_price - this.buyer.getMoney()) );
					System.out.println((this.generateRandomDistribution(5)*  0.5 + 1) * ((0.2-relativeDifference)/0.2) * (proposed_price - this.buyer.getMoney()) );
					
					System.out.println("");
					
					System.out.println(relativePropertyPrice);
					System.out.println(1/relativePropertyPrice);
					System.out.println(1/relativePropertyPrice + 1);
					System.out.println(proposed_price - standard_price);
					System.out.println((1/relativePropertyPrice + 0.5)*(this.generateRandomDistribution(5)*  0.25 + 1) * ((0.2-relativeDifference)/0.2) * (proposed_price - this.buyer.getMoney()) );
					System.out.println("");
					
					System.out.println("");
					diff = (int) (
								( 1 / relativePropertyPrice + 0.25)
								* (this.generateRandomDistribution(5)*  0.125 + 1) 
								* ((0.2-relativeDifference)/0.2) 
								* (proposed_price - 0.98*this.buyer.getMoney()));
					Integer new_proposal = (int) (this.buyer.getMoney() - diff); // possivelmente ser entre 1 e 1.5
					reply.setContent(Integer.toString(new_proposal));
					replies.add(reply);
					
				}else { // tenho dinheiro, posso aceitar ou negociar
					System.out.println("Tenho dinheiro, vou avaliar a situa��o");
					valid_proposals++;
					Integer new_proposal;
					if(relativePropertyPrice > 1) { // pre�o proposto � maior que o pre�o da propriedade
						new_proposal = (int)(standard_price - (1 + 0.5) * (proposed_price - standard_price));
					}else { // pre�o proposto � menor que o pre�o da propriedade, tentar descer a proposta, mas n�o muito, para n�o abusar
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
			
			if(bestFactor > this.buyer.getBestFactor() || // boa oferta pelo pre�o da casa
			   (valid_proposals <  this.buyer.getMinValid() && bestFactor > this.buyer.getWorstFactor())) { // se houverem poucas ofertas, mais vale ficar com uma do que a arder
				
				System.out.println("Vou aceitar a proposta do vendedor");
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
				
				System.out.println("Aceitei o pre�o do " + ((ACLMessage)responses.get(position)).getSender());
			}else { // tenho dinheiro, mas nenhuma oferta � sufecientemente boa
				if(replies.isEmpty() == false) {
					System.out.println("A mandar nova itera��o");
					this.newIteration(replies);
				}	
			}
			
		}else { // n�o tenho dinheiro para nada, a negociar tudo
			if(replies.isEmpty() == false) {
				System.out.println("A mandar nova itera��o");
				this.newIteration(replies);
			}	
		}
		
		
		
		/*if(position != -1) {
			ACLMessage accept = ((ACLMessage)responses.get(position)).createReply();
			accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			acceptances.add(accept);
			System.out.println("Aceitei o pre�o do " + ((ACLMessage)responses.get(position)).getSender());
		}else {
			System.out.println("N�o aceitei nenhum pre�o");
		}*/
	}
	
	protected void handleFailure(ACLMessage inform) {
		System.out.println("Failure recebido");
		//this.reset(new ACLMessage(ACLMessage.CFP));
	}
	
	protected void handleInform(ACLMessage inform) {
		System.out.println("Inform recebido");
		//System.out.println(inform);
		this.buyer.setProperty(selected_property);
		Integer price_payed = Integer.parseInt(inform.getContent());
		this.buyer.increaseMoney(-price_payed);
		System.out.println("Eu, " + this.buyer.getLocalName() + ", fiquei com " + this.buyer.getMoney() + "�, paguei " + price_payed + "� por esta casa:\n\t\t " +
							this.buyer.getProperty() + ",\nprocurava esta:\n\t\t " + this.buyer.getDesiredProperty()+
							"\ncom um pre�o estimado de:\n\t\t" + this.buyer.getDesiredProperty().evaluateHouse() );
	}
	
	private double generateRandomDistribution(int times) {
		double result = 0;
		for(int i = 0; i < times; i++) {
			result += rnd.nextDouble();
		}
		return result / times;
	}
	
}	

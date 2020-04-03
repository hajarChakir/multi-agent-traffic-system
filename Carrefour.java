package prjtsSMA.appli;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Carrefour extends Agent{
	public String namecar;
	List<AID> Lroutes = new ArrayList<>();
	boolean flag=false;
	boolean flag2=false;
	boolean flag3=false;
	
	String etatfeu="rouge";

	public Carrefour() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Carrefour(String name) {
		super();
		this.namecar=name;
		
	}
	//definition 
	public void setup() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(this.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName("carrefour");
		sd.setType("carrefour");
		dfd.addServices(sd);
		
		addBehaviour(new Behaviour(){
			@Override
			public void action() {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ACLMessage aclMessage = receive();
				if(aclMessage!=null) {
					System.out.println("Carrefour : "+aclMessage.getSender().getName()+" est arrivee !!!!");

				
					if((aclMessage.getSender().getName().split("@"))[0].equals("voiture2")){
						
						String msg=aclMessage.getContent();
						AID requester = aclMessage.getSender();
					System.out.println("recherche des route ");
					Lroutes=chercherServices(myAgent,"route");
					try {
						for (AID aid:Lroutes) {
							System.out.println("routes liees au carrefour B---"+aid.getName());
						}
						String name = aclMessage.getSender().getName();
						String [] T=name.split("@");
						System.out.println("carrefour B: Bonjour "+T[0] +" Quelle route voulez vous choisir ? ");
						ACLMessage msg2=aclMessage.createReply();
				    	msg2.setContent("hh");
				    	send(msg2);
				    	
				    	
					}catch(Exception e) {e.printStackTrace();}
					

					
					addBehaviour(new Behaviour() {

						@Override
						public void action() {
						
						ACLMessage aclMessage2 = receive();
						
					if(aclMessage2!=null) {

								String msg3=aclMessage2.getContent();
								if(msg3.equals("route2")) {
										System.out.println("Carrefour : Vous avez choisis route2 c'est permis !!!");
										if (etatfeu.equals("rouge")) {
											ACLMessage aclMessage =new ACLMessage(ACLMessage.REQUEST);
											 aclMessage.addReceiver(new AID("voiture2",AID.ISLOCALNAME));
											 String msgcarr="arretez";
											 System.out.println("Carrefour : il faut attendre le feux vert");
											 aclMessage.setContent(msgcarr);
											send(aclMessage);
											flag2=true;
										}}	
										else if(msg3.equals("je m arrete")) {
										addBehaviour(new WakerBehaviour(myAgent, 4000) {
											@Override
											public void onWake() {	
												System.out.println("Carrefour : avancez !!!");
												flag3=true; 
											}

											
										});}
											}

						}

						@Override
						public boolean done() {
							
							return flag2& flag3;
						}
					});
					flag=true;
					}
				
				}
				
				
			}
			@Override
			public boolean done() {
				// TODO Auto-generated method stub
				return flag;
			}
			
			private List<AID> chercherServices(Agent myAgent, String type) {
				List<AID> L = new ArrayList<>();
				DFAgentDescription agentDescription=new DFAgentDescription(); 
				ServiceDescription serviceDescription=new ServiceDescription();
				serviceDescription.setType(type); 
				agentDescription.addServices(serviceDescription); 
				try {
					DFAgentDescription[] description =DFService.search(myAgent, agentDescription);
					for(DFAgentDescription dfad:description) {
						L.add(dfad.getName());
					}
				}catch(FIPAException e ) {e.printStackTrace();}
				
				return L;
			}
			
			
		});
		
		
		

}
}

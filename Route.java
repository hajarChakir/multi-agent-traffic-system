package prjtsSMA.appli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;



public class Route extends Agent{

	public String namer;
	int count;
	int index=0;
	int firstd;
	AID sender;
	boolean flag=false;
	List<AID> Lvoitures = new ArrayList<>();
	public Route() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Route(String name) {
		super();
		this.namer=name;
		
	}
	public int calculedistance(int d1,int d2) {
		return Math.abs(d1-d2);
	}
		
	public void setup() 
	{
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(this.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName("route");
		sd.setType("route");
		dfd.addServices(sd);
		
		try
		{
			DFService.register(this,dfd);
		}
		catch(FIPAException e){
			System.err.println("Agent "+getLocalName() +": " + e.getMessage());
		}
		
		if(getLocalName().equals("route1")) {
			
		
		System.out.println("===Bienvenue dans la route A-B=====");
		
		addBehaviour(new Behaviour(){
			@Override
			public void action() {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("recherche des voitures existantes dans route");
				Lvoitures=chercherServices(myAgent,"voiture");
				try {
					for (AID aid:Lvoitures) {
						System.out.println("---"+aid.getName());
					}
				}catch(Exception e) {e.printStackTrace();}
				flag=true;
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
			@Override
			public boolean done() {
				// TODO Auto-generated method stub
				return flag;
			}
		});
		
		//chercher agent voitures !! to aDD
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				
				//recevoir distances initiales
				ACLMessage aclMessage = receive();
				if(aclMessage!=null ) {
					String msg=aclMessage.getContent();
					AID requester = aclMessage.getSender();
					String name = aclMessage.getSender().getName();
					String [] T=name.split("@");
					System.out.println("route1: Bonjour "+T[0] +" votre position est: "+msg);
					int message=Integer.parseInt(msg);
					//fixer la premiere distance du premier agent  voiture cree 
					if(index==0) {
						firstd=message;
						sender=requester;
						
					}
					//comparer les deux distances (Celle du premier agent voiture et la deuxieme)
					else {
						if(calculedistance(message,firstd)<30) {
							
							ACLMessage msg2= new ACLMessage(ACLMessage.REQUEST);
							msg2.addReceiver(sender);
							if(!(sender.getName().split("@")[0]).equals(T[0])){
								System.out.println("route1: Attention "+sender.getName()+" vous rapprochez de "+T[0]);
								msg2.setContent("hh");
								myAgent.send(msg2);

							}
							

						}
						else {System.out.println("route1: vous etes prudent "+sender.getName());}
					}index++;
					//fin de comparaison 
					if(index==2) {
						index=0;
						System.out.println("route 1: Quelle votre nouvelle position ?");
					}
					count++;
				}else block();
				
			}
		});

		}
		
}
	protected void takedown() {
		try {
			DFService.deregister(this);
		}catch(FIPAException e){
			e.printStackTrace();
		}
		
	}
}

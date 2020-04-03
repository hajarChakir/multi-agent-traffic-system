package prjtsSMA.appli;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Voiture extends Agent{
	

	public String agentName;
	public Route route;
	public String typeconduite;
	public int distancecarrfdepart;
	public int vitesse;
	public Hashtable<String, Integer> voitures=new Hashtable<>();
	int d1=10;
	int d2=20;
	private TickerBehaviour b1 = null;
	boolean heho=false;
	boolean heho1=false;
	boolean flag =false;
	boolean flag2 =false;
	
	
	public Voiture() {}
	public Voiture(String name,int distanceinitiale,int vitesse) {
		super();
		this.distancecarrfdepart=distanceinitiale;
		this.vitesse=vitesse;
		this.agentName=name;
			
	}
	public void setup() {
		
		//publication des services de la route avec DFAgentDescription 
		Object[] args= getArguments();
		distancecarrfdepart=(int) args[0];
		vitesse=(int)args[1];
		System.out.println("Creation voiture .."+this.getAID().getName());		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(this.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName("voiture");
		sd.setType("voiture");
		dfd.addServices(sd);
		
		try
		{
			DFService.register(this,dfd);
		}
		catch(FIPAException e){
			System.err.println("Agent "+getLocalName() +": " + e.getMessage());
		}

		//chaque voiture envoie sa position chaque 10s
		addBehaviour(b1=new TickerBehaviour(this, 10000) {

			@Override
			protected void onTick() {
					//le message envoye est de type INFORM qui contient la distance
				    ACLMessage aclMessage =new ACLMessage(ACLMessage.INFORM);
				    aclMessage.addReceiver(new AID("route1",AID.ISLOCALNAME));
				    //voiture1
				    if (getLocalName().equals("voiture1")) {
				    	 d1=d1+10;
					    	 System.out.println(getLocalName()+": Bonjour route 1 , je suis a la position "+d1);
				    		 aclMessage.setContent(Integer.toString(d1));
							 send(aclMessage);
				    	 }
				    //voiture2
				    else {
				    	d2=d2+10;
					    	System.out.println(getLocalName()+": Bonjour route 1 , je suis a la position "+d2);
					    	aclMessage.setContent(Integer.toString(d2));
							 send(aclMessage);
				    }
			}
		});
		
		//reception d'un message d'aupres de la route pour ralentir on utilise un cyclicBehaviour pour 
		addBehaviour(new CyclicBehaviour() {
			public void action(){
				 ACLMessage msgr = receive();
				    if(msgr!=null) {
				    	if (getLocalName().equals("voiture1")) {
					    	System.out.println("voiture1 : OK , je vais relentir!");
					    	vitesse=vitesse-10;
					    	d1=d1+20;
					    	System.out.println("voiture1 : ma vitesse dans la position "+d1+" est "+vitesse);
					    	//envoi de reponse contenant la nouvelle distance
					    	ACLMessage msg2=msgr.createReply();
					    	msg2.setContent(Integer.toString(d1));
					    	send(msg2);
					    
				    	}
				    	else {
				    		System.out.println("voiture2 : OK , je vais relentir!");
					    	vitesse=vitesse-10;
					    	d2=d2+20;
					    	System.out.println("voiture2 : ma vitesse dans la position "+d2+" est "+vitesse);
					    	//envoi de reponse contenant la nouvelle distance
					    	ACLMessage msg3=msgr.createReply();
					    	msg3.setContent(Integer.toString(d2));
					    	send(msg3);
					    	
					    	
				    	}
				    }else {
				    		block();
				    	}
				    	
				    }
		});
		
		
		addBehaviour(new Behaviour() {
			
			//pour terminer le Behaviour on utilise methode done()
			@Override
			public boolean done() {
				
				return heho || heho1;
			}
			
			
			@Override
			public void action() {
				if(d2!=0 & d2>60 ) {
					System.out.println("voiture2 ");
					b1.stop();
					System.out.println("voiture2: je suis arrivee au carrefour B !!!!");
					heho=true;
					System.out.println("========== carrefour=====================");
					ACLMessage aclMessage =new ACLMessage(ACLMessage.REQUEST);
					 aclMessage.addReceiver(new AID("carrefour",AID.ISLOCALNAME));
					 // la voiture envoie un message au carrefour pour demander les routes possibles
					 String msgcarr="Quelles sont les routes possibles";
					 System.out.println(msgcarr);
					 aclMessage.setContent(msgcarr);
					send(aclMessage);
					//utilisation d'un Behaviour qui recoit les messages du Carrefour
					addBehaviour(new Behaviour() {
						
						@Override
						public void action() {
							//reception du message de la part du carrefour
							ACLMessage aclMessage2 = receive();
							if(aclMessage2!=null ) {
								// la voiture recoit un message et reponds avec un message contenant son choix du route 
								if (!(aclMessage2.getContent().equals("arretez"))) {
									String msg=aclMessage2.getContent();
									ACLMessage msg2=aclMessage2.createReply();
							    	msg2.setContent("route2");
							    	
							    	System.out.println("voiture2: je choisis la"+msg2.getContent());
							    	send(msg2);
							    	flag=true;
							    	//si le message recu du carrefour est arretez alors la voiture  doit repondre avec un msg indiquant qu'elle s'arrete
								}else {
									System.out.println("voiture2: D'accord ,je m arrete");
									ACLMessage msg3=aclMessage2.createReply();
							    	msg3.setContent("je m arrete");
							    	send(msg3);
									flag2=true;
									
								}
								
						    	
						    	
							}
							
						}

						@Override
						public boolean done() {
							// TODO Auto-generated method stub
							return flag & flag2;
						}
						
					});
					 
					
				}
				else if(d1!=0 & d1>60) {
					System.out.println("voiture1 ");
					b1.stop();
					System.out.println("voiture1: je suis arrivee au carrefour B !!!!");
					heho1=true;
					System.out.println("========== carrefour=====================");

				}
				
				
			}
		});

	}
//	@SuppressWarnings("unused")
//	private class Carr extends WakerBehaviour{
//		int wakeupDate;
//		private Agent a;
//		
//
//		public Carr(Agent a, int i) {
//			super(a, i);
//			this.a=myAgent;
//			this.wakeupDate=5000;
//		}
//
//		@Override
//		public void onWake() {	
//			System.out.println("========== carrefour=====================");
//			 
//		}
//	}
//	
	protected void takedown() {
		try {
			DFService.deregister(this);
		}catch(FIPAException e){
			e.printStackTrace();
		}
		
	}
	public int getDistancecarrfdepart() {
		return distancecarrfdepart;
	}
	public void setDistancecarrfdepart(int distancecarrfdepart) {
		this.distancecarrfdepart = distancecarrfdepart;
	}



	
}

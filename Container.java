package prjtsSMA.env;


import jade.wrapper.AgentController;


import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import prjtsSMA.appli.Carrefour;
import prjtsSMA.appli.Route;
import prjtsSMA.appli.Voiture;

public class Container {

	public Container() {
		ContainerController container = new Plateform().getAContainer();
		AgentController ac;
		AgentController bc;
		AgentController cc;
		AgentController cd;
		AgentController ce;
		try {
			ac = container.createNewAgent("voiture1", Voiture.class.getName(),
					new Object []{10,50});
					ac.start();
			bc = container.createNewAgent("voiture2", Voiture.class.getName(),
					new Object []{20,50});
					bc.start();
			cc = container.createNewAgent("route1", Route.class.getName(),
							new Object []{});
							cc.start();
			cd = container.createNewAgent("route2", Route.class.getName(),
									new Object []{});
									cd.start();
			ce = container.createNewAgent("carrefour", Carrefour.class.getName(),
											new Object []{});
											ce.start();
				
			
			
			
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	
	}
	
	public static void main(String[] args) { new Container(); }
	

}

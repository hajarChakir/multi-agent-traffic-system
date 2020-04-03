package prjtsSMA.env;

import jade.core.MainContainer;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.ContainerController;

public class Plateform {
	
	private ContainerController MainContainer,Container;
	
	public Plateform() {
		Runtime Rt = Runtime.instance();
		Properties p = new ExtendedProperties();
		p.setProperty("gui", "true");
		ProfileImpl profile = new ProfileImpl(p);
		MainContainer = Rt.createMainContainer(profile);
		profile = new ProfileImpl(false);
		profile.setParameter(ProfileImpl.MAIN_HOST, "localhost"); 
		Container = Rt.createAgentContainer(profile);
	}
	public ContainerController getMainContainer(){ return MainContainer;} 
	
	public ContainerController getAContainer() {
		// TODO Auto-generated method stub
		 return Container;
	}


}
package model;

import java.util.ArrayList;

public class Scheduler {
	
	private ArrayList<Server> servers;
	public static int maxClientsPerServer;
	private Strategy strategy = new ConcreteStrategyTime();
	
	public Scheduler(int maxNoServers, int maxClientsPerServer) {
		Scheduler.maxClientsPerServer = maxClientsPerServer;
		servers = new ArrayList<Server>();
		
		for(int i = 0; i < maxNoServers; i++) {
			Server server = new Server(i+1);
			servers.add(server);
			Thread t = new Thread(server);
			t.setName("Queue " + i);
			t.start();
		}
	}
	
	public void dispatchTask(Client c) throws Exception {
		strategy.addTask(servers, c);
	}
	
	public int getMaximumWaitingTime() {
		int m = 0;
		for(Server s: servers) {
			if(s.getWaitingTime() > m) {
				m = s.getWaitingTime();
			}
		}
		return m;
	}
	
	public ArrayList<Server> getServers() {
		return servers;
	}
}

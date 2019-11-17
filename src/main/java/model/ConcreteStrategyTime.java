package model;

import java.util.List;

public class ConcreteStrategyTime implements Strategy {

	public void addTask(List<Server> servers, Client c) throws Exception {
		Server minTime = null;
		int min = Integer.MAX_VALUE;
		for (Server s : servers) {
			if (s.getClients().size() < Scheduler.maxClientsPerServer) {
				if (s.getWaitingTime() < min) {
					minTime = s;
					min = s.getWaitingTime();
				}
			}
		}
		
		if(min == Integer.MAX_VALUE) {
			throw new Exception("All queues are full!");
		} else {
			minTime.addClient(c);
		}
	}
}

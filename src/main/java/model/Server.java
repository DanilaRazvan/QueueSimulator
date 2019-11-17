package model;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
	private int id;
	private BlockingQueue<Client> clients;
	private AtomicInteger waitingPeriod;
	public static double averageWaitingTime;

	public Server(int id) {
		this.id = id;
		waitingPeriod = new AtomicInteger(0);
		clients = new ArrayBlockingQueue<Client>(Scheduler.maxClientsPerServer);
	}

	public void addClient(Client c) {
		clients.add(c);
		c.setFinishTime(c.getArrivalTime() + c.getProcessingPeriod() + this.waitingPeriod.intValue());
		this.waitingPeriod.set(this.waitingPeriod.intValue() + c.getProcessingPeriod());
	}

	public void run() {
		int noOfClientsProcessed = 0;
		double totalWaitingTime = 0;
		Client c;
		while (true) {
			try {
				c = clients.peek();
				if(c != null) {
					noOfClientsProcessed++;
					totalWaitingTime += c.getFinishTime() - c.getArrivalTime();
					Server.averageWaitingTime = totalWaitingTime / noOfClientsProcessed;
					Thread.sleep(1000 * c.getProcessingPeriod());
					this.waitingPeriod.set(this.waitingPeriod.intValue() - c.getProcessingPeriod());
					clients.poll();	
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public int getSize() {
		return this.clients.size();
	}

	public int getWaitingTime() {
		return this.waitingPeriod.intValue();
	}

	public double getAverageWaitingTime() {
		return Server.averageWaitingTime;
	}
	
	public ArrayList<Client> getClients() {
		ArrayList<Client> cl = new ArrayList<Client>(clients);
		return cl;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Queue number " + this.id + "\n");
		for(Client c: clients) {
			s.append(c.toString());
		}
		return s.toString();
	}

}

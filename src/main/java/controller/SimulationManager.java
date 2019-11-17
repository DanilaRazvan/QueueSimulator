package controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Client;
import model.Scheduler;
import model.Server;
import model.Strategy.SelectionPolicy;
import view.SimulationFrame;

public class SimulationManager extends Application implements Runnable {

	private static DecimalFormat df2 = new DecimalFormat(".##");
	
	// data read from UI
	private static int timeLimit;
	private static int maxProcessingTime;
	private static int minProcessingTime;
	public static int numberOfServers;
	public static int numberOfClients;
	// time period between clients
	private static int minArrTime;
	private static int maxArrTime;
	private SelectionPolicy seletionPolicy = SelectionPolicy.SHORTEST_TIME;

	// entity resposible with queue management and client distribution
	private Scheduler scheduler;
	// frame for displaying simulation
	private static SimulationFrame frame;
	// pool of tasks (client shopping in a store)
	private static ArrayList<Client> generatedClients = new ArrayList<Client>();

	public SimulationManager() {
		scheduler = new Scheduler(numberOfServers, Scheduler.maxClientsPerServer);
		addForm();
	}

	private static void generateNRandomClients(int n) throws Exception {
		int procPer;
		int arrT;
		
		@SuppressWarnings("unused")
		int lastArrT;

		for (int i = 0; i < n; i++) {
			if (maxProcessingTime < minProcessingTime)
				throw new Exception("Max Proc < Min Proc");
			procPer = new Random().nextInt(maxProcessingTime - minProcessingTime + 1) + minProcessingTime;
			
			if (maxArrTime < minArrTime)
				throw new Exception("Max Arrival Time < Min Arrival Time");
			arrT = new Random().nextInt(maxArrTime - minArrTime + 1) + minArrTime;
			
			lastArrT = arrT;
			generatedClients.add(new Client(i, arrT, 0, procPer));
		}
	}

	public void run() {
		StringBuilder sc = new StringBuilder();
		int currentTime = -1;
		int momentMaxNumberOfWaitingClients = 0;
		int servedClients = 0;
		int maxNumborOfWaitingClients = 0;
		int waitingClients = 0;
		
		sc.append("~~~~~~~START~~~~~~~\n\n");
		while (currentTime <= timeLimit) {
			sc.append("~~~~~~~ Moment " + currentTime + " ~~~~~~~\n");
			
			Iterator<Client> it = SimulationManager.generatedClients.iterator();
			while (it.hasNext()) {
				Client c = it.next();
				try {
					if (c.getArrivalTime() == currentTime) {
						scheduler.dispatchTask(c);
						servedClients++;
						it.remove();
						if(servedClients == numberOfClients) {
							timeLimit = currentTime + scheduler.getMaximumWaitingTime();
						}
					}
				} catch (Exception e) {
					if(e.getMessage().compareTo("All queues are full!") == 0) {
						frame.displayMessage(e.getMessage());
					}
				}
			}

			waitingClients = 0;
			for (Server s : scheduler.getServers()) {
				waitingClients += s.getSize();
				sc.append(s);
			}
			
			if (waitingClients > maxNumborOfWaitingClients) {
				maxNumborOfWaitingClients = waitingClients;
				momentMaxNumberOfWaitingClients = currentTime;
			}
			
			String st = new String(sc.toString());
			frame.getLog().appendText(st);
			sc.delete(0, sc.length());

			Platform.runLater(
					() -> {
						frame.displayQueues(scheduler.getServers());
					}
			);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Thread interrupted");
			}
			
			currentTime++;
		}
		
		double averageWaitingTime = 0;
		for(Server s: scheduler.getServers()) {
			averageWaitingTime += s.getAverageWaitingTime();
		}
		
		StringBuilder r = new StringBuilder();
		r.append("\n~~~~~~~END~~~~~~~\n\n");
		r.append("Average waiting time = " + df2.format(averageWaitingTime / numberOfServers) + "\n");
		r.append("Maximum number of waiting clients (" + maxNumborOfWaitingClients + ") was at moment " + momentMaxNumberOfWaitingClients + "\n");
		
		frame.getLog().appendText(r.toString());
		frame.submitB.setDisable(false);
	}
	
	public static void main(String[] args) {
		frame = new SimulationFrame();
		frame.getSubmitB().setOnAction(e -> SimStart());
		Application.launch(args);
	}

	private static void SimStart() {
		ArrayList<TextField> texts = new ArrayList<TextField>();
		texts.addAll(frame.getTexts());
		frame.getLog().clear();

		boolean ok = true;

		for (TextField t : texts) {
			if (t.getText().compareTo("") == 0) {
				ok = false;
			}
		}

		if (ok) {
			SimulationManager.timeLimit = Integer.parseInt(frame.getMaxSimTimeT());
			SimulationManager.maxProcessingTime = Integer.parseInt(frame.getMaxProcTimeT());
			SimulationManager.minProcessingTime = Integer.parseInt(frame.getMinProcTimeT());
			SimulationManager.numberOfServers = Integer.parseInt(frame.getNoOfQueuesT());
			SimulationManager.numberOfClients = Integer.parseInt(frame.getNoOfClientsT());
			Scheduler.maxClientsPerServer = Integer.parseInt(frame.getmaxNoOfClientsPerQueueT());
			SimulationManager.minArrTime = Integer.parseInt(frame.getMinArrTimeIntervalT());
			SimulationManager.maxArrTime = Integer.parseInt(frame.getMaxArrTimeIntervalT());
			try {
				generateNRandomClients(numberOfClients);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (generatedClients != null) {
				frame.submitB.setDisable(true);
				frame.qs = frame.generateTiles();
				frame.queuesBox.getChildren().addAll(frame.qs);
				frame.queuesBox.getChildren().clear();

				SimulationManager s = new SimulationManager();
				Thread t = new Thread(s);
				t.start();
			}

		} else {
			frame.getLog().appendText("Invalid values. Please introduce values in each Textbox");
		}
	}

	@Override
	public void start(Stage mainStage) throws Exception {
		mainStage.setScene(frame.scene);
		mainStage.setTitle("Simulator");
		mainStage.show();
		mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			public void handle(WindowEvent e) {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	private void addForm() {
		ArrayList<TextField> texts = new ArrayList<TextField>();
		texts.addAll(frame.getTexts());

		for (TextField t : texts) {
			t.setTextFormatter(new TextFormatter<String>((Change c) -> {
				String newt = c.getControlNewText();
				// only digits
				if (newt.matches("[0-9]+") || newt.equals("")) {
					return c;
				} else {
					return null;
				}
			}));
		}
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(int timeLimit) {
		SimulationManager.timeLimit = timeLimit;
	}

	public int getMaxProcessingTime() {
		return maxProcessingTime;
	}

	public void setMaxProcessingTime(int maxProcessingTime) {
		SimulationManager.maxProcessingTime = maxProcessingTime;
	}

	public int getMinProcessingTime() {
		return minProcessingTime;
	}

	public void setMinProcessingTime(int minProcessingTime) {
		SimulationManager.minProcessingTime = minProcessingTime;
	}

	public int getNumberOfServers() {
		return numberOfServers;
	}

	public void setNumberOfServers(int numberOfServers) {
		SimulationManager.numberOfServers = numberOfServers;
	}

	public int getNumberOfClients() {
		return numberOfClients;
	}

	public void setNumberOfClients(int numberOfClients) {
		SimulationManager.numberOfClients = numberOfClients;
	}

	public SelectionPolicy getSeletionPolicy() {
		return seletionPolicy;
	}

	public void setSeletionPolicy(SelectionPolicy seletionPolicy) {
		this.seletionPolicy = seletionPolicy;
	}
}

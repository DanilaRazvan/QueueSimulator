package view;

import java.util.ArrayList;

import controller.SimulationManager;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import model.Scheduler;
import model.Server;

public class SimulationFrame {
	
	public Scene scene;
	
	private TextField maxSimTimeT;
	private TextField minArrTimeT;
	private TextField maxArrTimeT;
	private TextField noOfQueuesT;
	private TextField noOfClientsT;
	private TextField minProcTimeT;
	private TextField maxProcTimeT;
	private TextField maxNoOfClientsPerServerT;
	private GridPane textPane = new GridPane();
	private ArrayList<TextField> texts;
	
	private Label maxSimTimeL = new Label("Maximum simulation time");
	private Label minArrTimeL = new Label("Minimum interval between clients");
	private Label maxArrTimeL = new Label("Maximum interval between clients");
	private Label noOfQueuesL = new Label("Number of queues");
	private Label noOfClientsL = new Label("Number of clients");
	private Label minProcTimeL = new Label("Minimum processing time");
	private Label maxProcTimeL = new Label("Maximum processing time");
	private Label maxNoOfClientsPerServerL = new Label("Maximum number of clients per queue");
	
	public Button submitB = new Button("Start simulation");
	
	private TextArea log = new TextArea();
	
	private VBox left = new VBox();
	public VBox queuesBox = new VBox();
	private HBox main = new HBox();
	
	public ArrayList<TilePane> qs = new ArrayList<TilePane>();
	
	public SimulationFrame() {
		maxSimTimeT = new TextField("20");
		maxSimTimeT.setPrefWidth(60);
		textPane.add(maxSimTimeL, 0, 0);
		textPane.add(maxSimTimeT, 1, 0);
		
		minArrTimeT = new TextField("0");
		minArrTimeT.setPrefWidth(60);
		textPane.add(minArrTimeL, 0, 1);
		textPane.add(minArrTimeT, 1, 1);
		
		maxArrTimeT = new TextField("2");
		maxArrTimeT.setPrefWidth(60);
		textPane.add(maxArrTimeL, 0, 2);
		textPane.add(maxArrTimeT, 1, 2);
		
		noOfQueuesT = new TextField("2");
		noOfQueuesT.setPrefWidth(60);
		textPane.add(noOfQueuesL, 0, 3);
		textPane.add(noOfQueuesT, 1, 3);
		
		noOfClientsT = new TextField("7");
		noOfClientsT.setPrefWidth(60);
		textPane.add(noOfClientsL, 0, 4);
		textPane.add(noOfClientsT, 1, 4);
		
		minProcTimeT = new TextField("1");
		minProcTimeT.setPrefWidth(60);
		textPane.add(minProcTimeL, 0, 5);
		textPane.add(minProcTimeT, 1, 5);
		
		maxProcTimeT = new TextField("2");
		maxProcTimeT.setPrefWidth(60);
		textPane.add(maxProcTimeL, 0, 6);
		textPane.add(maxProcTimeT, 1, 6);
		
		maxNoOfClientsPerServerT = new TextField("10");
		maxNoOfClientsPerServerT.setPrefWidth(60);
		textPane.add(maxNoOfClientsPerServerL, 0, 7);
		textPane.add(maxNoOfClientsPerServerT, 1, 7);
		
		texts = new ArrayList<TextField>();
		texts.add(noOfClientsT);
		texts.add(maxArrTimeT);
		texts.add(maxProcTimeT);
		texts.add(maxNoOfClientsPerServerT);
		texts.add(maxSimTimeT);
		texts.add(minArrTimeT);
		texts.add(minProcTimeT);
		texts.add(noOfQueuesT);
		
		textPane.add(submitB, 0, 8, 2, 1);
		GridPane.setHalignment(submitB, HPos.CENTER);
		submitB.setPrefSize(150, 50);
		
		log.prefWidthProperty().bind(textPane.widthProperty().subtract(2));
		log.setPrefHeight(400);
		log.setEditable(false);
		
		left.setPadding(new Insets(5, 5, 5, 5));
		left.getChildren().addAll(textPane, log);
		
		queuesBox.setPadding(new Insets(10, 10, 10, 20));
		queuesBox.setSpacing(10);
		
		Group info = new Group();
		info.getChildren().add(left);
		main.getChildren().addAll(info, queuesBox);
		
		scene = new Scene(main, 700, 660);
		
	}
	
	public ArrayList<TilePane> generateTiles() {
		ArrayList<TilePane> tiles = new ArrayList<TilePane>();
		for(int i = 0; i < SimulationManager.numberOfServers; i++) {
			TilePane tp = new TilePane();
			tp.setPrefColumns(Scheduler.maxClientsPerServer);
			ArrayList<Circle> c = generteCircles();
			tp.getChildren().addAll(c);
			tiles.add(tp);
		}
		
		return tiles;	
	}
	
	public ArrayList<Circle> generteCircles() {
		ArrayList<Circle> circles = new ArrayList<Circle>();
		
		for(int i = 0; i < SimulationManager.numberOfClients; i++) {
			Circle c = new Circle(10);
			c.setFill(Color.BLUE);
			circles.add(c);
		}
		
		return circles;
	}
	
	public ArrayList<TilePane> generateTiles(ArrayList<Server> q) {
		ArrayList<TilePane> tiles = new ArrayList<TilePane>();
		for(int i = 0; i < q.size(); i++) {
			TilePane tp = new TilePane();
			tp.setPrefColumns(q.get(i).getSize());
			ArrayList<Circle> c = generteCircles(q.get(i).getSize());
			tp.getChildren().addAll(c);
			tiles.add(tp);
		}
		
		return tiles;	
	}
	
	public ArrayList<Circle> generteCircles(int n) {
		ArrayList<Circle> circles = new ArrayList<Circle>();
		
		for(int i = 0; i < n; i++) {
			Circle c = new Circle(10);
			c.setFill(Color.BLUE);
			circles.add(c);
		}
		
		return circles;
	}
	
	public void displayQueues(ArrayList<Server> queues) {
		queuesBox.getChildren().clear();
		qs = generateTiles(queues);
		queuesBox.getChildren().addAll(qs);
	}
	
	public void displayMessage(String s) {
		Pane msg = new Pane();
		msg.setPrefSize(100, 100);
		msg.setVisible(true);
		
	}

	public String getMaxSimTimeT() {
		return maxSimTimeT.getText();
	}

	public String getMinArrTimeIntervalT() {
		return minArrTimeT.getText();
	}

	public String getMaxArrTimeIntervalT() {
		return maxArrTimeT.getText();
	}

	public String getNoOfQueuesT() {
		return noOfQueuesT.getText();
	}

	public String getNoOfClientsT() {
		return noOfClientsT.getText();
	}

	public String getMinProcTimeT() {
		return minProcTimeT.getText();
	}

	public String getMaxProcTimeT() {
		return maxProcTimeT.getText();
	}

	public String getmaxNoOfClientsPerQueueT() {
		return maxNoOfClientsPerServerT.getText();
	}

	public ArrayList<TextField> getTexts() {
		return texts;
	}

	public Button getSubmitB() {
		return submitB;
	}

	public TextArea getLog() {
		return log;
	}
}
